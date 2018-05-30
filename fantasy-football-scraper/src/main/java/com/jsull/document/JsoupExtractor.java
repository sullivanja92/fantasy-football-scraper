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
 * This class is the abstract super class of the *Extractor classes. The class provides methods
 * designed to interact with and return portions of the specified web page's DOM. 
 * 
 * @author Josh Sullivan
 * @version 1.0
 *
 */
public abstract class JsoupExtractor {
	
	public static final String USER_AGENT_HEADER = "Mozilla/5.0 (Macintosh; Intel Mac OS X 10_13_3) "
			+ "AppleWebKit/537.36 (KHTML, like Gecko) Chrome/66.0.3359.181 Safari/537.36";
	
	private Logger log = LoggerFactory.getLogger(JsoupExtractor.class);
	protected Document doc;
	
	/**
	 * 
	 * @param url String url object representing the web page URL to use for extraction.
	 */
	public JsoupExtractor(String url) {
		this.doc = initializeDoc(url);
	}
	
	/**
	 * This method returns an attribute selected from an element by name.
	 * 
	 * @param	e 				The element that is to be searched.
	 * @param 	attributeName 	The attribute name that is to be searched for.
	 * @return 					The attribute collected by name from the supplied element, 
	 * 							or null if the attribute is not present.
	 */
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

	/**
	 * This method returns all attributes belonging to an element.
	 * 
	 * @param element The element that is to be used for gathering attributes.
	 * @return All attributes belonging to the supplied element, or null if none exist.
	 */
	public Attributes attributesBelongingToElement(Element element) {
		Attributes attributes = element.attributes();
		log.debug(String.format("Found %d attributes belonging to element with tag = %s", 
				attributes.size(), element.tag()));
		return attributes;
	}
	
	/**
	 * This method returns a list of attributes with matching names collected from a group of elements.
	 * 
	 * @param elements A collection of elements to be searched.
	 * @param attributeName The attribute name that is to be searched for.
	 * @return A list of attributes with attribute names matching the attributeName param, or an empty list if none exist.
	 */
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
	
	/**
	 * This method returns the attribute value of an attribute selected from an element by name.
	 * 
	 * @param e The element that is to be searched.
	 * @param attributeName The attribute name that is to be searched for.
	 * @return The String value of the attribute identified by the attributeName param.
	 */
	public String attributeValueFromElement(Element e, String attributeName) {
		Attributes attributes = e.attributes();
		String val = attributes.get(attributeName);
		log.debug(String.format("Attribute value with name = %s from element with tag = %s is %s", 
				attributeName, e.tag(), val));
		return val;
	}
	
	/**
	 * This method selects a child element of the first supplied element's parent by class name.
	 * 
	 * @param elements The collection of elements. The first element in the collection is chosen as the parent.
	 * @param className The class name that is to be used to recognize a child element.
	 * @return The element identified by class name from the parent element, or null if none are found.
	 */
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
	
	/**
	 * This method selects a child element of the first supplied element's parent by contained text.
	 * 
	 * @param elements The collection of elements. The first element in the collection is chosen as the parent.
	 * @param text The text that is to be used to recognize a child element.
	 * @return The element identified by containing text from the parent element, or null if none are found.
	 */
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
	
	/**
	 * This method selects a single element by its id.
	 * 
	 * @param id The id value that is to be searched for.
	 * @return The element that is found with its id matching the id param, or null if none are found.
	 */
	public Element elementById(String id) {
		Element e = this.doc.getElementById(id);
		log.debug(String.format("Found element by id = %s with tag = %s", id, e.tag()));
		return e;
	}
	
	/**
	 * This method selects all elements which contain an attribute name and value matching supplied parameters.
	 * 
	 * @param 	attributeName 	The attribute name that is to be used to select elements.
	 * @param 	attributeValue 	The attribute values that is to be used to select elements.
	 * @return 	The elements that are selected with the value of their attributes identified by the 
	 * 		   	attributeName param matching the attributeValue param.
	 */
	public Elements elementsByAttributeValueEqualing(String attributeName, String attributeValue) {
		Elements elements = this.doc.getElementsByAttributeValue(attributeName, attributeValue);
		log.debug(String.format("Found %d elements with attribute = %s and value = %s", 
				elements.size(), attributeName, attributeValue));
		return elements;
	}
	
	/**
	 * This method returns all elements which contain an attribute name and beginning value matching supplied parameters.
	 * 
	 * @param attributeName The attribute name that is to be used to select elements.
	 * @param attributeValue The attribute values that is to be used to select elements.
	 * @return The elements that are selected with the value of their attributes identified by the 
	 * 		   	attributeName param beginning with the attributeValue param.
	 */
	public Elements elementsByAttributeValueStartingWith(String attributeName, String attributeValue) {
		Elements elements = this.doc.getElementsByAttributeValueStarting(attributeName, attributeValue);
		log.debug(String.format("Found %d elements by attribute = %s and value starting with %s", 
				elements.size(), attributeName, attributeValue));
		return elements;
	}
	
	/**
	 * This method returns all elements which contain the provided class.
	 * 
	 * @param className The name of the class that is to be used to select elements.
	 * @return The elements selected which are contain classes matching the className param.
	 */
	public Elements elementsByClass(String className) {
		Elements elements = this.doc.getElementsByClass(className);
		log.debug(String.format("Found %d elements by class = %s", elements.size(), className));
		return elements;
	}
	
	/**
	 * This method returns all elements selected which have a tag matching the provided parameter.
	 * 
	 * @param tag The tag that is to be used to select elements.
	 * @return The elements which have tags matching the tag specified in the tag param.
	 */
	public Elements elementsByTag(String tag) {
		Elements elements = this.doc.getElementsByTag(tag);
		log.debug(String.format("Found %d elements by tag = %s", elements.size(), tag));
		return elements;
	}
	
	/**
	 * This method returns all elements whose own text matches the supplied parameter.
	 * 
	 * @param text The text that is to be used to select elements.
	 * @return The elements which contain text exactly matching the supplied text in the text param.
	 */
	public Elements elementsByTextEqualTo(String text) {
		Elements elements = this.doc.getElementsContainingOwnText(text);
		log.debug(String.format("Found %d elements with having text = %s", elements.size(), text));
		return elements;
	}
	
	/**
	 * This method returns all elements selected from a single element by a CSS query.
	 * 
	 * @param element The element that is to be searched for additional elements.
	 * @param query The CSS query that is to be used to select elements.
	 * @return The elements that have been selected from the provided element by the provided query.
	 */
	public Elements elementsFromElementByQuery(Element element, String query) {
		Elements elements = element.select(query);
		log.debug(String.format("Found %d elements from element with tag = %s by query = %s", 
				elements.size(), element.tag(), query));
		return elements;
	}
	
	/**
	 * This method returns all elements whose own text matches a provided regular expression.
	 * 
	 * @param regex The regular expression that is to be used to select elements.
	 * @return The elements that have been selected by the supplied regular expression.
	 */
	public Elements elementsWithOwnTextMatchingRegex(String regex) {
		Elements elements = this.doc.getElementsMatchingOwnText(regex);
		log.debug(String.format("Found %d elements with text matching regex = %s", elements.size(), regex));
		return elements;
	}
	
	/**
	 * This method filters out elements from a collection of elements by a tag parameter.
	 * 
	 * @param elements The collection of elements that is to be filtered.
	 * @param tag The tag that is to be used to filter elements.
	 * @return The elements that have been selected from the supplied elements with tags matching the tag param.
	 */
	public Elements filterElementsByTag(Elements elements, String tag) {
		int size = elements.size();
		elements.removeIf((Element e) -> !e.tag().getName().equals(tag));
		log.info(String.format("Filtered %d elements.", elements.size() - size));
		return elements;
	}
	
	/**
	 * This method returns the first element selected from a collection of elements.
	 * 
	 * @param elements The collection of elements that is to be filtered.
	 * @return The first element in the collection of elements.
	 */
	public Element firstElementFromElements(Elements elements) {
		Element e = elements.get(0);
		return e;
	}
	
	/**
	 * This is a helper method that is used to initialize the Document instance variable. The method
	 * writes the contents of the input stream created through the provided URL to a StringBuilder and
	 * creates a Document object from that.
	 * 
	 * @param url The uniform resource locator of the desired webpage.
	 * @return A Document object representing the DOM for the web page located by the supplied URL.
	 */
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
	
	/**
	 * This method is used to select the parent of the first element selected with a CSS query.
	 * 
	 * @param query The CSS query that is used to select elements.
	 * @return The parent of the first element that is selected with the query param.
	 */
	public Element parentOfElementByQuery(String query) {
		Elements elements = this.doc.select(query);
		Element e = firstElementFromElements(elements);
		Element parent = e.parent();
		log.debug(String.format("Found parent element with tag = %s from child element with tag = %s by query = %s", 
				parent.tag(), e.tag(), query));
		return parent;
	}
	
	/**
	 * This method is used to select the parent of the first element selected whose own text matches the provided text param.
	 * 
	 * @param text The text that is to be used to identity valid elements. 
	 * @return The parent of the first element identified using the text param.
	 */
	public Element parentOfElementByText(String text) {
		Elements elements = this.doc.getElementsContainingOwnText(text);
		Element e = firstElementFromElements(elements);
		Element parent = e.parent();
		log.debug(String.format("Found parent element with tag = %s from child element with tag = %s by text = %s", 
				parent.tag(), e.tag(), text));
		return parent;
	}
}
