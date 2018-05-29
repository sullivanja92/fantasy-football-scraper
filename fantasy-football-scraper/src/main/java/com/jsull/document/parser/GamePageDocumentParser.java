package com.jsull.document.parser;

import java.time.LocalDate;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeFormatterBuilder;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import com.jsull.document.extractor.GamePageDocumentExtractor;
import com.jsull.entity.Game;
import com.jsull.entity.GameStats;
import com.jsull.entity.PassDetails;
import com.jsull.entity.RushDetails;
import com.jsull.entity.SnapDetails;
import com.jsull.util.StringUtils;

/**
 * 
 * @author Josh Sullivan
 *
 */
public class GamePageDocumentParser {
	
	//private static final Logger log = LogManager.getLogManager().getLogger(GamePageDocumentParser.class.getName());
	
	private GamePageDocumentExtractor extractor;
	
	public static final String GAME_WEEK_LABEL = "data-label";
	public static final String PLAYER_NAME_QUERY = "a[href]";
	public static final String GAME_STATS_LABEL = "data-stat";
	
	public GamePageDocumentParser(String url) {
		extractor = new GamePageDocumentExtractor(url);
	}
	
	public Game getGame() {
		Game game = new Game();
		game.setWeek(getGameWeek());
		Map<String, String> teams = getTeams();
		game.setHome(teams.get("home"));
		game.setAway(teams.get("away"));
		Map<String, Integer> scoreMap = getScore();
		game.setHomeScore(scoreMap.get("home"));
		game.setAwayScore(scoreMap.get("away"));
		game.setTime(getGameTime());
		game.setDate(getGameDate());
		return game;
	}
	
	public int getGameWeek() {
		Elements els = this.extractor.getGameWeekElements();
		String data = this.extractor.attributeValueFromElement(els.get(0), GAME_WEEK_LABEL);
		return parseWeekNumFromAttributeValue(data);
	}
	
	private static int parseWeekNumFromAttributeValue(String data) {
		String[] valArr = StringUtils.getStringArrBySplitter(data, " ");
		String numStr = valArr[2].trim();
		return Integer.parseInt(numStr);
	}
	
	public Map<String, String> getTeams() {
		Map<String, String> teams = new HashMap<>();
		Elements teamLinks = this.extractor.extractTeamLinks();
		teams.put("home",  convertFullTeamNameToNickname(teamLinks.get(0)));
		teams.put("away", convertFullTeamNameToNickname(teamLinks.get(1)));
		return teams;
	}
	
	private static String convertFullTeamNameToNickname(Element e) {
		String full = e.text();
		String[] fullArr = StringUtils.getStringArrBySplitter(full, " ");
		String name = fullArr[fullArr.length - 1].trim();
		return name;
	}
	
	public Map<String, Integer> getScore() {
		Map<String, Integer> scoreMap = new HashMap<>();
		Elements elements = this.extractor.extractGameScoreElements();
		scoreMap.put("home", Integer.valueOf(elements.get(0).text()));
		scoreMap.put("away", Integer.valueOf(elements.get(1).text()));
		return scoreMap;
	}
	
	public LocalTime getGameTime() {
		Element element = this.extractor.getGameTimeElement();
		String text = element.text();
		LocalTime time = parseTimeFromString(text);
		return time;
	}
	
	public static LocalTime parseTimeFromString(String text) {
		if (!text.contains(":"))
			return null;
		text = normalizeTimeString(text);
		DateTimeFormatter fmt = new DateTimeFormatterBuilder()
				.parseCaseInsensitive()
				.appendPattern("h:mma")
				.toFormatter(Locale.US);
		LocalTime time = LocalTime.parse(text, fmt);
		return time;
	}
	
	public static String normalizeTimeString(String text) {
		text = 
			text.replace("Start Time: ",  "")
			.replace("am", "AM")
			.replace("pm", "PM")
			.trim();
		return text;
	}
	
	public LocalDate getGameDate() {
		Element element = this.extractor.getGameDateElement();
		String text = element.text();
		LocalDate date = parseDateFromString(text);
		return date;
	}
	
	public static LocalDate parseDateFromString(String date) {
		if (date.contains(":"))
			return null;
		date = removeWeekDayFromDateString(date);
		DateTimeFormatter fmt = new DateTimeFormatterBuilder()
				.parseCaseInsensitive()
				.appendPattern("MMM d, uuuu")
				.toFormatter(Locale.US);
		LocalDate d = LocalDate.parse(date, fmt);
		return d;
	}
	
	public static String removeWeekDayFromDateString(String date) {
		date = date.substring(date.indexOf(" ") + 1);
		return date;
	}
	
	public Map<String, GameStats> getGameStats() {
		Map<String, GameStats> map = new HashMap<>();
		Elements rows = this.extractor.extractGameStatsRows();
		Map<String, String> teams = getTeams();
		String currentTeam = teams.get("away");
		int i=0;
		for (Element row : rows) {
			String name = null;
			GameStats gameStats = null;
			try {
				name = getPlayerNameFromRow(row);
				gameStats = getGameStatsFromRow(row);
				gameStats.setTeam(currentTeam);
			} catch (Exception ex) {
				System.err.println(ex.getMessage());
			}
			if (name != null && gameStats != null)
				map.put(name, gameStats);
			if (i >= 2) {
				if (row.getElementsByTag("td").size() == 0) 
					currentTeam = teams.get("home");
			}
			i++;
		}
		return map;
	}
	
	public String getPlayerNameFromRow(Element e) {
		Elements elements = this.extractor.elementsFromElementByQuery(e, PLAYER_NAME_QUERY);
		String[] nameArr = getNameArrFromLinks(elements);
		return nameArr[0] + " " + nameArr[1];
	}
	
	public static String[] getNameArrFromLinks(Elements elements) {
		String text = elements.text();
		String[] nameArr = StringUtils.getStringArrBySplitter(text, " ");
		return nameArr;
	}
	
	private GameStats getGameStatsFromRow(Element row) throws Exception { 
		if (!isPlayerRow(row))
			throw new Exception("This row: " + row + " is not a player row.");
		Elements cells = this.extractor.extractAllCellsFromRow(row);
		GameStats stats = new GameStats();
		for (Element c : cells) {
			String data = 
					this.extractor.attributeFromElementByName(c, GAME_STATS_LABEL).getValue();
			int val = 0;
			try {
				val = Integer.parseInt(c.text());
			} catch (NumberFormatException ex) {
				continue;
			}
			switch (data) {
				case "pass_cmp" :
					stats.setPassCompletions(val);
					break;
				case "pass_att":
					stats.setPassAttempts(val);
					break;
				case "pass_yds":
					stats.setPassYards(val);
					break;
				case "pass_td":
					stats.setPassTouchdowns(val);
					break;
				case "pass_int":
					stats.setInterceptions(val);
					break;
				case "pass_sacked":
					stats.setSacksTaken(val);
					break;
				case "pass_sacked_yds":
					stats.setSackYards(val);
					break;
				case "pass_long":
					stats.setPassLong(val);
					break;
				case "rush_Att":
					stats.setRushAttempts(val);
					break;
				case "rush_yds":
					stats.setRushYards(val);
					break;
				case "rush_td":
					stats.setRushTouchdowns(val);
					break;
				case "rush_long":
					stats.setRushLong(val);
					break;
				case "targets":
					stats.setTargets(val);
					break;
				case "rec":
					stats.setReceptions(val);
					break;
				case "rec_yds":
					stats.setReceptionYards(val);
					break;
				case "rec_td":
					stats.setReceptionTouchdowns(val);
					break;
				case "rec_long":
					stats.setReceptionLong(val);
					break;
				case "fumbles":
					stats.setFumbles(val);
					break;
				case "fumbles_lost":
					stats.setFumblesLost(val);
					break;
			}
		}
		return stats;
	}
	
	private boolean isPlayerRow(Element e) {
		Elements cells = e.getElementsByTag("td");
		Elements elements = this.extractor.elementsFromElementByQuery(e, PLAYER_NAME_QUERY);
		String[] nameArr = getNameArrFromLinks(elements);
		if (cells.size() == 0 || nameArr.length != 2)
			return false;
		return true;
	}
	

	
	public Map<String, RushDetails> extractPlayerRushDetails() {
		Elements rows = this.extractor.extractRushDetailsTableRows();
		Map<String, RushDetails> map = new HashMap<>();
		for (Element row : rows) {
			if (isPlayerRow(row)) {
				String name = getPlayerNameFromRow(row);
				RushDetails rushDetails = new RushDetails();
				Elements cells= this.extractor.extractAllCellsFromRow(row);
				for (Element cell : cells) {
					if (!cellIsValid(cell))
						continue;
					int val = Integer.parseInt(cell.text());
					String data = 
							this.extractor.attributeFromElementByName(cell, GAME_STATS_LABEL).getValue();
					switch (data) {
						case "rush_le":
							rushDetails.setRushAttLeftEnd(val);
							break;
						case "rush_yds_le":
							rushDetails.setRushYardsLeftEnd(val);
							break;
						case "rush_td_le":
							rushDetails.setRushTdLeftEnd(val);
							break;
						case "rush_lt":
							rushDetails.setRushAttLeftTackle(val);
							break;
						case "rush_yds_lt":
							rushDetails.setRushYardsLeftTackle(val);
							break;
						case "rush_td_lt":
							rushDetails.setRushTdLeftTackle(val);
							break;
						case "rush_lg":
							rushDetails.setRushAttLeftGuard(val);
							break;
						case "rush_yds_lg":
							rushDetails.setRushYardsLeftGuard(val);
							break;
						case "rush_td_lg":
							rushDetails.setRushTdLeftGuard(val);
							break;
						case "rush_md":
							rushDetails.setRushAttMid(val);
							break;
						case "rush_yds_md":
							rushDetails.setRushYardsMid(val);
							break;
						case "rush_td_md":
							rushDetails.setRushTdMid(val);
							break;
						case "rush_rg":
							rushDetails.setRushAttRightGuard(val);
							break;
						case "rush_yds_rg":
							rushDetails.setRushYardsRightGuard(val);
							break;
						case "rush_td_rg":
							rushDetails.setRushTdRightGuard(val);
							break;
						case "rush_rt":
							rushDetails.setRushAttRightTackle(val);
							break;
						case "rush_yds_rt":
							rushDetails.setRushYardsRightTackle(val);
							break;
						case "rush_td_rt":
							rushDetails.setRushTdRightTackle(val);
							break;
						case "rush_re":
							rushDetails.setRushAttRightEnd(val);
							break;
						case "rush_yds_re":
							rushDetails.setRushYardsRightEnd(val);
							break;
						case "rush_td_re":
							rushDetails.setRushTdRightEnd(val);
							break;
					}
				}
				map.put(name, rushDetails);
			}
		}
		return map;
	}
	
	public static boolean cellIsValid(Element cell) {
		return cell.hasText() && isCellValueNumeric(cell);
	}
	
	public static boolean isCellValueNumeric(Element e) {
		try {
			Integer.parseInt(e.text());
			return true;
		} catch (NumberFormatException ex) {
			return false;
		}
	}
	
	public Map<String, PassDetails> extractPlayerPassDetails() {
		Elements rows = this.extractor.extractPassDetailsTableRows();
		Map<String, PassDetails> map = new HashMap<>();
		for (Element row : rows) {
			if (isPlayerRow(row)) {
				String name = getPlayerNameFromRow(row);
				PassDetails passDetails = new PassDetails();
				Elements cells = this.extractor.extractAllCellsFromRow(row);
				for (Element cell : cells ) {
					if (!cellIsValid(cell))
						continue;
					int val = Integer.parseInt(cell.text());
					String data = 
							this.extractor.attributeFromElementByName(cell, GAME_STATS_LABEL).getValue();
					switch(data) {
						case "rec_targets_sl":
							passDetails.setRecTargetsShortLeft(val);
							break;
						case "rec_catches_sl":
							passDetails.setRecCatchesShortLeft(val);
							break;
						case "rec_yds_sl":
							passDetails.setRecYardsShortLeft(val);
							break;
						case "rec_td_sl":
							passDetails.setRecTdShortLeft(val);
							break;
						case "rec_targets_sm":
							passDetails.setRecTargetsShortMiddle(val);
							break;
						case "rec_catches_sm":
							passDetails.setRecCatchesShortMiddle(val);
							break;
						case "rec_yds_sm":
							passDetails.setRecYardsShortMiddle(val);
							break;
						case "rec_td_sm":
							passDetails.setRecTdShortMiddle(val);
							break;
						case "rec_targets_sr":
							passDetails.setRecTargetsShortRight(val);
							break;
						case "rec_catches_sr":
							passDetails.setRecCatchesShortRight(val);
							break;
						case "rec_yds_sr":
							passDetails.setRecYardsShortRight(val);
							break;
						case "rec_td_sr":
							passDetails.setRecTdShortRight(val);
							break;
						case "rec_targets_dl":
							passDetails.setRecTargetsDeepLeft(val);
							break;
						case "rec_catches_dl":
							passDetails.setRecCatchesDeepLeft(val);
							break;
						case "rec_yds_dl":
							passDetails.setRecYardsDeepLeft(val);
							break;
						case "rec_td_dl":
							passDetails.setRecTdDeepLeft(val);
							break;
						case "rec_targets_dm":
							passDetails.setRecTargetsDeepMiddle(val);
							break;
						case "rec_catches_dm":
							passDetails.setRecCatchesDeepMiddle(val);
							break;
						case "rec_yds_dm":
							passDetails.setRecYardsDeepMiddle(val);
							break;
						case "rec_td_dm":
							passDetails.setRecTdDeepMiddle(val);
							break;
						case "rec_targets_dr":
							passDetails.setRecTargetsDeepRight(val);
							break;
						case "rec_catches_dr":
							passDetails.setRecCatchesDeepRight(val);
							break;
						case "rec_yds_dr":
							passDetails.setRecYardsDeepRight(val);
							break;
						case "rec_td_dr":
							passDetails.setRecTdDeepRight(val);
							break;
					}
				}
				map.put(name, passDetails);
			}
		}
		return map;
	}
	
	/**
	 * 
	 * @return
	 */
	public Map<String, SnapDetails> extractSnapDetails() {
		Elements rows = this.extractor.extractSnapDetailsTableRows();
		Map<String, SnapDetails> map = new HashMap<>();
		for (Element row : rows) {
			String name = getPlayerNameFromRow(row);
			Element snapNumEl = this.extractor.extractSnapNumCellFromRow(row);
			int numSnaps = Integer.parseInt(snapNumEl.text());
			if (numSnaps <= 0)
				continue;
			Element snapPctEl = this.extractor.extractSnapPercentageCellFromRow(row);
			String snapPctText = snapPctEl.text();
			int snapPercent = StringUtils.parsePctFromString(snapPctText);
			SnapDetails snapDetails = new SnapDetails();
			snapDetails.setNumSnaps(numSnaps);
			snapDetails.setSnapPercentage(snapPercent);
			map.put(name, snapDetails);
		}
		return map;
	}
}
