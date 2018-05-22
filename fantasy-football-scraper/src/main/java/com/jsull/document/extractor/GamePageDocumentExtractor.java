package com.jsull.document.extractor;

import java.time.LocalDate;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeFormatterBuilder;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Attribute;
import org.jsoup.nodes.Attributes;
import org.jsoup.nodes.Comment;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.nodes.Node;
import org.jsoup.select.Elements;

import com.jsull.document.JsoupExtractor;
import com.jsull.entity.Game;
import com.jsull.entity.GameStats;
import com.jsull.entity.PassDetails;
import com.jsull.entity.RushDetails;

public class GamePageDocumentExtractor extends JsoupExtractor {
	
	public static final String ELEMENT_WEEK_TEXT = "All Week";
	public static final String TEAM_NAME_ATTRIBUTE_KEY = "itemprop";
	public static final String TEAM_NAME_ATTRIBUTE_VALUE = "name";
	public static final String GAME_SCORE_CLASS = "score";
	public static final String GAME_TIME_REGEX = ":\\s*\\d+:\\d{2}[a|p]m.*";
	public static final String GAME_DATE_REGEX = "\\w{6,9}\\s\\w{3}\\s\\d+,\\s\\d{4}";
	public static final String PLAYER_OFFENSE_TABLE_ID = "player_offense";
	public static final String PLAYER_PASS_TABLE_ID = "targets_directions";
	public static final String PLAYER_RUSH_TABLE_ID = "targets_directions";
	
	private List<Comment> comments = new ArrayList<>();
	
	public GamePageDocumentExtractor(String url) {
		super(url);
		Elements elements = this.doc.getAllElements();
		for (Element e : elements) {
			for (Node n : e.childNodes()) {
				if (n instanceof Comment) {
					comments.add((Comment) n);
				}
			}
		}
	}
	
	// can remove
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

	// can remove
	public int getGameWeek() {
		Elements els = getElementsByAttributeValueStartingWith("data-label", ELEMENT_WEEK_TEXT);
		return getWeekNumFromAttributeValue(els.get(0));
	}
	
	public Elements getGameWeekElements() {
		Elements els = getElementsByAttributeValueStartingWith("data-label", ELEMENT_WEEK_TEXT);
		return els;
	}
	
	// can remove
	public Map<String, String> getTeams() {
		Map<String, String> teams = new HashMap<>();
		Elements els = getElementsByAttributeValueEqualing("itemprop", "name");
		els = filterElementsByTag(els, "a");
		teams.put("home",  convertFullTeamNameToNickname(els.get(0)));
		teams.put("away", convertFullTeamNameToNickname(els.get(1)));
		return teams;
	}
	
	public Elements extractTeamLinks() {
		Elements elements = 
				getElementsByAttributeValueEqualing(TEAM_NAME_ATTRIBUTE_KEY, TEAM_NAME_ATTRIBUTE_VALUE);
		elements = filterElementsByTag(elements, "a");
		return elements;
	}
	
	// can remove
	public Map<String, Integer> getScore() {
		Map<String, Integer> scoreMap = new HashMap<>();
		Elements elements = elementsByClass(GAME_SCORE_CLASS);
		scoreMap.put("home", Integer.valueOf(elements.get(0).text()));
		scoreMap.put("away", Integer.valueOf(elements.get(1).text()));
		return scoreMap;
	}
	
	public Elements extractGameScoreElements() {
		Elements scoreElements = elementsByClass(GAME_SCORE_CLASS);
		return scoreElements;
	}
	
	// can remove
	public LocalTime getGameTime() {
		Elements els = getElementsWithOwnTextMatchingRegex(GAME_TIME_REGEX);
		try {
			LocalTime time = convertElementTextToTime(els.get(0).text());
			return time;
		} catch (Exception e) {
			e.printStackTrace();
			return null;
		}
	}
	
	public Element getGameTimeElement() {
		Elements elements = getElementsWithOwnTextMatchingRegex(GAME_TIME_REGEX);
		Element element = elements.get(0);
		return element;
	}
	
	// can remove
	public LocalDate getGameDate() {
		Elements els = getElementsWithOwnTextMatchingRegex(GAME_DATE_REGEX);
		try {
			LocalDate date = convertElementTextToDate(els.get(0).text());
			return date;
		} catch (Exception e) {
			e.printStackTrace();
			return null;
		}
	}
	
	public Element getGameDateElement() {
		Elements elements = getElementsWithOwnTextMatchingRegex(GAME_DATE_REGEX);
		Element element = elements.get(0);
		return element;
	}
	
	// can remove
	private String convertFullTeamNameToNickname(Element e) {
		String full = e.text();
		String[] fullArr = full.split(" ");
		String name = fullArr[fullArr.length - 1].trim();
		return name;
	}
	
	// can remove
	private int getWeekNumFromAttributeValue(Element e) {
		Attributes attributes = getAttributesBelongingTo(e);
		for (Attribute a : attributes) {
			if (a.getKey().equals("data-label")) {
				String val = a.getValue();
				String[] valArr = val.split(" ");
				return Integer.parseInt(valArr[2].trim());
			}
		}
		return -1;
	}
	
	// can remove
	private LocalTime convertElementTextToTime(String text) throws Exception {
		text = text.replace("Start Time: ",  "").trim();
		if (!text.contains(":"))
			throw new Exception(String.format("The text: %s is invalid and can't be converted to a time.", text));
		text = text.replace("am",  "AM").replaceAll("pm", "PM");
		DateTimeFormatter fmt = new DateTimeFormatterBuilder()
				.parseCaseInsensitive()
				.appendPattern("h:mma")
				.toFormatter(Locale.US);
		LocalTime time = LocalTime.parse(text, fmt);
		return time;
	}
	
	// can remove
	private LocalDate convertElementTextToDate(String date) throws Exception {
		if (date.contains(":"))
			throw new Exception(String.format("The text: %s is invalid and can't be converted to a date.", date));
		date = date.substring(date.indexOf(" ") + 1);
		DateTimeFormatter fmt = new DateTimeFormatterBuilder()
				.parseCaseInsensitive()
				.appendPattern("MMM d, uuuu")
				.toFormatter(Locale.US);
		LocalDate d = LocalDate.parse(date, fmt);
		return d;
	}
	
	// can remove
	public Map<String, GameStats> getGameStats() {
		Map<String, GameStats> map = new HashMap<>();
		Document table = Jsoup.parse(this.comments.get(0).getData());
		Elements rows = table.getElementsByTag("tr");
		Map<String, String> teams = getTeams();
		String currentTeam = teams.get("away");
		int i=0;
		for (Element row : rows) {
			String name = null;
			GameStats gameStats = null;
			try {
				name = getPlayerNameFromRow(row);
				gameStats = getGameStatsFromRow(row);
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
	
	public Elements extractGameStatsRows() {
		Document table = Jsoup.parse(this.comments.get(0).getData());
		Elements rows = table.getElementsByTag("tr");
		return rows;
	}
	
	// can remove
	public String getPlayerNameFromRow(Element e) {
		String[] nameArr = e.select("a[href]").text().split(" ");
		return nameArr[0] + " " + nameArr[1];
	}
	
	// can remove
	private boolean isPlayerRow(Element e) {
		Elements cells = e.getElementsByTag("td");
		String[] nameArr = e.select("a[href]").text().split(" ");
		if (cells.size() == 0 || nameArr.length != 2)
			return false;
		return true;
	}
	
	private Elements getAllRowsFromTable(Element e) {
		return e.getElementsByTag("tr");
	}
	
	// pick up here -> this method can stay
	private Elements getAllCellsFromRow(Element row) {
		return row.getElementsByTag("td");
	}
	
	// can remove
	private GameStats getGameStatsFromRow(Element row) throws Exception { 
		if (!isPlayerRow(row))
			throw new Exception("This row: " + row + " is not a player row.");
		Elements cells = getAllCellsFromRow(row);
		GameStats stats = new GameStats();
		for (Element c : cells) {
			Attributes a = c.attributes();
			String data = a.get("data-stat");
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
	
	public Map<String, RushDetails> extractPlayerRushDetails() {
		Document table = getTableFromCommentsById(PLAYER_RUSH_TABLE_ID);
		Elements rows = getAllRowsFromTable(table);
		Map<String, RushDetails> map = new HashMap<>();
		for (Element row : rows) {
			if (isPlayerRow(row)) {
				String name = getPlayerNameFromRow(row);
				RushDetails rushDetails = new RushDetails();
				Elements cells= getAllCellsFromRow(row);
				for (Element cell : cells) {
					if (!cell.hasText())
						continue;
					if (!isCellValueNumeric(cell))
						continue;
					int val = Integer.parseInt(cell.text());
					Attributes attributes = cell.attributes();
					String data = attributes.get("data-stat");
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
	
	public Map<String, PassDetails> extractPlayerPassDetails() {
		Document table = getTableFromCommentsById(PLAYER_PASS_TABLE_ID);
		Elements rows = getAllRowsFromTable(table);
		Map<String, PassDetails> map = new HashMap<>();
		for (Element row : rows) {
			if (isPlayerRow(row)) {
				String name = getPlayerNameFromRow(row);
				PassDetails passDetails = new PassDetails();
				Elements cells = getAllCellsFromRow(row);
				for (Element cell : cells ) {
					if (!cell.hasText())
						continue;
					if (!isCellValueNumeric(cell))
						continue;
					int val = Integer.parseInt(cell.text());
					Attributes attributes = cell.attributes();
					String data = attributes.get("data-stat");
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
	
	private boolean isCellValueNumeric(Element e) {
		try {
			Integer.parseInt(e.text());
			return true;
		} catch (NumberFormatException ex) {
			return false;
		}
	}
	
	private Document getTableFromCommentsById(String id) {
		for (Comment c : this.comments) {
			String data = c.getData();
			if (data.contains(id)) {
				return Jsoup.parse(data);
			}
		}
		return null;
	}
}
