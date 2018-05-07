package com.jsull.page;

import java.util.List;

import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;

public class EspnSearchResultsPage extends Page {
	
	public static String playerLinkXpath = "//a[contains(@href,'nfl') and contains(@href,'player') and contains(text(),'Stats, News')]";
	
	public EspnSearchResultsPage(WebDriver driver) {
		super(driver);
	}
	
	public EspnDetailsPage getDetailsPage() throws InterruptedException { // to do : click through result pages if no link found
		Thread.sleep(5000);
		List<WebElement> elements= driver.findElements(By.xpath(playerLinkXpath));
		String url = "";
		for (WebElement e : elements) {
			String link = e.getAttribute("href");
			if (link.contains("nfl/player")) {
				url = e.getAttribute("data-cturl").split("q=")[1];
				System.out.println(String.format("url = %s", url));
				break;
			}
		}
		goTo(url);
		return new EspnDetailsPage(this.driver);
	}

}
