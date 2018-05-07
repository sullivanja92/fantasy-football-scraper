package com.jsull.page;

import java.time.LocalDate;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.openqa.selenium.WebDriver;

import com.jsull.entity.Player;
import com.jsull.util.PlayerDataUtils;

public class EspnDetailsPage extends Page implements PlayerDetailsScraper {
	
	public static String heightAndWeightXpath = "//ul[@class='general-info']/li[2]";
	public static String collegeXpath = "//span[text()='College']/parent::li";
	public static String numberXpath = "//ul[@class='general-info']/li[@class='first']";
	public static String draftXpath = "//span[text()='Drafted']/parent::li";
	public static String birthDateXpath = "//span[text()='Born']/parent::li";
	public static String imageUrlXpath = "//div[@class='main-headshot']/img";
	public static String homeXpath = "//a[@href='http://www.espn.com']";
	
	public static final String draftRoundRegex = "(\\d{4}:\\s+)(\\d+\\w{2})";
	public static final String draftPickRegex = "(, )(\\d+)(\\w{2})";
	public static final String draftTeamRegex = "(by\\s+)(\\w+)";
	
	public EspnDetailsPage(WebDriver driver) {
		super(driver);
	}
	
	public EspnHomePage goHome() {
		clickByXpath(homeXpath);
		return new EspnHomePage(driver);
	}
	
	public void collectDetailsFor(Player p) {
		p.setFeet(getPlayerFeet());
		p.setInches(getPlayerInches());
		p.setWeight(getPlayerWeight());
		p.setCollege(getPlayerCollege());
		p.setNumber(getPlayerNumber());
		//p.setDraftInfo(getPlayerDraftInfo());
		p.setDraftYear(getPlayerDraftYear());	// improve this logic -> only check for presence once...
		p.setDraftRound(getPlayerDraftRound()); 
		p.setDraftPick(getPlayerDraftPick());
		p.setDraftTeam(getPlayerDraftTeam());
		p.setBirthDate(getPlayerBirthDate());
		p.setImageUrl(getPlayerImageUrl());
		System.out.println(p.toString());
	}
	
	@Override
	public int getPlayerFeet() {
		String heightAndWeight = getTextByXpath(heightAndWeightXpath);
		return Integer.parseInt(heightAndWeight.split("'")[0].trim());
	}
	
	@Override
	public int getPlayerInches() {
		String heightAndWeight = getTextByXpath(heightAndWeightXpath);
		return Integer.parseInt(heightAndWeight.split("\"")[0].split("'")[1].trim());
	}
	
	@Override
	public int getPlayerWeight() {
		String heightAndWeight = getTextByXpath(heightAndWeightXpath);
		return Integer.parseInt(heightAndWeight.split(",")[1].replaceAll("lbs", "").trim());
	}
	
	@Override
	public String getPlayerCollege() {
		String college = getTextByXpath(collegeXpath);
		return college.replace("College", "").trim();
	}
	
	@Override
	public int getPlayerNumber() {
		String number = getTextByXpath(numberXpath);
		return Integer.parseInt(number.split(" ")[0].replace("#", "").trim());
	}
	
//	@Override
//	public String getPlayerDraftInfo() {
//		String draftInfo;
//		try {
//			draftInfo = getTextByXpath(draftXpath);
//			draftInfo = draftInfo.replace("Drafted", "").trim();
//		} catch (Exception e) {
//			draftInfo = "Undrafted";
//		}
//		return draftInfo;
//	}
	
	@Override
	public Integer getPlayerDraftYear() {
		if (checkPresenceByXpath(draftXpath)) {
			String draftInfo = getTextByXpath(draftXpath).replace("Drafted","").trim();
			Integer year = Integer.valueOf(draftInfo.split(":")[0].trim());
			return year;
		} else {
			return null;
		}
	}
	
	@Override
	public Integer getPlayerDraftRound() {
		if (checkPresenceByXpath(draftXpath)) {
			String draftInfo = getTextByXpath(draftXpath).replace("Drafted","").trim();
			Pattern pattern = Pattern.compile(draftRoundRegex);
			Matcher matcher = pattern.matcher(draftInfo);
			Integer round;
			if (matcher.find()) {
				round = Integer.valueOf(stripNonDigits(matcher.group(2)));	
			} else {
				round = null;
			}
			return round;
		} else {
			return null;
		}
	}
	
	@Override
	public Integer getPlayerDraftPick() {
		if (checkPresenceByXpath(draftXpath)) {
			String draftInfo = getTextByXpath(draftXpath).replaceAll("Drafted", "").trim();
			Pattern pattern = Pattern.compile(draftPickRegex);
			Matcher matcher = pattern.matcher(draftInfo);
			Integer pick = null;
			if (matcher.find()) {
				pick = Integer.valueOf(matcher.group(2));
			}
			return pick;
		} else
			return null;
	}
	
	@Override
	public String getPlayerDraftTeam() {
		if (checkPresenceByXpath(draftXpath)) {
			String draftInfo = getTextByXpath(draftXpath).replaceAll("Drafted", "").trim();
			Pattern pattern = Pattern.compile(draftTeamRegex);
			Matcher matcher = pattern.matcher(draftInfo);
			String team = null;
			if (matcher.find()) {
				team = matcher.group(2);
			}
			return team;
		} else
			return null;
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

	@Override
	public String getPlayerImageUrl() {
		return getAttributeValueFromXpath(imageUrlXpath, "src");
	}

	@Override
	public LocalDate getPlayerBirthDate() {
		String date = getTextByXpath(birthDateXpath);
		date = date.replace("Born", "");
		date = date.split("in")[0];
		String[] dateArr = date.split(" ");
		LocalDate birthDate = LocalDate.of(
				Integer.parseInt(dateArr[2].trim()), 
				PlayerDataUtils.getMonthFromAbbreviation(dateArr[0].trim()), 
				Integer.parseInt(dateArr[1].replace(",", "").trim()));
		return birthDate;
	}

}
