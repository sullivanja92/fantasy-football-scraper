package com.jsull.page;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.openqa.selenium.chrome.ChromeDriver;

import com.jsull.document.extractor.GoogleSearchDocumentExtractor;
import com.jsull.document.parser.EspnPlayerPageDocumentParser;
import com.jsull.document.parser.GameListPageDocumentParser;
import com.jsull.document.parser.GamePageDocumentParser;
import com.jsull.entity.FantasyWeek;
import com.jsull.entity.Game;
import com.jsull.entity.GameStats;
import com.jsull.entity.PassDetails;
import com.jsull.entity.Player;
import com.jsull.entity.RushDetails;
import com.jsull.entity.SnapDetails;
import com.jsull.util.PlayerDataUtils;
import com.jsull.util.SqlGenerator;

public class Process {
	
	
	// todo: add player_id to game_stats
		// edit fantasyweek sql method
	
	
	
	public String getEspnLinkForPlayer(String first, String last) {
		Player p = new Player();
		p.setFirst(first);
		p.setLast(last);
		String googleLink = GoogleSearchDocumentExtractor.generateGoogleSearchLinkForPlayer(p);
		GoogleSearchDocumentExtractor g = new GoogleSearchDocumentExtractor(googleLink);
		return g.getEspnLinkForPlayer(p);
	}
	
	public static void main(String[] args) {
		System.setProperty("webdriver.chrome.driver", "Resources/chromedriver");
		scrapePlayerData(1, 1);
	}
	
	public static void scrapePlayerData(int startWeek, int numWeeks) { // include year
		
		StringBuilder playerBuilder = new StringBuilder();
		StringBuilder gameBuilder = new StringBuilder();
		StringBuilder fantasyWeekBuilder = new StringBuilder();
		StringBuilder gameStatsBuilder = new StringBuilder();
		StringBuilder rushDetailsBuilder = new StringBuilder();
		StringBuilder passDetailsBuilder = new StringBuilder();
		StringBuilder snapDetailsBuilder = new StringBuilder();
		
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
			System.out.println("Espn URL: " + espnUrl + " for player: " + name);
			try {
				Thread.sleep(1000);
			} catch(Exception e) {}
		}
		String espnLinkFileName = PlayerDataUtils.serializeLinks(espnLinkMap);
		///////////////////////////
		
		//String espnLinkFileName = xxxxxxx;
		
		
		Map<String, String> links = PlayerDataUtils.readSerializedLinks(espnLinkFileName);
		for (Map.Entry<String, Player> entry : players.entrySet()) {
			String name = entry.getKey();
			Player p = entry.getValue();
			String espnUrl = links.get(name);
			EspnPlayerPageDocumentParser espnPlayerPageParser = new EspnPlayerPageDocumentParser(espnUrl);
			espnPlayerPageParser.setPlayerDetailsFor(p);
			System.out.println(p);
			
			playerBuilder.append(SqlGenerator.generateSqlForPlayer(p));
		}
		
		List<String> gameUrls = new ArrayList<>();
		for (int i=startWeek; i<startWeek+numWeeks; i++)  {
			String gameListUrl = GameListPageDocumentParser.getGameListUrlByYearAndWeek(2017, i);
			GameListPageDocumentParser gameListPage = new GameListPageDocumentParser(gameListUrl);
			gameUrls.addAll(gameListPage.getGameUrls());
		}
		
		for (String gameUrl : gameUrls) {
			GamePageDocumentParser gp = new GamePageDocumentParser(gameUrl);
			Game game = gp.getGame();			
			
			gameBuilder.append(SqlGenerator.generateSqlForGame(game));
			
			
			Map<String, GameStats> gameStatsMap = gp.getGameStats();		// consider storing these in Map<Map<String, Stats>>
			Map<String, RushDetails> rushDetailsMap = gp.extractPlayerRushDetails();
			Map<String, PassDetails> passDetailsMap = gp.extractPlayerPassDetails();
			Map<String, SnapDetails> snapDetailsMap = gp.extractSnapDetails();
			
			for (Map.Entry<String, GameStats> entry : gameStatsMap.entrySet()) {
				
				String playerName = entry.getKey();
				
				
				if (!players.containsKey(playerName))
					continue;
					
					
				GameStats gs = entry.getValue();
				
				
				Player p = players.get(playerName);
				FantasyWeek fw = p.getFantasyWeekByWeekNum(game.getWeek());
				fw.setGame(game);
				
				// edit this method
				fantasyWeekBuilder.append(SqlGenerator.generateSqlForFantasyWeek(p, game));
				gameStatsBuilder.append(SqlGenerator.generateSqlForGameStats(gs, game)); // will need player

				if (rushDetailsMap.containsKey(playerName)) {
					RushDetails rd = rushDetailsMap.get(playerName);
					gs.setRushDetails(rd);
					rushDetailsBuilder.append(SqlGenerator.generateSqlForRushDetails(rd, game));
				}
				if (passDetailsMap.containsKey(playerName)) {
					PassDetails pd = passDetailsMap.get(playerName);
					gs.setPassDetails(pd);
					passDetailsBuilder.append(SqlGenerator.generateSqlForPassDetails(pd, game));
				}
				if (snapDetailsMap.containsKey(playerName)) {
					SnapDetails sd = snapDetailsMap.get(playerName);
					gs.setSnapDetails(sd);
					snapDetailsBuilder.append(SqlGenerator.generateSqlForSnapDetails(sd, game));
				}
				game.getGameStats().add(gs);
				
				
//				if (players.containsKey(playerName)) {
//					FantasyWeek fw = p.getFantasyWeekByWeekNum(game.getWeek());
//					fw.setGame(game);
//				}
			}
		}
		// write sql builders to output folder
		
		PlayerDataUtils.serializePlayers(players);
	}

}
