package com.jsull.document.parser;

import java.time.LocalDate;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.jsoup.nodes.Attribute;
import org.jsoup.nodes.Element;

import com.jsull.document.extractor.EspnPlayerPageDocumentExtractor;
import com.jsull.entity.Player;
import com.jsull.util.PlayerDataUtils;
import com.jsull.util.StringUtils;

public class EspnPlayerPageDocumentParser {
	
	private EspnPlayerPageDocumentExtractor espnPlayerPageDocument;
	
	public static final String DRAFT_ROUND_REGEX = "(\\d{4}:\\s+)(\\d+\\w{2})";
	public static final String DRAFT_PICK_REGEX = "(, )(\\d+)(\\w{2})";
	public static final String DRAFT_TEAM_REGEX = "(by\\s+)(\\w+)";
	
	public EspnPlayerPageDocumentParser(String url) {
		this.espnPlayerPageDocument = new EspnPlayerPageDocumentExtractor(url);
	}
	
	public void setPlayerDetailsFor(Player p) {
		try { p.setFeet(getPlayerFeet()); } catch(Exception e) { System.err.println(String.format("Error getting feet for: %s %s\n%s", p.getFirst(), p.getLast(), e)); }
		try { p.setInches(getPlayerInches()); } catch(Exception e) { System.err.println(String.format("Error getting inches for: %s %s\n%s", p.getFirst(), p.getLast(), e)); }
		try { p.setWeight(getPlayerWeight()); } catch(Exception e) { System.err.println(String.format("Error getting weight for: %s %s\n%s", p.getFirst(), p.getLast(), e)); }
		try { p.setCollege(getCollege()); } catch(Exception e) { System.err.println(String.format("Error getting college for: %s %s\n%s", p.getFirst(), p.getLast(), e)); }
		try { p.setNumber(getNumber()); } catch(Exception e) { System.err.println(String.format("Error getting number for: %s %s\n%s", p.getFirst(), p.getLast(), e)); }
		if (this.espnPlayerPageDocument.playerHasDraftInfoRow()) 
			setPlayerDraftInfo(p);
		try { p.setBirthDate(getBirthDate()); } catch(Exception e) { System.err.println(String.format("Error getting birthdate for: %s %s\n%s", p.getFirst(), p.getLast(), e)); }
		try { p.setImageUrl(getImageUrl()); } catch(Exception e) { System.err.println(String.format("Error getting image for: %s %s\n%s", p.getFirst(), p.getLast(), e)); }
	}
	
	public int getPlayerFeet() {
		Element heightWeightRow = 
				this.espnPlayerPageDocument.getHeightWeightRow();
		String text = heightWeightRow.text();
		int feet = parsePlayerFeetFromString(text);
		return feet;
	}
	
	public static int parsePlayerFeetFromString(String text) {
		int feet = Integer.parseInt(text.split("'")[0].trim());
		return feet;
	}
	
	public int getPlayerInches() {
		Element heightWeightRow = 
				this.espnPlayerPageDocument.getHeightWeightRow();
		String text = heightWeightRow.text();
		int inches = parsePlayerInchesFromString(text);
		return inches;
	}
	
	public static int parsePlayerInchesFromString(String text) {
		int inches = Integer.parseInt(text.split(",")[0].trim().split("'")[1].trim().replace("\"", ""));
		return inches;
	}
	
	public int getPlayerWeight() {
		Element heightWeightRow = 
				this.espnPlayerPageDocument.getHeightWeightRow();
		String text = heightWeightRow.text();
		int weight = parsePlayerWeightFromString(text);
		return weight;
	}
	
	public static int parsePlayerWeightFromString(String text) {
		int weight = Integer.parseInt(text.split(",")[1].trim().split(" ")[0]);
		return weight;
	}
	
	public String getCollege() {
		Element e = this.espnPlayerPageDocument.getCollegeRow();
		String text = e.ownText();
		return text;
	}
	
	public int getNumber() {
		Element numberRow = this.espnPlayerPageDocument.getNumberRow();
		String text = numberRow.text();
		int num = getPlayerNumberFromString(text);
		return num;
	}
	
	public static int getPlayerNumberFromString(String s) {
		s = s.replace("#", "");
		s = s.split(" ")[0];
		int x = Integer.parseInt(s);
		return x;
	}
	
	public void setPlayerDraftInfo(Player p) {
		Element draftInfoRow = 
				this.espnPlayerPageDocument.getPlayerDraftInfoRow();
		String text = parseFullDraftInfoFromElement(draftInfoRow);
		p.setDraftYear(parseDraftYearFromDraftInfoString(text));
		p.setDraftPick(parseDraftPickFromDraftInfoString(text));
		p.setDraftTeam(parseDraftTeamFromDraftInfoString(text));
		p.setDraftRound(parseDraftRoundFromDraftInfoString(text));
	}
	
	public static String parseFullDraftInfoFromElement(Element e) {
		String draftInfo = e.text().replace("Drafted", "").trim();
		return draftInfo;
	}
	
	public static Integer parseDraftYearFromDraftInfoString(String draftInfo) {
		Integer year = Integer.valueOf(draftInfo.split(":")[0].trim());
		return year;
	}
	
	public static Integer parseDraftRoundFromDraftInfoString(String draftInfo) {
		Matcher matcher = getMatcherFromStringByRegex(draftInfo, DRAFT_ROUND_REGEX);
		Integer round = null;
		if (matcher.find()) {
			round = Integer.valueOf(StringUtils.stripNonDigits(matcher.group(2)));	
		} 
		return round;
	}
	
	public static Integer parseDraftPickFromDraftInfoString(String draftInfo) {
		Matcher matcher = getMatcherFromStringByRegex(draftInfo, DRAFT_PICK_REGEX);
		Integer pick = null;
		if (matcher.find()) {
			pick = Integer.valueOf(matcher.group(2));
		}
		return pick;
	}
	
	public static String parseDraftTeamFromDraftInfoString(String draftInfo) {
		Matcher matcher = getMatcherFromStringByRegex(draftInfo, DRAFT_TEAM_REGEX);
		String team = null;
		if (matcher.find()) {
			team = matcher.group(2);
		}
		return team;
	}
	
	public static Matcher getMatcherFromStringByRegex(String text, String regex) {
		Pattern pattern = Pattern.compile(regex);
		Matcher matcher = pattern.matcher(text);
		return matcher;
	}
	
	public LocalDate getBirthDate() {
		Element parent = this.espnPlayerPageDocument.getBirthDateRow();
		String text = normalizeBirthDateStringFromElement(parent);			
		String[] dateArr = StringUtils.getStringArrBySplitter(text, " ");
		LocalDate birthDate = parseBirthDateFromStringArr(dateArr);
		return birthDate;
	}
	
	public static String normalizeBirthDateStringFromElement(Element e) {
		String text = e.text();
		text = text.replace("Born", "").trim();
		text = text.split("in")[0].trim();									
		return text;
	}
	
	public static LocalDate parseBirthDateFromStringArr(String[] arr) {
		LocalDate birthDate = LocalDate.of(
				Integer.parseInt(arr[2].trim()), 
				PlayerDataUtils.getMonthFromAbbreviation(arr[0].trim()), 
				Integer.parseInt(arr[1].replace(",", "").trim()));
		return birthDate;
	}
	
	public String getImageUrl() {
		Attribute src = this.espnPlayerPageDocument.getHeadshotImgSrcAttribute();
		String value = src.getValue();
		return value;
	}
}
