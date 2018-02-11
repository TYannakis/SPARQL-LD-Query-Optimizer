package ics.forth.utils;

import java.io.BufferedWriter;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;

public class WriteFile {
	private final String file;
	public WriteFile(String file) {
		this.file=file;
	}
	public void write(List<String> lines) {
		Path path = Paths.get(file);
		try (BufferedWriter writer = Files.newBufferedWriter(path)) {
			lines.forEach(line ->  write(writer, line));
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	private void write(BufferedWriter writer, String line) {
		try {
			writer.write(line);
			writer.newLine();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
}
