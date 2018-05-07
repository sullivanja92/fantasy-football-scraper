package com.jsull.util;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.PrintWriter;
import java.time.LocalDateTime;
import java.time.Month;
import java.time.format.DateTimeFormatter;
import java.util.HashSet;

import com.jsull.entity.Player;

public class PlayerDataUtils {
	
	public static final String FILE = "players.ser";
	
	@SuppressWarnings("unchecked")
	public static boolean writePlayerToFile(Player p) {
		boolean written = true;
		File file = null;
		ObjectOutputStream objectOutputStream = null;
		ObjectInputStream objectInputStream = null;
		try {
			file = new File(FILE);
			HashSet<Player> players = null;
			if (!file.exists()) {
				file.createNewFile();
				players = new HashSet<>();
			} else {
				objectInputStream = new ObjectInputStream(new FileInputStream(file));
				players = (HashSet<Player>) objectInputStream.readObject();
			}
			players.add(p);
			objectOutputStream = new ObjectOutputStream(new FileOutputStream(file));
			objectOutputStream.writeObject(players);
		} catch(Exception e) {
			e.printStackTrace();
			written = false;
		} finally {
			if (objectInputStream != null)
				try {
					objectInputStream.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
			if (objectOutputStream != null)
				try {
					objectOutputStream.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
		}
		return written;
	}
	
	public static void createSqlFromSerializedPlayers() {
		try (ObjectInputStream ois = new ObjectInputStream(new FileInputStream(new File(FILE))); 
				BufferedWriter bufferedWriter = new BufferedWriter(new FileWriter(new File(generateFileName("player_sql.txt"))))) {
			@SuppressWarnings("unchecked")
			HashSet<Player> players = (HashSet<Player>) ois.readObject();
			for (Player p : players) {
				bufferedWriter.write(p.toSql());
				bufferedWriter.newLine();
			}
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		} catch (ClassNotFoundException e) {
			e.printStackTrace();
		}
	}
	
	public static String generateFileName(String base) {
		LocalDateTime dateTime = LocalDateTime.now();
		DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyyMMdd_HHmmss");
		String fileName = dateTime.format(formatter) + "_" + base;
		return fileName;
	}
	
	public static void generatePlayerSQL(HashSet<Player> players) {
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
	
	public static Month getMonthFromAbbreviation(String abbreviation) {
		switch (abbreviation) {
		case "Jan":
			return Month.JANUARY;
		case "Feb":
			return Month.FEBRUARY;
		case "Mar":
			return Month.MARCH;
		case "Apr":
			return Month.APRIL;
		case "May":
			return Month.MAY;
		case "Jun":
			return Month.JUNE;
		case "July":
			return Month.JULY;
		case "Aug":
			return Month.AUGUST;
		case "Sep":
			return Month.SEPTEMBER;
		case "Oct":
			return Month.OCTOBER;
		case "Nov":
			return Month.NOVEMBER;
		case "Dec":
			return Month.DECEMBER;
		default:
			System.out.println("abbreviation = " + abbreviation);
			return null;
		}
	}
	
	public String getAlternatePossibleName(String name) {
		if (name.equalsIgnoreCase("josh"))
			return "Joshua";
		if (name.equalsIgnoreCase("joshua"))
			return "Josh";
		return null;
	}

}
