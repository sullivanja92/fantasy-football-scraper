package com.jsull.page;

import java.util.ArrayList;
import java.util.List;

import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

public class GameListPageDocument extends JsoupParser {
	
	public static final String BASE_URL = "https://www.pro-football-reference.com";
	public static final String GAME_LINK_LOCATOR = "Final";
	
	public GameListPageDocument(String url) {
		super(url);
	}

	public List<String> getGameUrls() {
		List<String> urls = new ArrayList<>();
		Elements elements = getElementsByTextEqualTo(GAME_LINK_LOCATOR);
		extractElementsByTag(elements, "a");
		for (Element e : elements) {
			String link = BASE_URL + getAttributeValue(e, "href");
			urls.add(link);
		}
		return urls;
	}
	
	public static String getGameListUrlByYearAndWeek(int year, int week) {
		if (year > 2018 || week > 17 || week <= 0) 
			throw new IllegalArgumentException(String.format("Illegal arguments: year =%d, week = %d", year, week));
		return String.format("https://www.pro-football-reference.com/years/%d/week_%d.htm", year, week);
	}
}
