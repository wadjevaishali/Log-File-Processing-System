package com.logfileprocessing;

import java.io.File;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;

public class LogFileProcessorPnC {

	public static BlockingQueue<LogData> ReaderProcessorQueue = new LinkedBlockingQueue<LogData>();
	public static BlockingQueue<LogData> ProcessorWriterQueue = new LinkedBlockingQueue<LogData>();

	private static int THREADCOUNT;
	private static String LOG_DIRECTORY;
	private final static int DEFAULT_THREADS = 10;

	protected static boolean doneReading = false;
	protected static boolean doneProcessing = false;

	public static void main(String[] args) {
		// LogFileProcessorPnC -t=4 -d="E:\\Files"

		if (args == null || args.length == 0) {
			printHelp();
			return;
		}

		boolean isSet = setLogDirectory(args);
		if (!isSet) {
			System.out.println("Invalid argument or log directory");
			printHelp();
			return;
		}

		isSet = setThreadCount(args);
		if (!isSet) {
			System.out.println("Invalid thread count");
			printHelp();
			return;
		}

		try {
			new Thread(new Reader(THREADCOUNT, LOG_DIRECTORY)).start();
			new Thread(new Processor()).start();
			new Thread(new Writer(THREADCOUNT)).start();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public static boolean setLogDirectory(String[] args) {
		for (String arg : args) {
			if (arg.trim().contains("-d=")) {
				LOG_DIRECTORY = arg.trim().substring(3);
				if (new File(LOG_DIRECTORY).isDirectory())
					return true;
			}
		}

		return false;
	}

	public static boolean setThreadCount(String[] args) {

		for (String arg : args) {
			if (arg.trim().contains("-t=")) {
				try {
					int count = Integer.valueOf(arg.trim().substring(3));
					if (count > 0 && count <= 100) {
						System.out.println("Threads Setting to: " + count);
						THREADCOUNT = count;
						return true;
					} else {
						System.out.println("Thread count out of range");
						return false;
					}

				} catch (Exception e) {
					System.out.println("Invalid Thread Count");
					return false;
				}
			}
		}

		THREADCOUNT = DEFAULT_THREADS;
		System.out.println("Threads setting to dafault: " + THREADCOUNT);
		return true;
	}

	private static void printHelp() {
		System.out.println("Invalid parameters");
		System.out
				.println("Use syntax: LogFileProcessorPnC -d=\"LOG_DIRECTORY\" -t=COUNT");
		System.out.println("-d=\"LOG_DIRECTORY\" is Necessary");
		System.out
				.println("-t=COUNT is Optional. Range is 1 to 100 inclusive. Default is 10");
	}
}