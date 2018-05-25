package com.jsull.entity;

import java.io.Serializable;

public class SnapDetails implements Serializable {

	private int numSnaps;
	private int snapPercentage;
	private GameStats gameStats;
	
	public SnapDetails() {}
	
	public int getNumSnaps() { return this.numSnaps; }
	public void setNumSnaps(int numSnaps) { this.numSnaps = numSnaps; }
	
	public int getSnapPercentage() { return this.snapPercentage; }
	public void setSnapPercentage(int snapPercentage) { this.snapPercentage = snapPercentage; }
	
	public GameStats getGameStats() { return this.gameStats; }
	public void setGameStats(GameStats gameStats) { this.gameStats = gameStats; }
}
