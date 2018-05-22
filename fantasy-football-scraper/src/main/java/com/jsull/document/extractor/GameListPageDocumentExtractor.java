package com.jsull.document.extractor;

import java.util.List;

import org.jsoup.nodes.Attribute;
import org.jsoup.select.Elements;

import com.jsull.document.JsoupExtractor;

public class GameListPageDocumentExtractor extends JsoupExtractor {
	
	public static final String GAME_LINK_LOCATOR = "Final";
	
	public GameListPageDocumentExtractor(String url) {
		super(url);
	}
	
	public List<Attribute> getGameBoxScoreHrefs() {
		Elements boxScoreElements = getGameBoxScoreElements();
		Elements linkElements = filterElementsByTag(boxScoreElements, "a");
		List<Attribute> hrefAttributes = 
				extractAttributesFromElementsByName(linkElements, "href");
		return hrefAttributes;
	}
	
	public Elements getGameBoxScoreElements() {
		Elements boxScoreElements = getElementsByTextEqualTo(GAME_LINK_LOCATOR);
		return boxScoreElements;
	}
}
