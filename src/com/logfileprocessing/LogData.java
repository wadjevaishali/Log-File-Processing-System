package com.logfileprocessing;

import java.nio.file.Path;
import java.util.List;

public class LogData {
	Path path;
	List<String> lines;

	public LogData(Path path, List<String> lines) {
		this.path = path;
		this.lines = lines;
	}
}
