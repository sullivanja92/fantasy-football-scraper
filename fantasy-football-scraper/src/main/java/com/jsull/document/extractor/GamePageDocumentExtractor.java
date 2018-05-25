package com.jsull.document.extractor;

import java.util.ArrayList;
import java.util.List;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Comment;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.nodes.Node;
import org.jsoup.select.Elements;

import com.jsull.document.JsoupExtractor;
import com.jsull.util.StringUtils;

public class GamePageDocumentExtractor extends JsoupExtractor {
	
	public static final String ELEMENT_WEEK_TEXT = "All Week";
	public static final String TEAM_NAME_ATTRIBUTE_KEY = "itemprop";
	public static final String TEAM_NAME_ATTRIBUTE_VALUE = "name";
	public static final String GAME_SCORE_CLASS = "score";
	public static final String GAME_TIME_REGEX = ":\\s*\\d+:\\d{2}[a|p]m.*";
	public static final String GAME_DATE_REGEX = "\\w{6,9}\\s\\w{3}\\s\\d+,\\s\\d{4}";
	public static final String PLAYER_OFFENSE_TABLE_ID = "player_offense";
	public static final String PLAYER_PASS_TABLE_ID = "targets_directions";
	public static final String PLAYER_RUSH_TABLE_ID = "targets_directions";
	public static final String HOME_SNAP_TABLE_ID = "home_snap_counts";
	public static final String AWAY_SNAP_TABLE_ID = "away_snap_counts";
	public static final String SNAP_CELL_ATTRIBUTE_KEY = "data-stat";
	public static final String SNAP_NUM_ATTRIBUTE_VALUE = "offense";
	public static final String SNAP_PCT_ATTRIBUTE_VALUE = "off_pct";
	
	private List<Comment> comments = new ArrayList<>();
	
	public GamePageDocumentExtractor(String url) {
		super(url);
		Elements elements = this.doc.getAllElements();
		for (Element e : elements) {
			for (Node n : e.childNodes()) {
				if (n instanceof Comment) {
					comments.add((Comment) n);
				}
			}
		}
	}
	
	public Elements getGameWeekElements() {
		Elements els = getElementsByAttributeValueStartingWith("data-label", ELEMENT_WEEK_TEXT);
		return els;
	}
	
	public Elements extractTeamLinks() {
		Elements elements = 
				getElementsByAttributeValueEqualing(TEAM_NAME_ATTRIBUTE_KEY, TEAM_NAME_ATTRIBUTE_VALUE);
		elements = filterElementsByTag(elements, "a");
		return elements;
	}
	
	public Elements extractGameScoreElements() {
		Elements scoreElements = elementsByClass(GAME_SCORE_CLASS);
		return scoreElements;
	}
	
	public Element getGameTimeElement() {
		Elements elements = getElementsWithOwnTextMatchingRegex(GAME_TIME_REGEX);
		Element element = elements.get(0);
		return element;
	}
	
	public Element getGameDateElement() {
		Elements elements = getElementsWithOwnTextMatchingRegex(GAME_DATE_REGEX);
		Element element = elements.get(0);
		return element;
	}
	
	public Elements extractGameStatsRows() {
		Document table = Jsoup.parse(this.comments.get(0).getData());
		Elements rows = table.getElementsByTag("tr");
		return rows;
	}
	
	private Elements extractAllRowsFromTable(Element e) {
		return e.getElementsByTag("tr");
	}
	
	public Elements extractAllCellsFromRow(Element row) {
		return row.getElementsByTag("td");
	}
	
	public Elements extractRushDetailsTableRows() {
		Elements rows = getRowsFromCommentedTableById(PLAYER_RUSH_TABLE_ID);
		return rows;
	}
	
	public Elements extractPassDetailsTableRows() {
		Elements rows = getRowsFromCommentedTableById(PLAYER_PASS_TABLE_ID);
		return rows;
	}
	
	public Elements getRowsFromCommentedTableById(String id) {
		Document table = getTableFromCommentsById(id);
		Elements rows = extractAllRowsFromTable(table);
		return rows;
	}
	
	public Document getTableFromCommentsById(String id) {
		for (Comment c : this.comments) {
			String data = c.getData();
			if (data.contains(id)) {
				return Jsoup.parse(data);
			}
		}
		return null;
	}
	
	public Elements extractSnapDetailsTableRows() {
		Elements rows = getRowsFromCommentedTableById(HOME_SNAP_TABLE_ID);
		Elements awayRows = getRowsFromCommentedTableById(AWAY_SNAP_TABLE_ID);
		rows.addAll(awayRows);
		return rows;
	}
	
	public Element extractSnapNumCellFromRow(Element row) {
		Element cell = extractCellFromRowByAttribute(row, SNAP_CELL_ATTRIBUTE_KEY, SNAP_NUM_ATTRIBUTE_VALUE);
		return cell;
	}
	
	public Element extractSnapPercentageCellFromRow(Element row) {
		Element cell = extractCellFromRowByAttribute(row, SNAP_CELL_ATTRIBUTE_KEY, SNAP_PCT_ATTRIBUTE_VALUE);
		return cell;
	}
	
	public Element extractCellFromRowByAttribute(Element row, String attributeKey, String attributeVal) {
		String query = StringUtils.generateCSSQueryFromAttrKeyAndVal("td", attributeKey, attributeVal);
		Element cell = selectFromElementByQuery(row, query).get(0);
		return cell;
	}
}
