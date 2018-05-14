package com.jsull.page;

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

public class GamePageDocument extends JsoupParser {
	
	public static final String ELEMENT_WEEK_TEXT = "All Week";
	public static final String GAME_SCORE_CLASS = "score";
	public static final String GAME_TIME_REGEX = ":\\s*\\d+:\\d{2}[a|p]m.*";
	public static final String GAME_DATE_REGEX = "\\w{6,9}\\s\\w{3}\\s\\d+,\\s\\d{4}";
	public static final String PLAYER_OFFENSE_TABLE_ID = "player_offense";
	public static final String PLAYER_PASS_TABLE_ID = "targets_directions";
	public static final String PLAYER_RUSH_TABLE_ID = "targets_directions";
	
	private List<Comment> comments = new ArrayList<>();
	
	public GamePageDocument(String url) {
		super(url);
		Elements elements = this.doc.getAllElements();
		for (Element e : elements) {
			for (Node n : e.childNodes()) {
				if (n instanceof Comment) {
					//String text = ((Comment) n).getData();
					//if (text.contains(PLAYER_OFFENSE_TABLE_ID))
						comments.add((Comment) n);
				}
			}
		}
	}
	
	public Game getGameDetails() {
		Game game = new Game();
		game.week = getGameWeek();
		Map<String, String> teams = getTeams();
		game.home = teams.get("home");
		game.away = teams.get("away");
		Map<String, Integer> scoreMap = getScore();
		game.homeScore = scoreMap.get("home");
		game.awayScore = scoreMap.get("away");
		game.time = getGameTime();
		game.date = getGameDate();
		return game;
	}

	public int getGameWeek() {
		Elements els = getElementsByAttributeValueStartingWith("data-label", ELEMENT_WEEK_TEXT);
		return getWeekNumFromAttributeValue(els.get(0));
	}
	
	public Map<String, String> getTeams() {
		Map<String, String> teams = new HashMap<>();
		Elements els = getElementsByAttributeValueEqualing("itemprop", "name");
		els = extractElementsByTag(els, "a");
		teams.put("home",  convertFullTeamNameToNickname(els.get(0)));
		teams.put("away", convertFullTeamNameToNickname(els.get(1)));
		return teams;
	}
	
	public Map<String, Integer> getScore() {
		Map<String, Integer> scoreMap = new HashMap<>();
		Elements elements = elementsByClass(GAME_SCORE_CLASS);
		scoreMap.put("home", Integer.valueOf(elements.get(0).text()));
		scoreMap.put("away", Integer.valueOf(elements.get(1).text()));
		return scoreMap;
	}
	
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
	
	private String convertFullTeamNameToNickname(Element e) {
		String full = e.text();
		String[] fullArr = full.split(" ");
		String name = fullArr[fullArr.length - 1].trim();
		return name;
	}
	
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
	
	public List<Player> getPlayers() {
		Game game = getGameDetails();
		List<Player> players = new ArrayList<>();
		Document table = Jsoup.parse(this.comments.get(0).getData());
		Elements elements = table.getElementsByTag("tr");
		String currentTeam = game.away;
		int i=0;
		for (Element e : elements) {
			Player p = null;
			try {
				// method to check name should go here, if not needed, i++ and cont
				p = extractPlayerFromRow(e);
				p.stats.team = currentTeam;
				p.stats.game = game;
			} catch (Exception ex) {
				System.err.println(ex.getMessage());
			}
			if (p != null)
				players.add(p);
			if (i >= 2) {
				if (e.getElementsByTag("td").size() == 0) 
					currentTeam = game.home;
			}
			i++;
		}
		return players;
	}
	
	public String[] getPlayerNameArrFromRow(Element e) {
		String[] nameArr = e.select("a[href]").text().split(" ");
		return nameArr;
	}
	
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
	
	private Elements getAllCellsFromRow(Element e) {
		return e.getElementsByTag("td");
	}
	
	private Player extractPlayerFromRow(Element e) throws Exception {
		Player p = new Player();
		if (!isPlayerRow(e))
			throw new Exception("This row: " + e + " is not a player row.");
		String[] nameArr = e.select("a[href]").text().split(" ");
		p.first = nameArr[0];
		p.last = nameArr[1];
		Elements cells = getAllCellsFromRow(e);
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
				stats.completions = val;
				break;
			case "pass_att":
				stats.passAttempts = val;
				break;
			case "pass_yds":
				stats.passYards = val;
				break;
			case "pass_td":
				stats.passTouchdowns = val;
				break;
			case "pass_int":
				stats.interceptions = val;
				break;
			case "pass_sacked":
				stats.sacks = val;
				break;
			case "pass_sacked_yds":
				stats.sackYards = val;
				break;
			case "pass_long":
				stats.passLong = val;
				break;
			case "rush_Att":
				stats.rushAttempts = val;
				break;
			case "rush_yds":
				stats.rushYards = val;
				break;
			case "rush_td":
				stats.rushTouchdowns = val;
				break;
			case "rush_long":
				stats.rushLong = val;
				break;
			case "targets":
				stats.targets = val;
				break;
			case "rec":
				stats.receptions = val;
				break;
			case "rec_yds":
				stats.recYards = val;
				break;
			case "rec_td":
				stats.recTouchdowns = val;
				break;
			case "rec_long":
				stats.recLong = val;
				break;
			case "fumbles":
				stats.fmbl = val;
				break;
			case "fumbles_lost":
				stats.fl = val;
				break;
			}
		}
		p.stats = stats;
		return p;
	}
	
	public void extractPlayerRushDetails(List<Player> players) {
		Document table = getTableFromCommentsById(PLAYER_RUSH_TABLE_ID);
		Elements rows = getAllRowsFromTable(table);
		for (Element row : rows) {
			if (isPlayerRow(row)) {
				String[] nameArr = getPlayerNameArrFromRow(row);
				System.out.println("\n" + nameArr[0] + " " + nameArr[1]);
				// check if player in set and point, if not continue
				Elements cells= getAllCellsFromRow(row);
				RushDetails rushDetails = new RushDetails();
				for (Element cell : cells) {
					if (!cell.hasText())
						continue;
					if (!isCellValueNumeric(cell))
						continue;
					int n = Integer.parseInt(cell.text());
					Attributes attributes = cell.attributes();
					String data = attributes.get("data-stat");
					System.out.println(data + " " + n);;
					switch (data) {
					case "rush_le":
						break;
					case "rush_yds_le":
						break;
					case "rush_td_le":
						break;
					case "rush_lt":
						break;
					case "rush_yds_lt":
						break;
					case "rush_td_lt":
						break;
					case "rush_lg":
						break;
					case "rush_yds_lg":
						break;
					case "rush_td_lg":
						break;
					case "rush_md":
						break;
					case "rush_yds_md":
						break;
					case "rush_td_md":
						break;
					case "rush_rg":
						break;
					case "rush_yds_rg":
						break;
					case "rush_td_rg":
						break;
					case "rush_rt":
						break;
					case "rush_yds_rt":
						break;
					case "rush_td_rt":
						break;
					case "rush_re":
						break;
					case "rush_yds_re":
						break;
					case "rush_td_re":
						break;
					}
				}
				//p.gameStats.rushDetails = rushDetails;
			}
		}
	}
	
	public void extractPlayerPassDetails(List<Player> players) {
		Document table = getTableFromCommentsById(PLAYER_PASS_TABLE_ID);
		Elements rows = getAllRowsFromTable(table);
		for (Element row : rows) {
			if (isPlayerRow(row)) {
				String[] nameArr = getPlayerNameArrFromRow(row);
				System.out.println("\n" + nameArr[0] + " " + nameArr[1]);
				// check if player in list
				Elements cells = getAllCellsFromRow(row);
				PassDetails passDetails = new PassDetails();
				for (Element cell : cells ) {
					if (!cell.hasText())
						continue;
					if (!isCellValueNumeric(cell))
						continue;
					int n = Integer.parseInt(cell.text());
					Attributes attributes = cell.attributes();
					String data = attributes.get("data-stat");
					System.out.println(data + " " + n);
					switch(data) {
					case "rec_targets_sl":
						break;
					case "rec_catches_sl":
						break;
					case "rec_yds_sl":
						break;
					case "rec_td_sl":
						break;
					case "rec_targets_sm":
						break;
					case "rec_catches_sm":
						break;
					case "rec_yds_sm":
						break;
					case "rec_td_sm":
						break;
					case "rec_targets_sr":
						break;
					case "rec_catches_sr":
						break;
					case "rec_yds_sr":
						break;
					case "rec_td_sr":
						break;
					case "rec_targets_dl":
						break;
					case "rec_catches_dl":
						break;
					case "rec_yds_dl":
						break;
					case "rec_td_dl":
						break;
					case "rec_targets_dm":
						break;
					case "rec_catches_dm":
						break;
					case "rec_yds_dm":
						break;
					case "rec_td_dm":
						break;
					case "rec_targets_dr":
						break;
					case "rec_catches_dr":
						break;
					case "rec_yds_dr":
						break;
					case "rec_td_dr":
						break;
					}
				}
				//p.gameStats.passDetails = passDetails;
			}
		}
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
