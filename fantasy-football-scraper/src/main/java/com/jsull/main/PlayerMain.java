package com.jsull.main;

import java.util.List;
import java.util.Map;

import org.openqa.selenium.chrome.ChromeDriver;

import com.jsull.entity.FantasyWeek;
import com.jsull.entity.Player;
import com.jsull.util.PlayerDataService;
import com.jsull.util.PlayerDataUtils;

public class PlayerMain {
	
	public static void main(String[] args) {
		PlayerDataService playerDataService = new PlayerDataService(new ChromeDriver());
		Map<String, Player> players = playerDataService.scrapePlayerDataByWeek(1, 1); //todo: create fantasy data class and add scraped salaries
		String fileName = PlayerDataUtils.serializePlayers(players);
		
		//		for (Map.Entry<String, Player> entry : players.entrySet()) {
//			Player p = entry.getValue();
//			System.out.println("**************************");
//			System.out.println(p.getFirst() + " " + p.getLast());
//			List<FantasyWeek> weeks = p.getFantasyWeeks();
//			for (FantasyWeek week : weeks) {
//				System.out.println(week);
//			}
//		}
	}

}
