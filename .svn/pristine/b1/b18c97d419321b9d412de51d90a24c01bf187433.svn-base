package com.logfileprocessing;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

public class LogFilesProcessingImpl {

	public static void main(String[] args) {

		Calendar cal = Calendar.getInstance();
		Timestamp initialTimestamp = new Timestamp(cal.getTime().getTime());
		System.out.println("Initial timestamp: " + initialTimestamp);

		File folder = new File("E:\\logfiles");
		File[] files = folder.listFiles();
		ExecutorService executor = Executors.newFixedThreadPool(10);

		try {
			for (int i = 0; i < files.length; i++) {
				Future<String> future = executor
						.submit(new ProcessFileOperation(files[i]));
				System.out.println(future.get());
			}
		} catch (Exception ex) {
			System.out.println(ex);
		} finally {
			executor.shutdown();
			cal = Calendar.getInstance();
			Timestamp currentTimestamp = new Timestamp(cal.getTime().getTime());
			System.out.println("Current timestamp: " + currentTimestamp);
			long tsDiff = currentTimestamp.getTime()
					- initialTimestamp.getTime();
			System.out.println("Program execution time: " + tsDiff);
		}
	}
}

class ProcessFileOperation implements Callable<String> {

	static int count = 0;
	private File file;

	public ProcessFileOperation(File file) {
		this.file = file;
	}

	// @Override
	// public void run() {
	// if (file.isFile()) {
	// try {
	// BufferedReader reader = new BufferedReader(new FileReader(file));
	// String line = null;
	// List<String> listofLines = new ArrayList<String>();
	//
	// while ((line = reader.readLine()) != null) {
	// StringBuilder sb = new StringBuilder();
	// listofLines.add(sb.append(++count).append(".").append(line)
	// .append("\r\n").toString());
	// }
	// reader.close();
	//
	// BufferedWriter writer = new BufferedWriter(new FileWriter(file));
	// for (String str : listofLines)
	// writer.write(str);
	//
	// writer.close();
	// System.out.println("Done with writing to: " + file +
	// Thread.currentThread().getName());
	//
	// } catch (FileNotFoundException e) {
	// System.out.println("Unable to open file: " + file);
	// } catch (IOException e) {
	// System.out.println("Unable to read file: " + file);
	// e.printStackTrace();
	// }
	// }

	@Override
	public String call() {
		if (file.isFile()) {
			try {
				BufferedReader reader = new BufferedReader(new FileReader(file));
				String line = null;
				List<String> listofLines = new ArrayList<String>();

				while ((line = reader.readLine()) != null) {
					StringBuilder sb = new StringBuilder();
					listofLines.add(sb.append(++count).append(".").append(line)
							.append("\r\n").toString());
				}
				reader.close();

				BufferedWriter writer = new BufferedWriter(new FileWriter(file));
				for (String str : listofLines)
					writer.write(str);

				writer.close();
				// System.out.println("Done with writing to: " + file +
				// Thread.currentThread().getName());

			} catch (FileNotFoundException e) {
				System.out.println("Unable to open file: " + file);
			} catch (IOException e) {
				System.out.println("Unable to read file: " + file);
				e.printStackTrace();
			}

			return "Done with writing to file: " + file;
		}

		return file + " is not a file";
	}
}
