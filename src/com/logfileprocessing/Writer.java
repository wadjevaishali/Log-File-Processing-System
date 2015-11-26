package com.logfileprocessing;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.StandardOpenOption;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class Writer implements Runnable {
	int tCount;

	public Writer(int threadCount) {
		tCount = threadCount;
	}

	@Override
	public void run() {
		ExecutorService consExecutor = Executors.newFixedThreadPool(tCount);

		while (!LogFileProcessorPnC.doneProcessing
				|| !LogFileProcessorPnC.ProcessorWriterQueue.isEmpty()) {
			consExecutor.execute(new Runnable() {
				@Override
				public void run() {
					while (!LogFileProcessorPnC.ProcessorWriterQueue.isEmpty()) {
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
				}
			});
		}

		consExecutor.shutdown();
	}
}
