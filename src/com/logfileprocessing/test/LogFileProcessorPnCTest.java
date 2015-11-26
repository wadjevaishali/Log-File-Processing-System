package com.logfileprocessing.test;

import static org.junit.Assert.assertEquals;

import org.junit.Test;

import com.logfileprocessing.LogFileProcessorPnC;

public class LogFileProcessorPnCTest {

	@Test
	public void invalidThreadCount() {
		String[] args = {"-t=aaa", "-d=E:\\LogFiles"};
		int result = LogFileProcessorPnC.main(args);
		assertEquals("Program exited due to invalid thread count", 0, result);		
	}
	
	@Test
	public void isDirectoryOrNot() {
		String[] args = {"-t=4", "-d=g:\\LogFiles"};
		int result = LogFileProcessorPnC.main(args);
		assertEquals("Program exited due to invalid directory", 0, result);		
	}
	

}
