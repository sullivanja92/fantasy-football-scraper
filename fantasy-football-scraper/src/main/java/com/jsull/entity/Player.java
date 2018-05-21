package com.jsull.entity;

import java.io.Serializable;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.format.FormatStyle;
import java.util.List;

public class Player implements Serializable {
	
	private static final long serialVersionUID = 1L;
	
	private long id;
	private String first;
	private String last;
	private String position;
	private Integer feet;
	private Integer inches;
	private Integer weight;
	private String college;
	private Integer number;
	private Integer draftYear;
	private Integer draftRound;
	private Integer draftPick;
	private String draftTeam;
	private LocalDate birthDate;
	private String imageUrl;
	private int hash;
	private List<FantasyWeek> fantasyWeeks;
	
	public Player() {}
	
	public Player(long id, String first, String last, String position) {
		this.first = first;
		this.last = last;
		this.position = position;
		this.hash = (this.first + this.last + this.position).hashCode();
	}
	
	public long getId() { return this.id; }
 	public void setId(long id) { this.id = id; }

	public String getFirst() { return first; }
	public void setFirst(String first) { this.first = first; }

	public String getLast() { return last; }
	public void setLast(String last) { this.last = last; }

	public String getPosition() { return position; }
	public void setPosition(String position) { this.position = position; }
	
	public Integer getFeet() { return feet; }
	public void setFeet(Integer feet) { this.feet = feet; }

	public Integer getInches() {return inches; }
	public void setInches(Integer inches) { this.inches = inches; }

	public Integer getWeight() { return weight; }
	public void setWeight(Integer weight) { this.weight = weight; }

	public String getCollege() { return college; }
	public void setCollege(String college) { this.college = college; }

	public Integer getNumber() { return number; }
	public void setNumber(Integer number) { this.number = number; }
	
//	public void setDraftInfo(String draftInfo) {
//		this.draftInfo = draftInfo;
//	}
//	
//	public String getDraftInfo() {
//		return this.draftInfo;
//	}
	
	public void setDraftYear(Integer draftYear) { this.draftYear = draftYear; }
	public Integer getDraftYear() { return this.draftYear; }
	
	public void setDraftRound(Integer draftRound) { this.draftRound = draftRound; }
	public Integer getDraftRound() { return this.draftRound; }
	
	public void setDraftPick(Integer draftPick) { this.draftPick = draftPick; }
	public Integer getDraftPick() { return this.draftPick; }
	
	public void setDraftTeam(String draftTeam) { this.draftTeam = draftTeam; }
	public String getDraftTeam() { return this.draftTeam; }
	
	public void setBirthDate(LocalDate birthDate) { this.birthDate = birthDate;}
	public LocalDate getBirthDate() { return this.birthDate; }
	
	public void setImageUrl(String imageUrl) { this.imageUrl = imageUrl;}
	public String getImageUrl() { return this.imageUrl; }

	public List<FantasyWeek> getFantasyWeeks() {
		return fantasyWeeks;
	}

	public void setFantasyWeeks(List<FantasyWeek> fantasyWeeks) {
		this.fantasyWeeks = fantasyWeeks;
	}

	public void setHash(int hash) { this.hash = hash; }
	public int getHash() { return this.hash; }
	
	public String toSql() {
		String first = this.first, last = this.last;
		if (first.contains("'")) {
			first = first.replace("'", "''");
			System.out.println(first);
		}
		if (last.contains("'")) {
			last = last.replace("'", "''");
			System.out.println(last);
		}
		return String.format("INSERT INTO player (first, last, position, feet, inches, weight, college,"
				+ " number, draft_year, draft_round, draft_pick, draft_team, birth_date, image_url) VALUES "
				+ "('%s', '%s', '%s', '%d', '%d', '%d', '%s', '%d', '%d', '%d', '%d', '%s', '%s', '%s');", 
				first, last, this.position, this.feet, this.inches, this.weight, this.college, this.number,
				this.draftYear, this.draftRound, this.draftPick, this.draftTeam, this.birthDate, this.imageUrl).replace("'null'", "null");
	}
	
	public FantasyWeek getFantasyWeekByWeekNum(int num) {
		for (int i=0; i<this.fantasyWeeks.size(); i++) {
			if (this.fantasyWeeks.get(i).getWeek() == num)
				return this.fantasyWeeks.get(i);
		}
		return null;
	}
	
	@Override
	public String toString() {
		return String.format("***************************************"
				+ "\nName: %s\nPosition: %s\nFeet: %d\nInches: %d\nWeight: %d\nCollege: %s\n"
				+ "Number: %d\nDraft Year: %s\nDraft Round: %s\nDraft Pick: %d\nDraft Team: %s\n" + 
				//"Birthdate: %s\nImage: %s\nHash: %s\n" + 
				"Hash: %s\n" + 
				"***************************************", 
				this.first + " " + this.last, this.position, this.feet, this.inches, 
				this.weight, this.college, this.number, //this.draftInfo, 
				this.draftYear, this.draftRound, this.draftPick, this.draftTeam,
				//this.birthDate.format(DateTimeFormatter.ofLocalizedDate(FormatStyle.SHORT)), this.imageUrl,
				(this.first + this.last + this.position).hashCode());
	}
	
	@Override
	public boolean equals(Object player) {
		if (!(player instanceof Player))
			return false;
		Player p = (Player) player;
		if (!p.getFirst().equals(this.first))
			return false;
		if (!p.getLast().equals(this.last))
			return false;
//		if (!p.getPosition().equals(this.position))
//			return false;
		return true;
	}
	
	@Override
	public int hashCode() {
		//return (this.first + this.last + this.position).hashCode();
		return (this.first + this.last).hashCode();
	}

}
