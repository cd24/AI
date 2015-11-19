package search.core;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.Scanner;

public class Funcs {
	public static String fromFile(File f) throws FileNotFoundException {
		Scanner s = new Scanner(f);
		String result = s.useDelimiter("\\A").next();
		s.close();
		return result;
	}
	
	public static String purgeHeadersFrom(String text) {
		StringBuilder result = new StringBuilder();
		for (String line: text.split("\n")) {
			if (!isHeaderLine(line)) {
				result.append(line);
				result.append('\n');
			}
		}
		return result.toString();
	}
	
	public static boolean isHeaderLine(String line) {
		String[] words = line.split("\\s+");
		return words.length > 0 && words[0].length() > 0 && words[0].charAt(words[0].length() - 1) == ':';
	}
}
