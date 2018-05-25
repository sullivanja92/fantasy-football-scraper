package com.jsull;

import java.util.List;

import org.junit.runner.JUnitCore;
import org.junit.runner.Result;
import org.junit.runner.RunWith;
import org.junit.runner.notification.Failure;
import org.junit.runners.Suite;
import org.junit.runners.Suite.SuiteClasses;

@RunWith(Suite.class)
@SuiteClasses({
	StringUtilsTest.class
})
public class TestSuite {

	public static void main(String[] args) {
		Result result = JUnitCore.runClasses(TestSuite.class);
		List<Failure> failures = result.getFailures();
		failures.forEach(System.out::println);
	}
}
