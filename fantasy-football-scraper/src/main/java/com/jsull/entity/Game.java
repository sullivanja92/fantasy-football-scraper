package com.jsull.entity;

import java.io.Serializable;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.List;

public class Game implements Serializable {		// break up date into month, date, year, nfl week

	private static final long serialVersionUID = 1L;
	
	private long id;
	private String home;
	private String away;
	private LocalDate date;
	private LocalTime time;
	private int homeScore;
	private int awayScore;
	private int week;
	private List<FantasyWeek> fantasyWeeks = new ArrayList<>();
	private List<GameStats> gameStats = new ArrayList<>();
	
	public Game() {}
	
	public Long getId() { return this.id; }
	public void setId(Long id) { this.id = id; }
	
	public String getHome() { return this.home; }
	public void setHome(String home) { this.home = home; }
	
	public String getAway() { return this.away; }
	public void setAway(String away) { this.away = away; }
	
	public LocalDate getDate() { return this.date; }
	public void setDate(LocalDate date) { this.date = date; }
	
	public LocalTime getTime() { return this.time; }
	public void setTime(LocalTime time) { this.time = time; }
	
	public int getHomeScore() { return this.homeScore; }
	public void setHomeScore(int homeScore) { this.homeScore = homeScore; }
	
	public int getAwayScore() { return this.awayScore; }
	public void setAwayScore(int awayScore) { this.awayScore = awayScore; }
	
	public int getWeek() { return this.week; }
	public void setWeek(int week) { this.week = week; }
	
	public List<FantasyWeek> getFantasyWeeks() { return this.fantasyWeeks; }
	public void setFantasyWeeks(List<FantasyWeek> fantasyWeeks) { this.fantasyWeeks = fantasyWeeks; }
	
	public List<GameStats> getGameStats() { return this.gameStats; }
	public void setGameStats(List<GameStats> gameStats) { this.gameStats = gameStats; }
	
	@Override
	public boolean equals(Object game) {
		Game g = (Game) game;
		if (!g.getHome().equals(this.home))
			return false;
		if (!g.getDate().equals(this.date))
			return false;
		if (!g.getTime().equals(this.time))
			return false;
		return true;
	}
	
	@Override
	public int hashCode() {
		return (this.home + " " + this.date + " " + this.time).hashCode();
	}
}
