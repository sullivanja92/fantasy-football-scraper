package com.jsull.document.extractor;

import org.jsoup.nodes.Attribute;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import com.jsull.document.JsoupExtractor;

public class EspnPlayerPageDocumentExtractor extends JsoupExtractor {
	
	public static final String GENERAL_INFO_CLASS = "general-info";
	public static final String HEIGHT_WEIGHT_ROW_TERM = "lbs";
	public static final String PLAYER_METADATA_CLASS = "player-metadata floatleft";
	public static final String COLLEGE_ROW_TERM = "College";
	public static final String NUMBER_ELEMENT_CLASS = "first";
	public static final String PLAYER_DRAFTED_TEXT = "Drafted";
	public static final String BIRTH_DATE_QUERY = "ul > li > span";
	public static final String IMAGE_PARENT_CLASS = "main-headshot";
	
	public EspnPlayerPageDocumentExtractor(String url) {
		super(url);
	}
	
	public Element getHeightWeightRow() {
		Elements elements = elementsByClass(GENERAL_INFO_CLASS);
		Element e = childElementFromElementsContainingText(elements, HEIGHT_WEIGHT_ROW_TERM);
		return e;
	}
	
	public Element getCollegeRow() {
		Elements elements = elementsByClass(PLAYER_METADATA_CLASS);
		Element e = childElementFromElementsContainingText(elements, COLLEGE_ROW_TERM);
		return e;
	}
	
	public Element getNumberRow() {
		Elements elements = elementsByClass(GENERAL_INFO_CLASS);
		Element e = childElementFromElementsByClass(elements, NUMBER_ELEMENT_CLASS);
		return e;
	}
	
	public boolean playerHasDraftInfoRow() {
		Elements draftedRowEls = elementsByTextEqualTo(PLAYER_DRAFTED_TEXT);
		Element draftedRowEl = draftedRowEls.get(0);
		String content = draftedRowEl.text();
		if (content.contains("{") || content.contains("\""))
			return false;
		return true;
	}
	
	public Element getPlayerDraftInfoRow() {
		Elements children = elementsByTextEqualTo(PLAYER_DRAFTED_TEXT);
		Element child = children.get(0);
		Element parent = child.parent();
		return parent;
	}
	
	public Element getBirthDateRow() {
		Element birthDateRow = parentOfElementByQuery(BIRTH_DATE_QUERY);
		return birthDateRow;
	}
	
	public Attribute getHeadshotImgSrcAttribute() {
		Elements imgElements = elementsByClass(IMAGE_PARENT_CLASS);
		Element imgEl = imgElements.get(0);
		Element img = imgEl.child(0);
		return attributeFromElementByName(img, "src");
	}
}
