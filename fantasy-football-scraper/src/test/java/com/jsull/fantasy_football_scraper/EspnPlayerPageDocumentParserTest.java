package com.jsull.fantasy_football_scraper;

import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;

import com.jsull.document.parser.EspnPlayerPageDocumentParser;

public class EspnPlayerPageDocumentParserTest {
	
	@BeforeClass
	public static void beforeTest() {
		System.out.println("Beginning EspnPlayerPageDocumentParser test.");
	}

	@Test
	public void parseFeetFromStringTest() {
		String text = "6' 2\", 225 lbs";
		int feet = EspnPlayerPageDocumentParser.parsePlayerFeetFromString(text);
		assert(feet==6);
	}
	
	@Test
	public void parseInchesFromStringTest() {
		String text = "6' 10\", 265 lbs";
		int inches = EspnPlayerPageDocumentParser.parsePlayerInchesFromString(text);
		assert(inches==10);
	}
	
	@AfterClass
	public static void afterTest() {
		System.out.println("Ending EspnPlayerPageDocumentParser test.");
	}
}
