package com.jsull.page;

import java.time.LocalDate;

public interface PlayerDetailsScraper {
	
	int getPlayerFeet();
	int getPlayerInches();
	int getPlayerWeight();
	String getPlayerCollege();
	int getPlayerNumber();
	//String getPlayerDraftInfo();
	Integer getPlayerDraftYear();
	Integer getPlayerDraftRound();
	Integer getPlayerDraftPick();
	String getPlayerDraftTeam();
	LocalDate getPlayerBirthDate();
	String getPlayerImageUrl();

}
