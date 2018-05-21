package com.jsull.entity;

public class GameStats {

	private long id;
	private int passCompletions;
	private int passAttempts;
	private int passYards;
	private int passTouchdowns;
	private int interceptions;
	private int passLong;
	private int sacksTaken;
	private int sackYards;
	private int rushAttempts;
	private int rushYards;
	private int rushTouchdowns;
	private int rushLong;
	private int targets;
	private int receptions;
	private int receptionYards;
	private int receptionTouchdowns;
	private int receptionLong;
	private int fumbles;
	private int fumblesLost;
	private Game game;
	private Team team;
	private RushDetails rushDetails;
	private PassDetails passDetails;
	
	public GameStats() {}

	public long getId() { return id; }
	public void setId(long id) { this.id = id; }

	public int getPassCompletions() { return passCompletions; }
	public void setPassCompletions(int passCompletions) { this.passCompletions = passCompletions; }

	public int getPassAttempts() { return passAttempts; }
	public void setPassAttempts(int passAttempts) { this.passAttempts = passAttempts; }

	public int getPassYards() { return passYards; }
	public void setPassYards(int passYards) { this.passYards = passYards; }

	public int getPassTouchdowns() { return passTouchdowns; }
	public void setPassTouchdowns(int passTouchdowns) { this.passTouchdowns = passTouchdowns; }

	public int getInterceptions() { return interceptions; }
	public void setInterceptions(int interceptions) { this.interceptions = interceptions; }
	
	public int getPassLong() { return this.passLong; }
	public void setPassLong(int passLong) { this.passLong = passLong; }

	public int getSacksTaken() { return sacksTaken; }
	public void setSacksTaken(int sacksTaken) { this.sacksTaken = sacksTaken; }

	public int getSackYards() { return sackYards; }
	public void setSackYards(int sackYards) { this.sackYards = sackYards; }

	public int getRushAttempts() { return rushAttempts; }
	public void setRushAttempts(int rushAttempts) { this.rushAttempts = rushAttempts; }

	public int getRushYards() { return rushYards; }
	public void setRushYards(int rushYards) { this.rushYards = rushYards; }

	public int getRushTouchdowns() { return rushTouchdowns; }
	public void setRushTouchdowns(int rushTouchdowns) { this.rushTouchdowns = rushTouchdowns; }

	public int getRushLong() { return rushLong; }
	public void setRushLong(int rushLong) { this.rushLong = rushLong; }

	public int getTargets() { return targets; }
	public void setTargets(int targets) { this.targets = targets; }

	public int getReceptions() { return receptions; }
	public void setReceptions(int receptions) { this.receptions = receptions; }

	public int getReceptionYards() { return receptionYards; }
	public void setReceptionYards(int receptionYards) { this.receptionYards = receptionYards; }

	public int getReceptionTouchdowns() { return receptionTouchdowns; }
	public void setReceptionTouchdowns(int receptionTouchdowns) { this.receptionTouchdowns = receptionTouchdowns; }

	public int getReceptionLong() { return receptionLong; }
	public void setReceptionLong(int receptionLong) { this.receptionLong = receptionLong; }

	public int getFumbles() { return fumbles; }
	public void setFumbles(int fumbles) { this.fumbles = fumbles; }

	public int getFumblesLost() { return fumblesLost; }
	public void setFumblesLost(int fumblesLost) { this.fumblesLost = fumblesLost; }
	
	public Game getGame() { return game; }
	public void setGame(Game game) { this.game = game; }
	
	public Team getTeam() { return team; }
	public void setTeam(Team team) { this.team = team; }
	
	public RushDetails getRushDetails() { return this.rushDetails; }
	public void setRushDetails(RushDetails rushDetails) { this.rushDetails = rushDetails; }
	
	public PassDetails getPassDetails() { return this.passDetails; }
	public void setPassDetails(PassDetails passDetails) { this.passDetails = passDetails; } 
}
