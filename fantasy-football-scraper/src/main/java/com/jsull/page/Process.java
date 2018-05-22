package com.jsull.page;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.openqa.selenium.chrome.ChromeDriver;

import com.jsull.document.extractor.GamePageDocumentExtractor;
import com.jsull.document.extractor.GoogleSearchDocumentExtractor;
import com.jsull.document.parser.EspnPlayerPageDocumentParser;
import com.jsull.document.parser.GameListPageDocumentParser;
import com.jsull.entity.FantasyWeek;
import com.jsull.entity.Game;
import com.jsull.entity.GameStats;
import com.jsull.entity.PassDetails;
import com.jsull.entity.Player;
import com.jsull.entity.RushDetails;
import com.jsull.util.PlayerDataUtils;

public class Process {
	
	public String getEspnLinkForPlayer(String first, String last) {
		Player p = new Player();
		p.setFirst(first);
		p.setLast(last);
		String googleLink = GoogleSearchDocumentExtractor.generateGoogleSearchLinkForPlayer(p);
		GoogleSearchDocumentExtractor g = new GoogleSearchDocumentExtractor(googleLink);
		return g.getEspnLinkForPlayer(p);
	}
	
	public static void main(String[] args) {
		scrapePlayerData(1, 1);
	}
	
	public static void scrapePlayerData(int startWeek, int numWeeks) { // include year
		// can comment out once have all players wanted
		FantasyWeekPage fwp = new FantasyWeekPage(new ChromeDriver());
		Map<String, Player> players = fwp.scrapePlayerData(startWeek, numWeeks);
		String playersFileName = PlayerDataUtils.serializePlayers(players);
		System.out.println(String.format("%d players scraped.", players.size()));
		//////////////////////////////////////////
		//String playersFileName = xxxxxxx;
		//Map<String, Player> players = PlayerDataUtils.readSerializedPlayers(playersFileName);
		
		// can comment out once have links
		Map<String, String> espnLinkMap = new HashMap<>();
		for (Map.Entry<String, Player> entry : players.entrySet()) {
			Player p = entry.getValue();
			GoogleSearchDocumentExtractor gsd = new GoogleSearchDocumentExtractor(p);
			String espnUrl = gsd.getEspnLinkForPlayer();
			String name = p.getFirst() + " " + p.getLast();
			espnLinkMap.put(name, espnUrl);
		}
		String espnLinkFileName = PlayerDataUtils.serializeLinks(espnLinkMap);
		///////////////////////////
		
		//String espnLinkFileName = xxxxxxx;
		Map<String, String> links = PlayerDataUtils.readSerializedLinks(espnLinkFileName);
		for (Map.Entry<String, Player> entry : players.entrySet()) {
			Player p = entry.getValue();
			String espnUrl = links.get(p.getFirst() + " " + p.getLast());
			EspnPlayerPageDocumentParser espnPlayerPageParser = new EspnPlayerPageDocumentParser(espnUrl);
			espnPlayerPageParser.setPlayerDetailsFor(p);
			System.out.println(p);
		}
		List<String> gameUrls = new ArrayList<>();
		for (int i=startWeek; i<startWeek+numWeeks; i++)  {
			String gameListUrl = GameListPageDocumentParser.getGameListUrlByYearAndWeek(2017, i);
			GameListPageDocumentParser gameListPage = new GameListPageDocumentParser(gameListUrl);
			gameUrls.addAll(gameListPage.getGameUrls());
		}
		for (String gameUrl : gameUrls) {
			GamePageDocumentExtractor gpd = new GamePageDocumentExtractor(gameUrl);
			Game game = gpd.getGame();
			Map<String, GameStats> gameStatsMap = gpd.getGameStats();		// consider storing these in Map<Map<String, Stats>>
			Map<String, RushDetails> rushDetailsMap = gpd.extractPlayerRushDetails();
			Map<String, PassDetails> passDetailsMap = gpd.extractPlayerPassDetails();
			for (Map.Entry<String, GameStats> entry : gameStatsMap.entrySet()) {
				String playerName = entry.getKey();
				GameStats gs = entry.getValue();
				if (rushDetailsMap.containsKey(playerName))
					gs.setRushDetails(rushDetailsMap.get(playerName));
				if (passDetailsMap.containsKey(playerName))
					gs.setPassDetails(passDetailsMap.get(playerName));
				game.getGameStats().add(gs);
				if (players.containsKey(playerName)) {
					Player p = players.get(playerName);
					FantasyWeek fw = p.getFantasyWeekByWeekNum(game.getWeek());
					fw.setGame(game);
				}
			}
		}
		PlayerDataUtils.serializePlayers(players);
	}

}
