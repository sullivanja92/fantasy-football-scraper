package com.jsull.document.extractor;

import java.util.Arrays;
import java.util.List;

import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import com.jsull.document.JsoupExtractor;
import com.jsull.entity.Player;

public class GoogleSearchDocumentExtractor extends JsoupExtractor {
	
	public static final String BASE_URL = "https://www.google.com/search?";
	public static final String URL_SEARCH_TAG = "a";
	
	public GoogleSearchDocumentExtractor(String url) {
		super(url);
	}
	
	public GoogleSearchDocumentExtractor(Player p) {
		super(generateGoogleSearchLinkForPlayer(p));
	}

	public String getEspnLinkForPlayer(Player p) {		// may be removed
		Elements elements = getByTag(URL_SEARCH_TAG);
		String link = findEspnLink(elements);
		return link;
	}
	
	public String getEspnLinkForPlayer() {
		Elements elements = getByTag(URL_SEARCH_TAG);
		String link = findEspnLink(elements);
		System.out.println("Link to espn page: " + link);
		return link;
	}
	
	public String findEspnLink(Elements links) {
		for (Element link : links) {
			String href = getAttributeValueBelongingTo(link, "href");
			if (validEspnLink(href))
				return href;
		}
		return null;
	}
	
	public boolean validEspnLink(String link) {
		List<String> linkParts = splitLinkIntoParts(link);
		if (!linkParts.contains("www.espn.com"))
			return false;
		if (!linkParts.contains("nfl"))
			return false;
		if (!linkParts.contains("player"))
			return false;
		if (!linkParts.contains("id"))
			return false;
		return true;
	}
	
	public List<String> splitLinkIntoParts(String link) {
		link = link.replace("//", "/");
		return Arrays.asList(link.split("/"));
	}
	
	public static String generateGoogleSearchLinkForPlayer(Player p) {
		return BASE_URL + String.format("q=%s+nfl+espn&num=100",  p.getFirst() + "+" + p.getLast());
	}
}
