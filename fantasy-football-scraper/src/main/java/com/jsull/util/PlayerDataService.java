package com.jsull.util;

import java.util.HashSet;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.openqa.selenium.WebDriver;
import org.openqa.selenium.chrome.ChromeDriver;

import com.jsull.entity.Player;
import com.jsull.page.EspnDetailsPage;
import com.jsull.page.EspnHomePage;
import com.jsull.page.EspnSearchResultsPage;
import com.jsull.page.PlayerListPage;

public class PlayerDataService {
	
	private WebDriver driver;
	
	public PlayerDataService(WebDriver driver) {
		System.setProperty("webdriver.chrome.driver", "chromedriver");
		this.driver = driver;
	}
	
	public HashSet<Player> getPlayerDataByWeek(int start, int numWeeks) {
		PlayerListPage listPage = new PlayerListPage(this.driver);
		return listPage.scrapePlayerDataByWeek(start, numWeeks);
	}
	
	public void getPlayerDetails(HashSet<Player> players) {
		EspnHomePage espnPage = new EspnHomePage(this.driver);
		EspnSearchResultsPage espnSearchResultsPage = null;
		EspnDetailsPage espnDetailsPage = null;
		for (Player p : players) {
			try {
				espnSearchResultsPage = espnPage.searchFor(p.getFirst(), p.getLast());
				espnDetailsPage = espnSearchResultsPage.getDetailsPage();
				espnDetailsPage.collectDetailsFor(p);
				PlayerDataUtils.writePlayerToFile(p);
			} catch (Exception e) {
				System.err.println("Error getting player details for: " 
						+ p.getFirst() + " " + p.getLast());
				System.err.println(e.getMessage());
			}
			espnDetailsPage.goTo(EspnHomePage.url);
		}
	}
	
	public static void main(String[] args) {
		HashSet<Player> players = new HashSet<>();
		players.add(new Player(1, "Jimmy", "Graham", "QB"));
		PlayerDataService service = new PlayerDataService(new ChromeDriver());
		service.getPlayerDetails(players);
//		PlayerDataUtils.createSqlFromSerializedPlayers();
	}

}
