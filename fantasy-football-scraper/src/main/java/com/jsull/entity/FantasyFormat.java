package com.jsull.entity;

public enum FantasyFormat {
	
	DRAFTKINGS("DraftKings"),
	FANDUEL("FanDuel"),
	YAHOO("Yahoo DFS");
	
	private String format;
	
	private FantasyFormat(String format) {
		this.format = format;
	}
	
	public String getFormat() {
		return this.format;
	}
}
