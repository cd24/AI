package text.test;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.function.BiConsumer;

import search.core.Duple;
import text.core.LabeledWords;
import text.core.TestResult;
import text.core.TextLearner;

public class SimpleTester {
	public static void test(String[] args, DataSetReader dataReader, BiConsumer<LabeledWords,LabeledWords> testDriver) throws FileNotFoundException {
		if (args.length == 0) {
			System.out.println("Usage: SimpleTester [filenames]");
			System.exit(1);
		}
		
		for (int i = 0; i < args.length; i++) {
			System.out.println(args[i]);
			File input = new File(args[i]);
			Duple<LabeledWords, LabeledWords> data = dataReader.read(input).split();
			LabeledWords train = data.getFirst();
			LabeledWords test = data.getSecond();

			System.out.println("Training size: " + train.size());
			System.out.println("Test size: " + test.size());

			testDriver.accept(train, test);
		}
	}
	
	public static void conductTest(String title, TextLearner learner, LabeledWords train, LabeledWords test) {
		learner.train(train);
		TestResult result = new TestResult(learner, test);
		show(title, result.getCorrect(), result.getTotal());
		for (String lbl: result.allLabels()) {
			show(lbl.toString(), result.getCorrectFor(lbl), result.getTotalFor(lbl));
		}
	}

	public static void show(String label, double correct, double total) {
		double ratio = 100.0 * correct / total;
		System.out.printf("%s: %d/%d (%4.2f%%)\n", label, (int)correct, (int)total, ratio);
	}
}
