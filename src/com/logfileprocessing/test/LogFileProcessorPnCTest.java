package com.logfileprocessing.test;

import static org.junit.Assert.assertEquals;

import org.junit.Test;

import com.logfileprocessing.LogFileProcessorPnC;

public class LogFileProcessorPnCTest {

	@Test
	public void invalidThreadCount() {
		String[] args = {"-t=aaa", "-d=E:\\LogFiles"};				
		boolean result = LogFileProcessorPnC.setThreadCount(args);
		assertEquals("Program exited due to invalid thread count", false, result);		
	}
	
	@Test
	public void isDirectoryOrNot() {
		String[] args = {"-t=4", "-d=g:\\LogFiles"};
		boolean result = LogFileProcessorPnC.setLogDirectory(args);
		assertEquals("Program exited due to invalid directory", false, result);		
	}
}
