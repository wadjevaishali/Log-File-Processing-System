package com.logfileprocessing;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.DirectoryStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class LogFileProcessor {

	public static void main(String[] args) {

		// Calendar cal = Calendar.getInstance();
		// Timestamp initialTimestamp = new Timestamp(cal.getTime().getTime());
		// System.out.println("Initial timestamp: " + initialTimestamp);

		try (DirectoryStream<Path> directoryStream = Files
				.newDirectoryStream(Paths.get("E:\\LogFiles"))) {

			ExecutorService executor = Executors.newFixedThreadPool(3);
			int count = 1;

			for (Path path : directoryStream)
				executor.execute(new LogProcess(new FileId(count++, path)));

		} catch (IOException e) {
			e.printStackTrace();
		}

		// cal = Calendar.getInstance();
		// Timestamp currentTimestamp = new Timestamp(cal.getTime().getTime());
		// System.out.println("Current timestamp: " + currentTimestamp);
		// long tsDiff = currentTimestamp.getTime()
		// - initialTimestamp.getTime();
		// System.out.println("Program execution time: " + tsDiff);
	}

}

class FileId {
	private int _count;

	public synchronized int getCount() {
		return _count;
	}

	private Path _path;

	public synchronized Path getPath() {
		return _path;
	}

	public FileId(int count, Path path) {
		_count = count;
		_path = path;
	}
}

class LogProcess implements Runnable {
	public static int count = 0;

	private static FileId reading;

	private static synchronized FileId getReading() {
		return reading;
	}

	private static FileId processing;

	private static synchronized FileId getProcessing() {
		return processing;
	}

	private static FileId writing;

	private static synchronized FileId getWriting() {
		return writing;
	}

	FileId _fileId;

	public LogProcess(FileId fileId) {
		_fileId = fileId;
	}

	@Override
	public void run() {
		try {
			// Reading
			System.out.println(_fileId.getPath().toString() + " is reading.");

			while (getReading() != null
					|| (getProcessing() != null && (_fileId.getCount() - 1) != getProcessing()
							.getCount())) {
			}

			reading = _fileId;
			List<String> readLines = Files.readAllLines(
					Paths.get(_fileId.getPath().toString()),
					StandardCharsets.UTF_8);
			while (getProcessing() != null
					|| (getWriting() != null && (_fileId.getCount() - 1) != getWriting()
							.getCount())) {
			}

			reading = null;

			// Processing
			// System.out.println(path.toString() + " has " +
			// lines.size()
			// + " lines.");
			System.out.println(_fileId.getPath().toString() + " is processing");

			processing = _fileId;
			List<String> processedLines = getProcessedLines(readLines);

			while (getWriting() != null) {
			}

			processing = null;

			// Writing
			System.out.println(_fileId.getPath().toString() + " is writing.");

			writing = _fileId;
			writeLinesToFile(_fileId.getPath(), processedLines);
			writing = null;

		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	private static List<String> getProcessedLines(List<String> lines) {
		for (int i = 0; i < lines.size(); i++) {
			StringBuilder sb = new StringBuilder();
			lines.set(i, sb.append(++count).append(".").append(lines.get(i))
					.toString());
		}

		return lines;
	}

	private static void writeLinesToFile(Path path, List<String> lines) {
		try {
			Files.write(path, lines, StandardOpenOption.CREATE);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
}
