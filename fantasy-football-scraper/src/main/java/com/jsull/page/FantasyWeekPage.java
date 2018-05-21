package com.jsull.page;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;

import com.jsull.entity.FantasyWeek;
import com.jsull.entity.Player;

public class FantasyWeekPage extends Page {
	
	public static String playerListUrl = "http://rotoguru1.com/cgi-bin/fyday.pl?game=dk";
	public static String QB_STRING = "Quarterbacks";
	public static String RB_STRING = "Running Backs";
	public static String WR_STRING = "Wide Receivers";
	public static String TE_STRING = "Tight Ends";
	public static String playerRowXpath = "//table/tbody/tr";
	public static String positionXpath = "./td/u/b";
	public static String playerNameXpath = "./td/a";
	public static String pointsXpath = "./td[@align='center']/a";
	public static String salaryXpath = "./td[@align='right']";
	public static String selectXpath = "//select[@name='game']";
	public static String draftKingsText = "DraftKings";
	public static String fanduelText = "FanDuel";
	public static String yahooText = "Yahoo DFS";
	public static String[] fantasyGames = {draftKingsText, fanduelText, yahooText};
	
	public FantasyWeekPage(WebDriver driver) {
		super(driver);
		goTo(playerListUrl);
	}
		
	public Map<String, Player> scrapePlayerData(int startWeek, int numWeeks) {	// move methods like these to 'FantasyWeekPage Extraction' class
		int year = 2017; // todo -> allow for different year also throw meaningful exception messages
		Map<String, Player> players = new HashMap<>();
		for (int i=startWeek; i<startWeek + numWeeks; i++) {
			//for (String game : fantasyGames) {
			String game = "DraftKings";
				clickSelectWithXpathByText(selectXpath, game);
				clickByLinkText(getWeekLinkText(i));
				ArrayList<WebElement> rows = getPlayerRows();
				String position = "Quarterbacks";
				for (WebElement row : rows) {
					try {
						String player = getPlayerNameFromRow(row);
						double points = getPointsFromRow(row);
						if (points <= 0)
							continue;
						double salary = getSalaryFromRow(row);
						if (validatePlayerNameVal(player)) {
							if (!player.contains(","))
								continue;
							String[] nameArr = player.split(",");
							String first = nameArr[1].trim();
							String last = nameArr[0].trim();
							Player p = new Player();
							p.setFirst(first);
							p.setLast(last);
							p.setPosition(position);
							String full = first + " " + last;
							if (players.get(full) != null)
								p = players.get(full);
							List<FantasyWeek> fantasyWeeks = null;
							if (players.get(full) == null) {
								fantasyWeeks = new ArrayList<>();
							} else {
								fantasyWeeks = p.getFantasyWeeks();
							}
							//set fantasy week
							FantasyWeek fantasyWeek = null;
							if (fantasyWeeks != null)
								for (FantasyWeek week : fantasyWeeks) {
									if (week.getYear() == year && week.getWeek() == i) {
										fantasyWeek = week;
										break;
									}
								}
							if (fantasyWeek == null) {
								fantasyWeek = new FantasyWeek();
								fantasyWeek.setYear(year);
								fantasyWeek.setWeek(i);
								fantasyWeeks.add(fantasyWeek);
							}
							if (game.equals(draftKingsText))
								fantasyWeek.setDraftkingsSalary(salary);
							else if (game.equals(fanduelText))
								fantasyWeek.setFanduelSalary(salary);
							else if (game.equals(yahooText))
								fantasyWeek.setYahooSalary(salary);
							
							if (players.get(full) == null) {
								p.setFantasyWeeks(fantasyWeeks);
								players.put(first + " " + last, p);
							} 
						}				
					} catch (Exception e) {
						System.err.println(e + " for row: " + row.getText());
						try {
							position = getPositionFromRow(row);
							if (QB_STRING.equals(position)) {
								position = "QB";
							} else if (RB_STRING.equals(position)) {
								position = "RB";
							} else if (WR_STRING.equals(position)) {
								position = "WR";
							} else if (TE_STRING.equals(position)) {
								position = "TE";
							}
						} catch (Exception ex) {
							//System.err.println("Row: " + row.getText() + " is not player or position row");
						}
					}
			}	
			//}
		}
		return players;
	}
	
	public static boolean validatePlayerNameVal(String val) {
		if (QB_STRING.equals(val) || RB_STRING.equals(val) || WR_STRING.equals(val) || 
				TE_STRING.equals(val) || "".equals(val) || val.contains(":"))
			return false;
		return true;
	}
	
	public String getPlayerNameFromRow(WebElement element) {
		return getTextByXpathFromWebElement(element, playerNameXpath);
	}
	
	public double getPointsFromRow(WebElement element) {
		return Double.parseDouble(getTextByXpathFromWebElement(element, pointsXpath));
	}
	
	public double getSalaryFromRow(WebElement element) {
		String text = getTextByXpathFromWebElement(element, salaryXpath);
		return getValFromSalaryString(text);
	}
	
	public double getValFromSalaryString(String salary) {  // string utils
		salary = salary.replace("$", "").replace(",", "");
		return Double.parseDouble(salary);
	}
	
	public String getPositionFromRow(WebElement element) {
		return getTextByXpathFromWebElement(element, positionXpath);
	}
	
	public ArrayList<WebElement> getPlayerRows() {
		return collectElementsByXpath(playerRowXpath);
	}
	
	public static String getWeekLinkText(int week) {
		String linkText = "week " + week;
		return linkText;
	}

}
