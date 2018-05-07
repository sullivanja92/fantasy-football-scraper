package com.jsull.util;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;

import com.jsull.entity.Player;

public class PlayerSqlGenerator {
	
	public static void generatePlayerSQL(ArrayList<Player> players) {
		File file = new File("player_sql.txt");
		FileWriter fileWriter = null;
		PrintWriter printWriter = null;
		try {
			if (!file.exists())
				file.createNewFile();
			fileWriter = new FileWriter(file);
			printWriter = new PrintWriter(fileWriter);
			for (Player p : players) {
				printWriter.println(p.toSql());
			}
		} catch (IOException e) {
			
		} finally {
			if (printWriter != null) {
				printWriter.flush();
				printWriter.close();
			}
		}
	}

}
