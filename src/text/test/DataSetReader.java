package text.test;

import java.io.File;
import java.io.FileNotFoundException;

import text.core.LabeledWords;

public interface DataSetReader {
	public LabeledWords read(File input) throws FileNotFoundException;
}
