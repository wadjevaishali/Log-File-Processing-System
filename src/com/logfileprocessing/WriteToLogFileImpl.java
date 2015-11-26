package com.logfileprocessing;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.StandardOpenOption;
import java.util.ArrayList;
import java.util.List;

public class WriteToLogFileImpl {

	public static void main(String[] args) {
		File folder = new File("E:\\logfiles");
		File[] files = folder.listFiles();
		List<String> logs = new ArrayList<String>();
		logs.add("aaaaa" + "\r\n");
		logs.add("bbbbb" + "\r\n");
		logs.add("ccccc" + "\r\n");
		logs.add("ddddd" + "\r\n");
		logs.add("eeeee" + "\r\n");
		logs.add("fffff" + "\r\n");
		logs.add("ggggg" + "\r\n");
		logs.add("hhhhh" + "\r\n");
		logs.add("iiiii" + "\r\n");
		logs.add("jjjjj");

		for (int i = 0; i < files.length; i++) {
			try {
				//Files.write(files[i], logs, StandardOpenOption.CREATE);
				BufferedWriter writer = new BufferedWriter(new FileWriter(
						files[i]));
				for (String log : logs)
					writer.write(log);
				writer.close();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
		System.out.println("Finished with writing");
	}

}
