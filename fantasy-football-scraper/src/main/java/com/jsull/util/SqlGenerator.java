package com.jsull.util;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Properties;

import com.jsull.entity.FantasyWeek;
import com.jsull.entity.Game;
import com.jsull.entity.GameStats;
import com.jsull.entity.PassDetails;
import com.jsull.entity.Player;
import com.jsull.entity.RushDetails;
import com.jsull.entity.SnapDetails;

public class SqlGenerator {
	
	private static final String propsFile = "Resources/db-application.properties";
	private static final Properties props = new Properties();
	static {
		try {
			props.load(new FileInputStream(new File(propsFile)));
		} catch (Exception e) {}
	}
	
	// todo: create a properties file and read all table/column names from it
	public static final String PLAYER_TABLE = "player";
	public static final String FANTASY_WEEK_TABLE = "fantasy_week";
	public static final String GAME_TABLE = "game";
	public static final String GAME_STATS_TABLE = "game_stats";
	public static final String PASS_DETAILS_TABLE = "pass_details";
	public static final String RUSH_DETAILS_TABLE = "rush_details";
	public static final String SNAP_DETAILS_TABLE = "snap_details";

	public static String generateSqlForPlayer(Player p) {
		if (p == null)
			return "";
		String sql = String.format(
				"INSERT INTO `%s` "
				+ "(first, last, position, feet, inches, weight, college, number, draft_year, "
				+ "draft_round, draft_pick, draft_team, birth_date, image_url) "
				+ "VALUES ('%s','%s','%s',%d,%d,%d,'%s',%d,%d,%d,%d,'%s','%s','%s');\n", 
				PLAYER_TABLE, p.getFirst(), p.getLast(), p.getPosition(), p.getFeet(), p.getInches(), p.getWeight(),
				p.getCollege(), p.getNumber(), p.getDraftYear(), p.getDraftRound(), p.getDraftPick(),
				p.getDraftTeam(), p.getBirthDate(), p.getImageUrl());
		return sql;
	}
	
	public static void main(String[] args) {
		Player p = new Player();
		p.setFirst("josh");p.setLast("Sullivan");p.setPosition("RB");p.setFeet(6);p.setInches(2);p.setWeight(250);
		p.setCollege("UW-Whitewater");p.setNumber(44);p.setDraftYear(2018);p.setDraftRound(1);p.setDraftPick(25);
		p.setDraftTeam("GB");p.setBirthDate(LocalDate.now());p.setImageUrl("my_image_url");
		Game g = new Game();
		g.setHome("GB");g.setAway("MN");g.setDate(LocalDate.now());g.setTime(LocalTime.now());g.setHomeScore(30);g.setAwayScore(20);g.setWeek(3);
		String gsql = generateSqlForGame(g);
		System.out.println(gsql);
		List<FantasyWeek> fws = new ArrayList<>();
		FantasyWeek fw1 = new FantasyWeek();
		fw1.setDraftkingsSalary(6500);fw1.setFanduelSalary(6500);fw1.setYahooSalary(5500);fw1.setWeek(1);fw1.setYear(2017);
		fws.add(fw1);
		FantasyWeek fw2 = new FantasyWeek();
		fw2.setDraftkingsSalary(5500);fw2.setFanduelSalary(5500);fw2.setYahooSalary(5500);fw2.setWeek(1);fw2.setYear(2017);
		fws.add(fw2);
		p.setFantasyWeeks(fws);
		String sql = generateSqlForFantasyWeek(p, g);
		GameStats gs = new GameStats();
		gs.setPassAttempts(35);gs.setPassYards(300);gs.setPassTouchdowns(3);gs.setPassCompletions(30);gs.setRushAttempts(10);
		gs.setRushYards(30);gs.setFumbles(1);gs.setSacksTaken(4);gs.setSackYards(10);
		sql = generateSqlForGameStats(gs, g);
		System.out.println(sql);
		PassDetails pd = new PassDetails();
		sql = generateSqlForPassDetails(pd, g);
		System.out.println(sql);
		RushDetails rd = new RushDetails(); rd.setRushAttRightEnd(10);rd.setRushTdRightEnd(5);
		sql = generateSqlForRushDetails(rd, g);
		System.out.println(sql);
		SnapDetails sd = new SnapDetails();sd.setNumSnaps(99);
		sql = generateSqlForSnapDetails(sd, g);
		System.out.println(sql);
	}
	
	public static String generateSqlForFantasyWeek(Player p, Game g) { // will be fw, p, g
		List<FantasyWeek> fantasyWeeks = p.getFantasyWeeks();
		String sql = "";
		for (FantasyWeek week : fantasyWeeks) {
			if (week.getWeek() != g.getWeek())		// create helper method
				continue;
			String weekSql = String.format("INSERT INTO `%s` "
					+ "(draftkings_salary, fanduel_salary, yahoo_salary, week, year, player_id, game_id) "
					+ "values (%f, %f, %f, %d, %d, " + 
					"SELECT p.id from %s p where p.first = '%s' AND p.last='%s', " +
					"SELECT g.id from %s g WHERE g.week=%d AND YEAR(g.date)=%d AND g.home_team='%s');\n"
					, FANTASY_WEEK_TABLE, week.getDraftkingsSalary(), week.getFanduelSalary(), week.getYahooSalary(),
					week.getWeek(), week.getYear(), PLAYER_TABLE, p.getFirst(), p.getLast(), GAME_TABLE,
					g.getWeek(), g.getDate().getYear(), g.getHome());	
			sql += weekSql + "\n";
		}
		return sql;
	}
	
	public static String generateSqlForGame(Game g) {
		if (g == null)
			return "";
		String sql = String.format("INSERT INTO `%s` "
				+ "(home_team, away_team, date, time, home_score, away_score, week) "
				+ "VALUES('%s', '%s', '%s', '%s', %d, %d, %d);\n"
				, GAME_TABLE, g.getHome(), g.getAway(), g.getDate(), g.getTime(), g.getHomeScore(), g.getAwayScore(), g.getWeek());
		return sql;
	}
	
	public static String generateSqlForGameStats(GameStats gs, Game g) {
		if (gs == null)
			return "";
		String sql = String.format("INSERT INTO `%s` "
				+ "(pass_completions, pass_attempts, pass_yards, pass_touchdowns, pass_interceptions, "
				+ "pass_long, sacks_taken, sack_yards, rush_attempts, rush_yards, rush_touchdowns, rush_long, "
				+ "targets, receptions, reception_yards, reception_touchdowns, reception_long, fumbles, fumbles_lost, game_id) "
				+ "VALUES(%d, %d, %d, %d, %d, %d, %d, %d, %d, %d, %d, %d, %d, %d, %d, %d, %d, %d, %d, "
				+ "SELECT id FROM `%s` g WHERE YEAR(g.date)=%d AND g.week=%d AND g.home_team='%s');\n"
				, GAME_STATS_TABLE, gs.getPassCompletions(), gs.getPassAttempts(), gs.getPassYards(), gs.getPassTouchdowns(),
				gs.getInterceptions(), gs.getPassLong(), gs.getSacksTaken(), gs.getSackYards(), gs.getRushAttempts(),
				gs.getRushYards(), gs.getRushTouchdowns(), gs.getRushLong(), gs.getTargets(), gs.getReceptions(), gs.getReceptionYards(),
				gs.getReceptionTouchdowns(), gs.getReceptionLong(), gs.getFumbles(), gs.getFumblesLost(), GAME_TABLE,
				g.getDate().getYear(), g.getWeek(), g.getHome());
		return sql;
	}
	
	public static String generateSqlForPassDetails(PassDetails pd, Game g) {
		if (pd == null)
			return "";
		String sql = String.format("INSERT INTO `%s` "
				+ "(rec_targets_short_left, rec_catches_short_left, rec_yards_short_left, rec_td_short_left, rec_targets_short_middle, "
				+ "rec_catches_short_middle, rec_yards_short_middle, rec_td_short_middle, rec_targets_short_right, "
				+ "rec_catches_short_right, rec_yards_short_right, rec_td_short_right, rec_targets_deep_left, rec_catches_deep_left, "
				+ "rec_yards_deep_left, rec_td_deep_left, rec_targets_deep_middle, rec_catches_deep_middle, rec_yards_deep_middle, "
				+ "rec_td_deep_middle, rec_targets_deep_right, rec_catches_deep_right, rec_yards_deep_right, rec_td_deep_right, "
				+ "game_stats_id) "
				+ "VALUES(%d, %d, %d, %d, %d, %d, %d, %d, %d, %d, %d, %d, %d, %d, %d, %d, %d, %d, %d, %d, %d, %d, %d, %d, "
				+ "SELECT gs.id FROM `%s` gs, `%s` g WHERE gs.game_id=g.id AND YEAR(g.date)=%d AND g.week=%d AND g.home_team='%s');\n"
				, PASS_DETAILS_TABLE, pd.getRecTargetsShortLeft(), pd.getRecCatchesShortLeft(), pd.getRecYardsShortLeft(), 
				pd.getRecTdShortLeft(), pd.getRecTargetsShortMiddle(), pd.getRecCatchesShortMiddle(), pd.getRecYardsShortMiddle(), 
				pd.getRecTdShortMiddle(), pd.getRecTargetsShortRight(), pd.getRecCatchesShortRight(), pd.getRecYardsShortRight(), 
				pd.getRecTdShortRight(), pd.getRecTargetsDeepLeft(), pd.getRecCatchesDeepLeft(), pd.getRecYardsDeepLeft(), 
				pd.getRecTdDeepLeft(), pd.getRecTargetsDeepMiddle(), pd.getRecCatchesDeepMiddle(), pd.getRecYardsDeepMiddle(), 
				pd.getRecTdDeepMiddle(), pd.getRecTargetsDeepRight(), pd.getRecCatchesDeepRight(), pd.getRecYardsDeepRight(), 
				pd.getRecTdDeepRight(), GAME_STATS_TABLE, GAME_TABLE, g.getDate().getYear(), g.getWeek(), g.getHome());
		return sql;
	}
	
	public static String generateSqlForRushDetails(RushDetails rd, Game g) {
		if (rd == null)
			return "";
		String sql = String.format("INSERT INTO `%s` "
				+ "(rush_att_left_end, rush_yards_left_end, rush_td_left_end, rush_att_left_tackle, rush_yards_left_tackle, "
				+ "rush_td_left_tackle, rush_att_left_guard, rush_yards_left_guard, rush_td_left_guard, rush_att_mid, "
				+ "rush_yards_mid, rush_td_mid, rush_att_right_guard, rush_yards_right_guard, rush_td_right_guard, "
				+ "rush_att_right_tackle, rush_yards_right_tackle, rush_td_right_tackle, rush_att_right_end, rush_yards_right_end, " 
				+ "rush_td_right_end, game_stats_id) "
				+ "VALUES(%d, %d, %d, %d, %d, %d, %d, %d, %d, %d, %d, %d, %d, %d, %d, %d, %d, %d, %d, %d, %d, "
				+ "SELECT gs.id FROM `%s` gs, `%s` g WHERE gs.game_id=g.id AND YEAR(g.date)=%d AND g.week=%d AND g.home_team='%s');\n"
				, RUSH_DETAILS_TABLE, rd.getRushAttLeftEnd(), rd.getRushYardsLeftEnd(), rd.getRushTdLeftEnd(), 
				rd.getRushAttLeftTackle(), rd.getRushYardsLeftTackle(), rd.getRushTdLeftTackle(), rd.getRushAttLeftGuard(), 
				rd.getRushYardsLeftGuard(), rd.getRushTdLeftGuard(), rd.getRushAttMid(), rd.getRushYardsMid(), rd.getRushTdMid(), 
				rd.getRushAttRightGuard(), rd.getRushYardsRightGuard(), rd.getRushTdRightGuard(), rd.getRushAttRightTackle(), 
				rd.getRushYardsRightTackle(), rd.getRushTdRightTackle(), rd.getRushAttRightEnd(), rd.getRushYardsRightEnd(), 
				rd.getRushTdRightEnd(), GAME_STATS_TABLE, GAME_TABLE, g.getDate().getYear(), g.getWeek(), g.getHome());
		return sql;
	}
	
	public static String generateSqlForSnapDetails(SnapDetails sd, Game g) {
		if (sd == null)
			return "";
		String sql = String.format("INSERT INTO `%s` "
				+ "(num_snaps, snap_percentage, game_stats_id) "
				+ "VALUES(%d, %d, "
				+ "SELECT gs.id FROM `%s` gs, `%s` g WHERE gs.game_id=g.id AND YEAR(g.date)=%d AND g.week=%d AND g.home_team='%s');\n"
				, SNAP_DETAILS_TABLE, sd.getNumSnaps(), sd.getSnapPercentage(), GAME_STATS_TABLE, GAME_TABLE, 
				g.getDate().getYear(), g.getWeek(), g.getHome());
		return sql;
	}
	
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
