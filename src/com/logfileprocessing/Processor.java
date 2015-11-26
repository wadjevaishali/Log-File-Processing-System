package com.logfileprocessing;

public class Processor implements Runnable {

	static long count = 1;

	@Override
	public void run() {
		while (!LogFileProcessorPnC.doneReading
				|| !LogFileProcessorPnC.ReaderProcessorQueue.isEmpty()) {
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

		LogFileProcessorPnC.doneProcessing = true;
	}
}
