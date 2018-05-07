package com.jsull.page;

import java.util.ArrayList;

import org.openqa.selenium.By;
import org.openqa.selenium.Keys;
import org.openqa.selenium.NoSuchElementException;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;

public abstract class Page {
	
	protected WebDriver driver;
	private WebDriverWait wait;
	
	public Page(WebDriver driver) {
		this.driver = driver;
		this.wait = new WebDriverWait(this.driver, 30);
	}
	
	public void goTo(String url) {
		this.driver.get(url);
	}
	
	public String getTextByXpath(String xpath) {
		return wait.until(ExpectedConditions.elementToBeClickable(By.xpath(xpath))).getText();
	}
	
	public String getTextByXpathFromWebElement(WebElement element, String xpath) {
		return element.findElement(By.xpath(xpath)).getText();
	}
	
	public String getAttributeValueFromXpath(String xpath, String attribute) {
		return wait.until(ExpectedConditions.elementToBeClickable(By.xpath(xpath))).getAttribute(attribute);
	}
	
	public WebElement clickByXpath(String xpath) {
		//WebElement element = wait.until(ExpectedConditions.elementToBeClickable(By.xpath(xpath)));
		WebElement element = wait.until(ExpectedConditions.visibilityOfElementLocated(By.xpath(xpath)));
		element.click();
		return element;
	}
	
	public WebElement clickByLinkText(String text) {
		WebElement element = this.wait.until(ExpectedConditions.presenceOfElementLocated(By.linkText(text)));
		element.click();
		return element;
	}
	
	public ArrayList<WebElement> collectElementsByXpath(String xpath) {
		return (ArrayList<WebElement>) wait.until(ExpectedConditions.visibilityOfAllElementsLocatedBy(By.xpath(xpath)));
	}
	
	public WebElement setTextByXpath(String xpath, String text) {
		WebElement element = clickByXpath(xpath);
		element.sendKeys(text);
		return element;
	}
	
	public boolean checkPresenceByXpath(String xpath) {
		try {
			driver.findElement(By.xpath(xpath));
			return true;
		} catch (NoSuchElementException e) {
			System.err.println(String.format("xpath: %s is not located on the page", xpath));
		}
		return false;
	}
	
	public WebElement pressEnter(WebElement element) {
		element.sendKeys(Keys.ENTER);
		return element;
	}
	
	public void finish() {
		this.driver.close();
		this.driver.quit();
	}

}
