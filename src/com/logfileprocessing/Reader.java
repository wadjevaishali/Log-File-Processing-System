package com.logfileprocessing;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.DirectoryStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

public class Reader implements Runnable {

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

		LogFileProcessorPnC.doneReading = true;
		prodExecutor.shutdown();
	}
}
