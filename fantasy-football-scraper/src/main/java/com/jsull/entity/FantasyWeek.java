package com.jsull.entity;

public class FantasyWeek {	
	
	private long id;
	private double draftkingsSalary;
	private double fanduelSalary;
	private double yahooSalary;
	private int week;
	private int year;
	//private Player player;
	//private Game game;
	
	public FantasyWeek() {}

	public long getId() { return id; }
	public void setId(long id) { this.id = id; }
	
	public double getDraftkingsSalary() { return draftkingsSalary; }
	public void setDraftkingsSalary(double draftkingsSalary) { this.draftkingsSalary = draftkingsSalary; }

	public double getFanduelSalary() { return fanduelSalary; }
	public void setFanduelSalary(double fanduelSalary) { this.fanduelSalary = fanduelSalary; }

	public double getYahooSalary() { return yahooSalary; }
	public void setYahooSalary(double yahooSalary) { this.yahooSalary = yahooSalary; }
	
	public int getWeek() { return this.week; }
	public void setWeek(int week) { this.week = week; }
	
	public int getYear() { return this.year; }
	public void setYear(int year) { this.year = year; }
	
//	public Player getPlayer() { return this.player; }
//	public void setPlayer(Player player) { this.player = player; }
//	
//	public Game getGame() { return this.game; }
//	public void setGame(Game game) { this.game = game; }
	@Override
	public String toString() {
		return String.format("Year: %d\nWeek: %d\nDraftkings: %f\nFanDuel: %f\nYahoo: %f", year, week, draftkingsSalary, fanduelSalary, yahooSalary);
	}
}
