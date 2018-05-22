package com.jsull.document.parser;

import java.util.ArrayList;
import java.util.List;

import org.jsoup.nodes.Attribute;

import com.jsull.document.extractor.GameListPageDocumentExtractor;

public class GameListPageDocumentParser {
	
	private GameListPageDocumentExtractor extractor;
	
	public static final String BASE_URL = "https://www.pro-football-reference.com";
	public static final String URL_TEMPLATE = "https://www.pro-football-reference.com/years/%d/week_%d.htm";
	
	public GameListPageDocumentParser(String url) {
		this.extractor = new GameListPageDocumentExtractor(url);
	}
	
	public List<String> getGameUrls() {
		List<Attribute> hrefs = this.extractor.getGameBoxScoreHrefs();
		List<String> urls = new ArrayList<>();
		for (Attribute href : hrefs) {
			String url = BASE_URL + href.getValue();
			urls.add(url);
		}
		return urls;
	}
	
	public static String getGameListUrlByYearAndWeek(int year, int week) {
		if (year > 2018 || week > 17 || week <= 0) 
			throw new IllegalArgumentException(String.format("Illegal arguments: year =%d, week = %d", year, week));
		return String.format(URL_TEMPLATE, year, week);
	}
}
