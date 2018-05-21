package com.jsull.document;

import java.time.LocalDate;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import com.jsull.entity.Player;
import com.jsull.util.PlayerDataUtils;

public class EspnPlayerPageDocument extends JsoupExtractor {
	
	public static final String GENERAL_INFO_CLASS = "general-info";
	public static final String HEIGHT_WEIGHT_ROW_TERM = "lbs";
	public static final String PLAYER_METADATA_CLASS = "player-metadata floatleft";
	public static final String NUMBER_ELEMENT_CLASS = "first";
	public static final String PLAYER_DRAFTED_TEXT = "Drafted";
	public static final String draftRoundRegex = "(\\d{4}:\\s+)(\\d+\\w{2})";
	public static final String draftPickRegex = "(, )(\\d+)(\\w{2})";
	public static final String draftTeamRegex = "(by\\s+)(\\w+)";
	public static final String BIRTH_DATE_QUERY = "ul > li > span";
	public static final String IMAGE_PARENT_CLASS = "main-headshot";
	
	public EspnPlayerPageDocument(String url) {
		super(url);
	}
	
	public void getPlayerDetails(Player p) {
		try { p.setFeet(getFeet()); } catch(Exception e) { System.err.println(String.format("Error getting feet for: %s %s\n%s", p.getFirst(), p.getLast(), e)); }
		try { p.setInches(getInches()); } catch(Exception e) { System.err.println(String.format("Error getting inches for: %s %s\n%s", p.getFirst(), p.getLast(), e)); }
		try { p.setWeight(getWeight()); } catch(Exception e) { System.err.println(String.format("Error getting weight for: %s %s\n%s", p.getFirst(), p.getLast(), e)); }
		try { p.setCollege(getCollege()); } catch(Exception e) { System.err.println(String.format("Error getting college for: %s %s\n%s", p.getFirst(), p.getLast(), e)); }
		try { p.setNumber(getNumber()); } catch(Exception e) { System.err.println(String.format("Error getting number for: %s %s\n%s", p.getFirst(), p.getLast(), e)); }
		if (playerHasDraftInfo()) 
			setPlayerDraftInfo(p);
		try { p.setBirthDate(getBirthDate()); } catch(Exception e) { System.err.println(String.format("Error getting birthdate for: %s %s\n%s", p.getFirst(), p.getLast(), e)); }
		try { p.setImageUrl(getImageUrl()); } catch(Exception e) { System.err.println(String.format("Error getting image for: %s %s\n%s", p.getFirst(), p.getLast(), e)); }
	}
	
	public int getFeet() {
		Element e = getGeneralInfoRowElement(HEIGHT_WEIGHT_ROW_TERM);
		System.out.println("getFeet text = " + e.text());
		int feet = Integer.parseInt(e.text().split("'")[0].trim());
		return feet;
	}
	
	public int getInches() {
		Element e = getGeneralInfoRowElement(HEIGHT_WEIGHT_ROW_TERM);
		int inches = Integer.parseInt(e.text().split(",")[0].trim().split("'")[1].trim().replace("\"", ""));
		return inches;
	}
	
	public int getWeight() {
		Element e = getGeneralInfoRowElement(HEIGHT_WEIGHT_ROW_TERM);
		int weight = Integer.parseInt(e.text().split(",")[1].trim().split(" ")[0]);
		return weight;
	}
	
	public String getCollege() {
		Elements elements = elementsByClass(PLAYER_METADATA_CLASS);
		Element e = getChildElementContainingText(elements, "College");
		return e.ownText();
	}
	
	public int getNumber() {
		Elements elements = elementsByClass(GENERAL_INFO_CLASS);
		Element e = getChildElementByClass(elements, NUMBER_ELEMENT_CLASS);
		String text = e.text();
		return getPlayerNumberFromString(text);
	}
	
	
	public boolean playerHasDraftInfo() {
		String content = getElementsByTextEqualTo(PLAYER_DRAFTED_TEXT).get(0).text();
		System.out.println("content = " + content);
		if (content.contains("{") || content.contains("\""))
			return false;
		return true;
	}
	
	public void setPlayerDraftInfo(Player p) {
		Element parent = getElementsByTextEqualTo(PLAYER_DRAFTED_TEXT).get(0).parent();
		String text = parent.text().replace("Drafted", "").trim();
		p.setDraftYear(extractDraftYear(text));
		p.setDraftPick(extractDraftPick(text));
		p.setDraftTeam(extractDraftTeam(text));
		p.setDraftRound(extractDraftRound(text));
	}
	
	private Integer extractDraftYear(String text) {
		Integer year = Integer.valueOf(text.split(":")[0].trim());
		return year;
	}
	
	private Integer extractDraftRound(String text) {
		Pattern pattern = Pattern.compile(draftRoundRegex);
		Matcher matcher = pattern.matcher(text);
		Integer round;
		if (matcher.find()) {
			round = Integer.valueOf(stripNonDigits(matcher.group(2)));	
		} else {
			round = null;
		}
		return round;
	}
	
	private Integer extractDraftPick(String text) {
		Pattern pattern = Pattern.compile(draftPickRegex);
		Matcher matcher = pattern.matcher(text);
		Integer pick = null;
		if (matcher.find()) {
			pick = Integer.valueOf(matcher.group(2));
		}
		return pick;
	}
	
	private String extractDraftTeam(String text) {
		Pattern pattern = Pattern.compile(draftTeamRegex);
		Matcher matcher = pattern.matcher(text);
		String team = null;
		if (matcher.find()) {
			team = matcher.group(2);
		}
		return team;
	}
	
	public LocalDate getBirthDate() {
		Element parent = getParentByCSSQuery(BIRTH_DATE_QUERY);// refactor classes into 'document' classes containing methods
		String text = parent.text().replace("Born", "").trim();				// such as 'getBirthDateText()' and 'extraction' methods
		text = text.split("in")[0].trim();									// performing the rest of the logic
		String[] dateArr = text.split(" ");
		LocalDate birthDate = LocalDate.of(
				Integer.parseInt(dateArr[2].trim()), 
				PlayerDataUtils.getMonthFromAbbreviation(dateArr[0].trim()), 
				Integer.parseInt(dateArr[1].replace(",", "").trim()));
		System.out.println("birthdate: " + birthDate);
		return birthDate;
	}
	
	public String getImageUrl() {
		Element e = elementsByClass(IMAGE_PARENT_CLASS).get(0);
		Element img = e.child(0);
		return getAttributeValueBelongingTo(img, "src");
	}
	
	// should move to another class
	public static String stripNonDigits(String string) {
		String returnStr = "";
		for (int i=0; i<string.length(); i++) {
			if (Character.isDigit(string.charAt(i)))
				returnStr += string.charAt(i);
		}
		return returnStr;
	}
	
	public static int getPlayerNumberFromString(String s) {
		s = s.replace("#", "");
		s = s.split(" ")[0];
		int x = Integer.parseInt(s);
		return x;
	}
	
	public Element getGeneralInfoRowElement(String val) {
		Elements elements = elementsByClass(GENERAL_INFO_CLASS);
		Element e = getChildElementContainingText(elements, val);
		return e;
	}
	
	public static void main(String[] args) {
		String url = "http://www.espn.com/nfl/player/_/id/2577134/ty-montgomery";
		EspnPlayerPageDocument doc = new EspnPlayerPageDocument(url);
		System.out.println(doc.getCollege());
	}
}
