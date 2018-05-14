package com.jsull.page;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.List;


public class Main {
	
	public static void main(String[] args) {
		List<String> gameUrls = new ArrayList<>();
		int year = 2017;
		for (int i=1; i<=1; i++) {
			String url = GameListPageDocument.getGameListUrlByYearAndWeek(year, i);
			GameListPageDocument gameListPage = new GameListPageDocument(url);
			List<String> urls = gameListPage.getGameUrls();
			gameUrls.addAll(urls);
		}
		List<Player> players = new ArrayList<>();
		for (String url : gameUrls) {
			GamePageDocument page = new GamePageDocument(url);
			players.addAll(page.getPlayers());
		}
		for (Player p : players)
			System.out.println(p.toString());
		System.out.println(players.size());
	}
}

class Game {
	int week;
	String home;
	String away;
	int homeScore;
	int awayScore;
	LocalTime time;
	LocalDate date;
	
	public String toString() {
		return String.format("\n\tWeek: %d\n\tHome: %s\n\tAway: %s\n\tDate: %s\n\tTime: %s", week, home, away, date, time);
	}
}

class Player {
	String first;
	String last;
	GameStats stats;
	
	public String toString() {
		return String.format("***********************\nFirst: %s\nLast: %s\n%s", first, last, stats);
	}
}

class GameStats {
	int completions;
	int passAttempts;
	int passYards;
	int passTouchdowns;
	int interceptions;
	int sacks;
	int sackYards;
	int passLong;
	int rushAttempts;
	int rushYards;
	int rushTouchdowns;
	int rushLong;
	int targets;
	int receptions;
	int recYards;
	int recTouchdowns;
	int recLong;
	int fmbl;
	int fl;
	String team;
	Game game;
	
	public String toString() {
		String stats = "";
		stats += String.format("completions: %s\n", completions);
		stats += String.format("passAttempts: %s\n", passAttempts);
		stats += String.format("passYards: %s\n", passYards);
		stats += String.format("passTouchdowns: %s\n", passTouchdowns);
		stats += String.format("interceptions: %s\n", interceptions);
		stats += String.format("sacks: %s\n", sacks);
		stats += String.format("sackYards: %s\n", sackYards);
		stats += String.format("passLong: %s\n", passLong);
		stats += String.format("rushAttempts: %s\n", rushAttempts);
		stats += String.format("rushYards: %s\n", rushYards);
		stats += String.format("rushTouchdowns: %s\n", rushTouchdowns);
		stats += String.format("rushLong: %s\n", rushLong);
		stats += String.format("targets: %s\n", targets);
		stats += String.format("receptions: %s\n", receptions);
		stats += String.format("recYards: %s\n", recYards);
		stats += String.format("recTouchdowns: %s\n", recTouchdowns);
		stats += String.format("recLong: %s\n", recLong);
		stats += String.format("fmbl: %s\n", fmbl);
		stats += String.format("fl: %s\n", fl);
		return stats;
	}
}

class RushDetails {
	
}

class PassDetails {
	
}
