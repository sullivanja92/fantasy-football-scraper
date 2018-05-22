package com.jsull.document;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Attribute;
import org.jsoup.nodes.Attributes;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

public abstract class JsoupExtractor {
	
	protected Document doc;
	public static final String USER_AGENT_HEADER = "Mozilla/5.0 (Windows NT 6.1; WOW64) "
			+ "AppleWebKit/537.36 (KHTML, linke Gecko) Chrome/62.0.3202.75 Safari/537.36";
	
	public JsoupExtractor(String url) {
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

	public Elements filterElementsByTag(Elements elements, String tag) {
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
	
	public Elements selectFromElementByQuery(Element e, String query) {
		return e.select(query);
	}
	
	public String getAttributeValueBelongingTo(Element e, String attribute) {
		Attributes a = e.attributes();
		return a.get(attribute);
	}
	
	public Attributes getAttributesBelongingTo(Element e) {
		return e.attributes();
	}
	
	public Attribute getAttributeFromElementByName(Element e, String name) {
		Attributes attributes = e.attributes();
		for (Attribute attribute : attributes) {
			if (attribute.getKey().equals(name))
				return attribute;
		}
		return null;
	}
	
	public List<Attribute> extractAttributesFromElementsByName(Elements elements, String name) {
		List<Attribute> attributesList = new ArrayList<>();
		for (Element e : elements) {
			Attributes attributes = e.attributes();
			for (Attribute a : attributes) {
				if (a.getKey().equals(name)) {
					attributesList.add(a);
					break;
				}
			}
		}
		return attributesList;
	}
	
	public Element getChildElementContainingText(Elements elements, String text) {
		for (Element e : elements.get(0).children()) {
			if (e.text().contains(text))
				return e;
		}
		return null;
	}
	
	public Element getChildElementByClass(Elements elements, String name) {
		for (Element e : elements.get(0).children()) {
			if (e.classNames().contains(name))
				return e;
		}
		return null;
	}
	
	public Element getParentElementByOwnText(String text) {
		Elements els = this.doc.getElementsContainingOwnText(text);
		Element e = els.get(0);
		return e.parent();
	}
	
	public Element getParentByCSSQuery(String query) {
		Elements els = this.doc.select(query);
		Element e = els.get(0);
		return e.parent();
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
