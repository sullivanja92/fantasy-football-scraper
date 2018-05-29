package com.jsull.util;

import java.util.Arrays;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class StringUtils {
	
	private static final Logger log = LogManager.getLogger(StringUtils.class);
	
	public static String stripNonDigits(String string) {
		log.debug(String.format("stripNonDigits() called with arg=%s", string));
		String returnStr = "";
		for (int i=0; i<string.length(); i++) {
			if (Character.isDigit(string.charAt(i)))
				returnStr += string.charAt(i);
		}
		log.debug(String.format("stripNonDigits() return val=%s", string));
		return returnStr;
	}
	
	public static String[] getStringArrBySplitter(String text, String splitter) {
		log.debug(String.format("getStringArrBySplitter() called with text=%s and "
				+ "splitter=%s", text, splitter));
		String[] arr = text.split(splitter);
		log.debug(String.format("getStringArrBySplitter() return val=%s", Arrays.toString(arr)));
		return arr;
	}
	
	public static String generateCSSQueryFromAttrKeyAndVal(String tag, String key, String val) {
		String query = String.format("%s[%s=%s]", tag, key, val);
		return query;
	}
	
	public static int parsePctFromString(String string) {
		string = string.replace("%", "");
		int n = Integer.parseInt(string);
		return n;
	}
}
