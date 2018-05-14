package com.jsull.page;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Attribute;
import org.jsoup.nodes.Attributes;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

public abstract class JsoupParser {
	
	protected Document doc;
	public static final String USER_AGENT_HEADER = "Mozilla/5.0 (Windows NT 6.1; WOW64) "
			+ "AppleWebKit/537.36 (KHTML, linke Gecko) Chrome/62.0.3202.75 Safari/537.36";
	
	public JsoupParser(String url) {
		try {
			URL u = new URL(url);
			HttpURLConnection conn = (HttpURLConnection) u.openConnection();
			conn.setRequestProperty("User-Agent", USER_AGENT_HEADER);
			BufferedReader reader = new BufferedReader(new InputStreamReader(conn.getInputStream()));
			String line;
			StringBuilder contents = new StringBuilder("");
			BufferedWriter writer = new BufferedWriter(new FileWriter(new File("doc_contents.html")));
			while ((line = reader.readLine()) != null) {
				contents.append(line);
				writer.write(line);
				writer.write("\n");
			}
			writer.flush();
			writer.close();
			this.doc = Jsoup.parse(new File("doc_contents.html"), "UTF-8", "");
		} catch (Exception e) {
			e.printStackTrace();
		}
		
		
	}

	public Elements extractElementsByTag(Elements elements, String tag) {
		elements.removeIf((Element e) -> !e.tag().getName().equals(tag));
		return elements;
	}
	
	public Element getById(String id) {
		return this.doc.getElementById(id);
	}
	
	public Elements getByTag(String tag) {
		return this.doc.getElementsByTag(tag);
	}
	
	public Elements elementsByClass(String name) {
		return this.doc.getElementsByClass(name);
	}
	
	public Elements getElementsByAttributeValueStartingWith(String attribute, String val) {
		return this.doc.getElementsByAttributeValueStarting(attribute, val);
	}
	
	public Elements getElementsByAttributeValueEqualing(String attribute, String val) {
		return this.doc.getElementsByAttributeValue(attribute, val);
	}
	
	public Elements getElementsByTextEqualTo(String text) {
		return this.doc.getElementsContainingOwnText(text);
	}
	
	public Elements getElementsWithOwnTextMatchingRegex(String regex) {
		return this.doc.getElementsMatchingOwnText(regex);
	}
	
	public String getAttributeValueBelongingTo(Element e, String attribute) {
		Attributes a = e.attributes();
		return a.get(attribute);
	}
	
	public Attributes getAttributesBelongingTo(Element e) {
		return e.attributes();
	}
	
	public String getAttributeValue(Element e, String attribute) {
		Attributes a = e.attributes();
		for (Attribute att : a) {
			if (att.getKey().equals(attribute))
				return att.getValue();
		}
		return null;
	}
}
