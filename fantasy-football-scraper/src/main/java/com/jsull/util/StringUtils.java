package com.jsull.util;

public class StringUtils {
	
	public static String stripNonDigits(String string) {
		String returnStr = "";
		for (int i=0; i<string.length(); i++) {
			if (Character.isDigit(string.charAt(i)))
				returnStr += string.charAt(i);
		}
		return returnStr;
	}
	
	public static String[] getStringArrBySplitter(String text, String splitter) {
		String[] arr = text.split(splitter);
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
