package com.logfileprocessing;

import java.io.File;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.DirectoryStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.LinkedBlockingQueue;

public class LogFileProcessorPnC {

	public static BlockingQueue<LogData> ReaderProcessorQueue = new LinkedBlockingQueue<LogData>();
	public static BlockingQueue<LogData> ProcessorWriterQueue = new LinkedBlockingQueue<LogData>();

	private static int THREADCOUNT;
	private static String LOG_DIRECTORY;
	private final static int DEFAULT_THREADS = 10;

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
		System.out.println("-t=COUNT is Optional. Range is 1 to 100 inclusive. Default is 10");
	}
}

class LogData {
	Path path;
	List<String> lines;

	public LogData(Path path, List<String> lines) {
		this.path = path;
		this.lines = lines;
	}
}

class Reader implements Runnable {

	DirectoryStream<Path> directoryStream = null;
	int tCount;

	public Reader(int threadCount, String logDirectory) {
		try {
			tCount = threadCount;
			directoryStream = Files.newDirectoryStream(Paths.get(logDirectory));
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	@Override
	public void run() {
		ExecutorService prodExecutor = Executors.newFixedThreadPool(tCount);
		List<Future<LogData>> futureObjects = new ArrayList<Future<LogData>>();

		for (Path path : directoryStream) {
			futureObjects.add(prodExecutor.submit(new Callable<LogData>() {

				@Override
				public LogData call() throws Exception {
					LogData data = null;
					try {
						List<String> readLines = Files.readAllLines(path,
								StandardCharsets.UTF_8);
						data = new LogData(path, readLines);
					} catch (IOException e) {
						e.printStackTrace();
					}

					return data;
				}
			}));
		}

		for (Future<LogData> future : futureObjects) {
			try {
				System.out.println(future.get().path + " is reading.");
				LogFileProcessorPnC.ReaderProcessorQueue.put(future.get());
			} catch (InterruptedException | ExecutionException e) {
				e.printStackTrace();
			}
		}

		prodExecutor.shutdown();
	}
}

class Processor implements Runnable {

	static long count = 1;

	@Override
	public void run() {
		while (true) {
			LogData data = null;
			try {
				data = LogFileProcessorPnC.ReaderProcessorQueue.take();
				System.out.println("processing " + data.path);
				for (int i = 0; i < data.lines.size(); i++) {
					StringBuilder sb = new StringBuilder();
					data.lines.set(
							i,
							sb.append(count++).append(".")
									.append(data.lines.get(i)).toString());
				}
				LogFileProcessorPnC.ProcessorWriterQueue.put(data);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		}
	}
}

class Writer implements Runnable {
	int tCount;

	public Writer(int threadCount) {
		tCount = threadCount;
	}

	@Override
	public void run() {
		ExecutorService consExecutor = Executors.newFixedThreadPool(tCount);
		while (!LogFileProcessorPnC.ProcessorWriterQueue.isEmpty()) {
			consExecutor.execute(new Runnable() {
				@Override
				public void run() {
					try {
						LogData data = LogFileProcessorPnC.ProcessorWriterQueue
								.take();
						System.out.println(data.path + " is writing.");
						Files.write(data.path, data.lines,
								StandardOpenOption.CREATE);
					} catch (IOException e) {
						e.printStackTrace();
					} catch (InterruptedException e) {
						e.printStackTrace();
					}
				}
			});
		}

		consExecutor.shutdown();
	}
}
