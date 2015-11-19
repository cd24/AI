package text.test;

import java.io.FileNotFoundException;

import text.core.LabeledWords;

public class BayesTest1 {
	public static void main(String[] args) throws FileNotFoundException {
		BayesTester.test(args, LabeledWords::fromSentiment);
	}
}
