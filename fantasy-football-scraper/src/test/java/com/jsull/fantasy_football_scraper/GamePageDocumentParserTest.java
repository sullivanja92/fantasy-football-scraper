package com.jsull.fantasy_football_scraper;

import java.time.LocalDate;
import java.time.LocalTime;
import java.time.Month;
import java.util.Map;

import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;

import com.jsull.document.parser.GamePageDocumentParser;

public class GamePageDocumentParserTest {
	
	@BeforeClass
	public static void beforeTest() {
		System.out.println("Beginning GamePageDocumentParser test.");
	}

	@Test
	public void getTeamsTest() {
		String url = "https://www.pro-football-reference.com/boxscores/201709100gnb.htm";
		GamePageDocumentParser parser = new GamePageDocumentParser(url);
		Map<String, String> teams = parser.getTeams();
		assert(teams.get("home").equals("Packers") && teams.get("away").equals("Seahawks"));
	}
	
	@Test
	public void getScoreTest() {
		String url = "https://www.pro-football-reference.com/boxscores/201710080det.htm";
		GamePageDocumentParser parser = new GamePageDocumentParser(url);
		Map<String, Integer> scoreMap = parser.getScore();
		assert(scoreMap.get("home") == 24 && scoreMap.get("away") == 27);
	}
	
	@Test
	public void convertElementTextToTimeTest() {
		String text = "Start Time: 1:00pm";
		LocalTime time = GamePageDocumentParser.parseTimeFromString(text);
		LocalTime correct = LocalTime.of(13, 0);
		assert(time.equals(correct));
	}
	
	@Test
	public void normalizeTimeStringTest() {
		String text = "Start Time: 8:30pm";
		String normalized = GamePageDocumentParser.normalizeTimeString(text);
		assert(normalized.equals("8:30PM"));
	}
	
	@Test
	public void parseDateFromStringTest() {
		String date = "Sunday Dec 10, 2017";
		LocalDate d = GamePageDocumentParser.parseDateFromString(date);
		LocalDate correctDate = LocalDate.of(2017, Month.DECEMBER, 10);
		assert(d.equals(correctDate));
	}
	
	@Test
	public void removeWeekDayFromDateString() {
		String date = "Sunday Sep 17, 2017";
		date = GamePageDocumentParser.removeWeekDayFromDateString(date);
		assert(date.equals("Sep 17, 2017"));
	}

	@AfterClass
	public static void afterTest() {
		System.out.println("Ending GamePageDocumentParser test.");
	}
}
