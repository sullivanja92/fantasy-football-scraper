package com.jsull.util;

import java.io.BufferedWriter;
import java.io.File;
import java.io.PrintWriter;

public class EntityWriter {
	
	public static final String OUTPUT_FOLDER = "output";
	
	public static enum EntityType {
		PLAYER("players_sql.txt"),
		GAME("game_sql.txt"),
		FANTASY_WEEK("fantasy_week_sql.txt"),
		GAME_STATS("game_stats.txt"),
		RUSH_DETAILS("rush_details.txt"),
		PASS_DETAILS("pass_details.txt"),
		SNAP_DETAILS("snap_details.txt");
		
		private String fileName;
		
		private EntityType(String fileName) {
			this.fileName = fileName;
		}
		
		public String getFileName() {
			return OUTPUT_FOLDER + "/" + this.fileName;
		}
	}

	public static void write(StringBuilder builder, EntityType type) {
		String fileName = type.getFileName();
		try (BufferedWriter bw = new BufferedWriter(new PrintWriter(new File(fileName)))) {
			bw.write(builder.toString());
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
}
