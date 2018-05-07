package com.jsull.page;

import java.util.List;

import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;

public class EspnSearchResultsPage extends Page {
	
	public static String playerLinkXpath = "//a[contains(@href,'nfl') and contains(@href,'player') and contains(text(),'Stats, News')]";
	public static String nextPageXpath = "//div[@class='gsc-cursor']/div[text()='#']";
	
	public EspnSearchResultsPage(WebDriver driver) {
		super(driver);
	}
	
	public EspnDetailsPage getDetailsPage() throws InterruptedException { 
		Thread.sleep(5000);
		List<WebElement> elements;
		String url = "";
		FIVETIMESLOOP:
		for (int i=1; i<=5; i++) {
			elements = driver.findElements(By.xpath(playerLinkXpath));
			for (WebElement e : elements) {
				String link = e.getAttribute("href");
				if (link.contains("nfl/player")) {
					url = e.getAttribute("data-cturl").split("q=")[1];
					System.out.println(String.format("url = %s", url));
					break FIVETIMESLOOP;
				}
			}
			clickByXpath(nextPageXpath.replace("#", "" + (i+1)));
			Thread.sleep(5000);
		}
		goTo(url);
		return new EspnDetailsPage(this.driver);
	}

}
