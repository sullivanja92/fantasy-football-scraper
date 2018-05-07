package com.jsull.main;

import java.util.HashSet;

import org.openqa.selenium.chrome.ChromeDriver;

import com.jsull.entity.Player;
import com.jsull.util.PlayerDataService;
import com.jsull.util.PlayerDataUtils;

public class PlayerMain {
	
	public static void main(String[] args) {
		PlayerDataService playerDataService = new PlayerDataService(new ChromeDriver());
		HashSet<Player> players = playerDataService.getPlayerDataByWeek(1, 1); //todo: create fantasy data class and add scraped salaries
		playerDataService.getPlayerDetails(players);
		PlayerDataUtils.createSqlFromSerializedPlayers();
	}

}
