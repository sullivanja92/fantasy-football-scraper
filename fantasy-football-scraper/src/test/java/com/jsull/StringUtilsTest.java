package com.jsull;

import org.junit.Test;

import com.jsull.util.StringUtils;

public class StringUtilsTest {
	

	@Test
	public void testNonDigitsStripped() {
		String digitString = "1a2b3c4d5e";
		String nonDigitString = StringUtils.stripNonDigits(digitString);
		String expected = "12345";
		assert(expected.equals(nonDigitString));
//		System.out.println("Passed testNonDigitsStripped");
	}
	
	@Test
	public void testStringArrBySplitterLength() {
		String string = "This is my string that should produce an array with 12 strings.";
		String splitter = " ";
		String[] arr = StringUtils.getStringArrBySplitter(string, splitter);
		assert(arr.length == 12);
		System.out.println("Passed testStringArrBySplitterLength");
	}
}
