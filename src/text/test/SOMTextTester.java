package text.test;

import text.learners.NaiveBayes;
import text.learners.SOMText;

import java.io.FileNotFoundException;

/**
 * Created by john on 11/19/15.
 */
public class SOMTextTester {
    public static void test(String[] args, DataSetReader dataReader) throws FileNotFoundException {
        SimpleTester.test(args, dataReader, (train, test) -> SimpleTester.conductTest("SOM Text", new SOMText(), train, test));
    }
}
