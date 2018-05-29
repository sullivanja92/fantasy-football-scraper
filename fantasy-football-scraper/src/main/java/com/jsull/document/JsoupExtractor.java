package com.jsull.document;

import java.io.BufferedReader;
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
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * 
 * @author Josh Sullivan
 *
 */
public abstract class JsoupExtractor {
	
	public static final String USER_AGENT_HEADER = "Mozilla/5.0 (Macintosh; Intel Mac OS X 10_13_3) "
			+ "AppleWebKit/537.36 (KHTML, like Gecko) Chrome/66.0.3359.181 Safari/537.36";
	
	private Logger log = LoggerFactory.getLogger(JsoupExtractor.class);
	protected Document doc;
	
	public JsoupExtractor(String url) {
		this.doc = initializeDoc(url);
	}
	
	public Attribute attributeFromElementByName(Element e, String attributeName) {
		Attributes attributes = e.attributes();
		Attribute attribute = null;
		for (Attribute a : attributes) {
			if (a.getKey().equals(attributeName)) {
				attribute = a;
				break;
			}
		}
		log.debug(String.format("Attribute value for attribute = %s from element with tag = %s is %s", 
				attributeName, e.tag(), attribute.getValue()));
		return attribute;
	}

	public Attributes attributesBelongingToElement(Element element) {
		Attributes attributes = element.attributes();
		log.debug(String.format("Found %d attributes belonging to element with tag = %s", 
				attributes.size(), element.tag()));
		return attributes;
	}
	
	public List<Attribute> attributesFromElementsByName(Elements elements, String attributeName) {
		List<Attribute> attributesList = new ArrayList<>();
		for (Element element : elements) {
			Attributes attributes = element.attributes();
			for (Attribute a : attributes) {
				if (a.getKey().equals(attributeName)) {
					attributesList.add(a);
					break;
				}
			}
		}
		log.debug(String.format("Found %d attributes with name = %s from list of %d elements",
				attributesList.size(), attributeName, elements.size()));
		return attributesList;
	}
	
	public String attributeValueFromElement(Element e, String attributeName) {
		Attributes attributes = e.attributes();
		String val = attributes.get(attributeName);
		log.debug(String.format("Attribute value with name = %s from element with tag = %s is %s", 
				attributeName, e.tag(), val));
		return val;
	}
	
	public Element childElementFromElementsByClass(Elements elements, String className) {
		Element parent = firstElementFromElements(elements);
		Element element = null;
		for (Element e : parent.children()) {
			if (e.classNames().contains(className)) {
				element = e;
				break;
			}
		}
		log.debug(String.format("Found child element with class = %s and tag = %s from element with tag = %s", 
				className, element.tag(), parent.tag()));
		return element;
	}
	
	public Element childElementFromElementsContainingText(Elements elements, String text) {
		Element element = null;
		Element parent = firstElementFromElements(elements);
		for (Element e : parent.children()) {
			if (e.text().contains(text)) {
				element = e;
				break;
			}
		}
		log.debug(String.format("Found child element with tag = %s from parent element with tag = %s by text = %s", 
				element.tag(), parent.tag(), text));
		return element;
	}
	
	public Element elementById(String id) {
		Element e = this.doc.getElementById(id);
		log.debug(String.format("Found element by id = %s with tag = %s", id, e.tag()));
		return e;
	}
	
	public Elements elementsByAttributeValueEqualing(String attributeName, String attributeValue) {
		Elements elements = this.doc.getElementsByAttributeValue(attributeName, attributeValue);
		log.debug(String.format("Found %d elements with attribute = %s and value = %s", 
				elements.size(), attributeName, attributeValue));
		return elements;
	}
	
	public Elements elementsByAttributeValueStartingWith(String attributeName, String attributeValue) {
		Elements elements = this.doc.getElementsByAttributeValueStarting(attributeName, attributeValue);
		log.debug(String.format("Found %d elements by attribute = %s and value starting with %s", 
				elements.size(), attributeName, attributeValue));
		return elements;
	}
	
	public Elements elementsByClass(String className) {
		Elements elements = this.doc.getElementsByClass(className);
		log.debug(String.format("Found %d elements by class = %s", elements.size(), className));
		return elements;
	}
	
	public Elements elementsByTag(String tag) {
		Elements elements = this.doc.getElementsByTag(tag);
		log.debug(String.format("Found %d elements by tag = %s", elements.size(), tag));
		return elements;
	}
	
	public Elements elementsByTextEqualTo(String text) {
		Elements elements = this.doc.getElementsContainingOwnText(text);
		log.debug(String.format("Found %d elements with having text = %s", elements.size(), text));
		return elements;
	}
	
	public Elements elementsFromElementByQuery(Element element, String query) {
		Elements elements = element.select(query);
		log.debug(String.format("Found %d elements from element with tag = %s by query = %s", 
				elements.size(), element.tag(), query));
		return elements;
	}
	
	public Elements elementsWithOwnTextMatchingRegex(String regex) {
		Elements elements = this.doc.getElementsMatchingOwnText(regex);
		log.debug(String.format("Found %d elements with text matching regex = %s", elements.size(), regex));
		return elements;
	}
	
	public Elements filterElementsByTag(Elements elements, String tag) {
		int size = elements.size();
		elements.removeIf((Element e) -> !e.tag().getName().equals(tag));
		log.info(String.format("Filtered %d elements.", elements.size() - size));
		return elements;
	}
	
	public Element firstElementFromElements(Elements elements) {
		Element e = elements.get(0);
		return e;
	}
	
	private Document initializeDoc(String url) {
		Document doc = null;
		try {
			log.debug(String.format("Initializing jsoup document with url = %s", url));
			URL u = new URL(url);
			HttpURLConnection conn = (HttpURLConnection) u.openConnection();
			conn.setRequestProperty("User-Agent", USER_AGENT_HEADER);
			BufferedReader reader = new BufferedReader(new InputStreamReader(conn.getInputStream()));
			String line;
			StringBuilder contents = new StringBuilder("");
			//BufferedWriter writer = new BufferedWriter(new FileWriter(new File("doc_contents.html")));
			while ((line = reader.readLine()) != null) {
				contents.append(line);
				//writer.write(line);
				//writer.write("\n");
			}
			//writer.flush();
			//writer.close();
			//this.doc = Jsoup.parse(new File("doc_contents.html"), "UTF-8", "");
			//this.doc = Jsoup.parse(contents.toString());
			doc = Jsoup.parse(contents.toString());
		} catch (Exception e) {
			log.error(String.format("Exception: %s encountered while initializing jsoup document\n%s", 
					e.getClass(), e.getStackTrace()));
		}
		return doc;
	}
	
	public Element parentOfElementByQuery(String query) {
		Elements elements = this.doc.select(query);
		Element e = firstElementFromElements(elements);
		Element parent = e.parent();
		log.debug(String.format("Found parent element with tag = %s from child element with tag = %s by query = %s", 
				parent.tag(), e.tag(), query));
		return parent;
	}
	
	public Element parentOfElementByText(String text) {
		Elements elements = this.doc.getElementsContainingOwnText(text);
		Element e = firstElementFromElements(elements);
		Element parent = e.parent();
		log.debug(String.format("Found parent element with tag = %s from child element with tag = %s by text = %s", 
				parent.tag(), e.tag(), text));
		return parent;
	}
}
