package com.jsull.page;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;

import com.jsull.entity.FantasyFormat;
import com.jsull.entity.FantasyWeek;
import com.jsull.entity.Player;
import com.jsull.util.StringUtils;

public class FantasyWeekPage extends Page {
	
	public static final String PLAYER_LIST_URL = "http://rotoguru1.com/cgi-bin/fyday.pl?game=dk";
	public static final String QB_STRING = "Quarterbacks";
	public static final String RB_STRING = "Running Backs";
	public static final String WR_STRING = "Wide Receivers";
	public static final String TE_STRING = "Tight Ends";
	public static final String K_STRING = "Kickers";
	public static final String D_STRING = "Defenses";
	//public static String PLAYER_ROW_XPATH = "//table/tbody/tr";
	public static final String PLAYER_ROW_XPATH = "//table/tbody/tr/td[5]/parent::tr";
	public static final String POSITION_XPATH = "./td/u/b";
	public static final String PLAYER_NAME_XPATH = "./td/a";
	public static final String POINTS_XPATH = "./td[@align='center']/a";
	public static final String SALARY_XPATH = "./td[@align='right']";
	public static final String GAME_SELECT_XPATH = "//select[@name='game']";
	public static final String PAGE_YEAR_XPATH = "//table/tbody/tr/td/font[contains(text(),' ')]";
	public static final String YEAR_SELECT_XPATH = "//select[@name='gameyr']";
	
	public FantasyWeekPage(WebDriver driver) {
		super(driver);
		goTo(PLAYER_LIST_URL);
	}
		
	public Map<String, Player> scrapePlayerData(int startWeek, int numWeeks) {
		int year = 2017; 
		int numYears = 1; // these should be params
		
		Map<String, Player> players = new HashMap<>();
		for (int j=year; j<year + numYears; j++) {
			if (!isPageDisplayingCorrectYear(j))
				clickAppropriateYearInSelect(j);
			for (int i=startWeek; i<startWeek + numWeeks; i++) {
	//			for (FantasyFormat format : FantasyFormat.values())
	//				String game = format.getFormat();
					String game = "DraftKings";
					System.out.println("Scraping for game = " + game);
					clickSelectWithXpathByText(GAME_SELECT_XPATH, game);
					clickByLinkText(getWeekLinkText(i));
					ArrayList<WebElement> rows = getPlayerAndHeadingRows();
					String position = "Quarterbacks";
					for (WebElement row : rows) {
						
						
						if (isHeadingRow(row)) {
							position = getPositionFromHeadingRow(row);
							System.out.println("*******************\nPosition set to: "
									+ position + "\n*******************");
							continue;
						}
						if (position.equals("D") || position.equals("K")) {
							continue;
						}
						
//						try {
//							String player = getPlayerNameFromRow(row);
							double points = getPointsFromRow(row);
							if (points <= 0)
								continue;
							
							double salary = getSalaryFromRow(row);
//							if (validatePlayerNameVal(player)) {
//								if (!player.contains(","))
//									continue;
								String name = getPlayerNameFromRow(row);
								String[] nameArr = StringUtils.getStringArrBySplitter(name, ",");
								String first = nameArr[1].trim();
								String last = nameArr[0].trim();
								String full = first + " " + last;
								System.out.println(String.format("Player: %s, Points: %f, Salary: %f", full, points, salary));								
								Player p = null;
								List<FantasyWeek> fantasyWeeks = null;
								FantasyWeek fantasyWeek = null;
								
								if (players.containsKey(full)) {
									p = players.get(full);
									fantasyWeeks = p.getFantasyWeeks();
									for (FantasyWeek week : fantasyWeeks) {
										if (week.getYear() == j && week.getWeek() == i) {
											fantasyWeek = week;
											break;
										}
									}
								} else {
									p = new Player();
									p.setFirst(first);
									p.setLast(last);
									p.setPosition(position);
									fantasyWeeks = new ArrayList<>();
									fantasyWeek = new FantasyWeek();
									fantasyWeek.setYear(year);
									fantasyWeek.setWeek(i);
									fantasyWeeks.add(fantasyWeek);
									p.setFantasyWeeks(fantasyWeeks);
									players.put(full, p);
								}
								if (game.equals(FantasyFormat.DRAFTKINGS.getFormat()))	// can refactor to another method
									fantasyWeek.setDraftkingsSalary(salary);					// setSalaryForFantasyWeek(game, week, salary)
								else if (game.equals(FantasyFormat.FANDUEL.getFormat()))
									fantasyWeek.setFanduelSalary(salary);
								else if (game.equals(FantasyFormat.YAHOO.getFormat()))
									fantasyWeek.setYahooSalary(salary);
								
//							}				
//						} catch (Exception e) {
//							System.err.println(e + " for row: " + row.getText());
//							try {
//								position = getPositionFromRow(row);
//								if (QB_STRING.equals(position)) {
//									position = "QB";
//								} else if (RB_STRING.equals(position)) {
//									position = "RB";
//								} else if (WR_STRING.equals(position)) {
//									position = "WR";
//								} else if (TE_STRING.equals(position)) {
//									position = "TE";
//								}
//							} catch (Exception ex) {
//								//System.err.println("Row: " + row.getText() + " is not player or position row");
//							}
//						}
				}	
				//}
			}
		}
		return players;
	}
	
	public boolean isHeadingRow(WebElement row) {
		return checkPresenceInElementByXpath(row, POSITION_XPATH);
	}
	
	public String getPositionFromHeadingRow(WebElement row) {
		String position = getTextByXpathFromWebElement(row, POSITION_XPATH);
		switch (position) {
			case QB_STRING:
				return "QB";
			case RB_STRING:
				return "RB";
			case WR_STRING:
				return "WR";
			case TE_STRING:
				return "TE";
			case D_STRING:
				return "D";
			case K_STRING:
				return "K";
			}
		return null;
	}
	
	public boolean isPageDisplayingCorrectYear(int year) {
		String title = getTextByXpath(PAGE_YEAR_XPATH);
		int currentYear = getYearFromFantasyWeekPageTitle(title);
		return year == currentYear;
	}
	
	public static int getYearFromFantasyWeekPageTitle(String title) {
		String[] arr = StringUtils.getStringArrBySplitter(title, "-");
		String yrString = arr[1];
		yrString = StringUtils.stripNonDigits(yrString);
		int year = Integer.parseInt(yrString);
		return year;
	}
	
	public void clickAppropriateYearInSelect(int year) {
		String yrString = Integer.valueOf(year).toString();
		clickSelectWithXpathByTextContaining(YEAR_SELECT_XPATH, yrString);
	}
	
//	public static boolean validatePlayerNameVal(String val) {
//		if (QB_STRING.equals(val) || RB_STRING.equals(val) || WR_STRING.equals(val) || 
//				TE_STRING.equals(val) || "".equals(val) || val.contains(":"))
//			return false;
//		return true;
//	}
	
	public String getPlayerNameFromRow(WebElement element) {
		return getTextByXpathFromWebElement(element, PLAYER_NAME_XPATH);
	}
	
	public double getPointsFromRow(WebElement element) {
		return Double.parseDouble(getTextByXpathFromWebElement(element, POINTS_XPATH));
	}
	
	public double getSalaryFromRow(WebElement element) {
		String text = getTextByXpathFromWebElement(element, SALARY_XPATH);
		return getValFromSalaryString(text);
	}
	
	public double getValFromSalaryString(String salary) {  // string utils
		salary = salary.replace("$", "").replace(",", "");
		if (salary.equals("N/A"))
			return 0;
		return Double.parseDouble(salary);
	}
	
//	public String getPositionFromRow(WebElement element) {
//		return getTextByXpathFromWebElement(element, POSITION_XPATH);
//	}
	
	public ArrayList<WebElement> getPlayerAndHeadingRows() {
		return collectElementsByXpath(PLAYER_ROW_XPATH);
	}
	
	public static String getWeekLinkText(int week) {
		String linkText = "week " + week;
		return linkText;
	}

}
