package com.jsull;

import org.jsoup.nodes.Element;
import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;

import com.jsull.document.extractor.EspnPlayerPageDocumentExtractor;

public class EspnPlayerPageDocumentExtractorTest {
	
	@BeforeClass
	public static void beforeTest() {
		System.out.println("Beginning EspnPlayerPageDocumentExtractor test.");
	}

	@Test
	public void test() {
		String url = "http://www.espn.com/nfl/player/_/id/12649/julian-edelman";
		EspnPlayerPageDocumentExtractor extractor = 
				new EspnPlayerPageDocumentExtractor(url);
		Element e = extractor.getHeightWeightRow();
		assert(e.text().equals("5' 10\", 200 lbs"));
	}

	@AfterClass
	public static void afterTest() {
		System.out.println("Ending EspnPlayerPageDocumentExtractor test.");
	}
}
