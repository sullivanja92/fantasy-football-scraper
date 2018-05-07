package com.jsull.util;

import java.util.HashSet;

import org.openqa.selenium.WebDriver;
import org.openqa.selenium.chrome.ChromeDriver;

import com.jsull.entity.Player;

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
		for (Player p : players) {
			EspnSearchResultsPage espnSearchResultsPage = null;
			EspnDetailsPage espnDetailsPage = null;
			try {
				espnSearchResultsPage = espnPage.searchFor(p.getFirst(), p.getLast());
				espnDetailsPage = espnSearchResultsPage.getDetailsPage();
				espnDetailsPage.collectDetailsFor(p);
				PlayerDataUtils.writePlayerToFile(p);
			} catch (Exception e) {
				System.err.println("Error getting player details for: " 
						+ p.getFirst() + " " + p.getLast());
				System.err.println(e.getMessage());
				espnPage.goTo(EspnHomePage.url);
			}
		}
	}
	
	public int[] getInput(){
	    boolean rightInput = false;
	    int[] nothing = {0,0};
	    while(rightInput == false){
	      try{
	       rightInput = true;
	      int[] XY = {0,0};
	      String coordinates;
	      System.out.println("Insert coordinates (x y) and press enter.");
	      //coordinates = input.nextLine();
	      coordinates = "";
	      XY[1] = Character.getNumericValue(coordinates.charAt(0));
	      XY[0] = Character.getNumericValue(coordinates.charAt(2));
	      return XY;
	     }catch(StringIndexOutOfBoundsException se){
	      System.out.println(se);
	      System.out.println("Try Again with a space between x and y.");
	      rightInput =  false;  

	      return nothing;
	     }

	    }
	    return null;

	}
	
	public static void main(String[] args) {
		HashSet<Player> players = new HashSet<>();
		players.add(new Player(1, "Ty", "Montgomery", "RB"));
		PlayerDataService service = new PlayerDataService(new ChromeDriver());
		service.getPlayerDetails(players);
		PlayerDataUtils.createSqlFromSerializedPlayers();
	}

}
