package text.test;

import text.core.LabeledWords;

import java.io.FileNotFoundException;


public class FarmTests {
    public static void main(String[] args) throws FileNotFoundException {
        BayesTester.test(args, LabeledWords::farmData);
        //SOMTextTester.test(args, LabeledWords::farmData);
    }
}
