package com.jsull.entity;

import java.util.List;

public class Team {
	
	private String name;
	private String location;
	private String abbreviation;
	private String imageUrl;
	private String stadiumName;
	private String stadiumCity;
	private String stadiumState;
	private List<GameStats> gameStats;
	
	public Team() {}
	
	public Team(String name, String location, String abbreviation) {
		this.name = name;
		this.location = location;
		this.abbreviation = abbreviation;
	}

	public String getName() { return name; }
	public void setName(String name) { this.name = name; }

	public String getLocation() { return this.location; }
	public void setLocation(String location) { this.location = location; }

	public String getAbbreviation() { return abbreviation; }
	public void setAbbreviation(String abbreviation) { this.abbreviation = abbreviation; }
	
	public String getImageUrl() { return this.imageUrl; }
	public void setImageUrl(String url) { this.imageUrl = url; }
	
	public String getStadiumName() {
		return stadiumName;
	}

	public void setStadiumName(String stadiumName) {
		this.stadiumName = stadiumName;
	}

	public String getStadiumCity() {
		return stadiumCity;
	}

	public void setStadiumCity(String stadiumCity) {
		this.stadiumCity = stadiumCity;
	}

	public String getStadiumState() {
		return stadiumState;
	}

	public void setStadiumState(String stadiumState) {
		this.stadiumState = stadiumState;
	}

	public List<GameStats> getGameStats() { return this.gameStats; }
	public void getGameStats(List<GameStats> gameStats) { this.gameStats = gameStats; }

}
