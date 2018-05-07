package com.jsull.page;

import org.openqa.selenium.Keys;
import org.openqa.selenium.WebDriver;

public class EspnHomePage extends Page {
	
	public static String url = "https://www.espn.com/";
	public static String searchButtonXpath = "//a[@id='global-search-trigger']";
	public static String searchFieldXpath = "//input[@class='search-box']";
	
	public EspnHomePage(WebDriver driver) {
		super(driver);
		goTo(url);
	}
	
	public EspnSearchResultsPage searchFor(String first, String last) {
		String fullName = first + " " + last;
		clickByXpath(searchButtonXpath);
		setTextByXpath(searchFieldXpath, fullName).sendKeys(Keys.RETURN);	
		return new EspnSearchResultsPage(driver);
	}

}
