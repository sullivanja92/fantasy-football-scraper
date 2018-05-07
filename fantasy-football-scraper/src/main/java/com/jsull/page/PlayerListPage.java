package com.jsull.page;

import java.util.ArrayList;
import java.util.HashSet;

import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;

import com.jsull.entity.Player;

public class PlayerListPage extends Page {
	
	public static String playerListUrl = "http://rotoguru1.com/cgi-bin/fyday.pl?game=dk";
	public static String QB_STRING = "Quarterbacks";
	public static String RB_STRING = "Running Backs";
	public static String WR_STRING = "Wide Receivers";
	public static String TE_STRING = "Tight Ends";
	public static String playerRowXpath = "//table/tbody/tr";
	public static String positionXpath = "./td/u/b";
	public static String playerNameXpath = "./td/a";
	
	public PlayerListPage(WebDriver driver) {
		super(driver);
		goTo(playerListUrl);
	}
		
	public HashSet<Player> scrapePlayerDataByWeek(int startWeek, int numWeeks) {
		HashSet<Player> players = new HashSet<>();
		for (int i=startWeek; i<startWeek + numWeeks; i++) {
			clickByLinkText(getWeekLinkText(i));
			ArrayList<WebElement> rows = getPlayerRows();
			String position = "Quarterbacks";
			for (WebElement row : rows) {
				try {
					String player = getPlayerNameFromRow(row);
					if (validatePlayerNameVal(player)) {
						String[] nameArr = player.split(",");
						String first = nameArr[1].trim();
						String last = nameArr[0].trim();
						Player p = new Player();
						p.setFirst(first);
						p.setLast(last);
						p.setPosition(position);
						if (!players.contains(p)) {
							players.add(p);
							//System.out.println(p.toString());
						}
					}				
				} catch (Exception e) {
					try {
						position = getPositionFromRow(row);
						System.out.println("position = " + position);
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
						System.err.println("Row is not player or position row");
					}
				}
			}
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
