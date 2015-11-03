package handwriting.learners;

import handwriting.core.RecognizerAI;

import java.util.function.Supplier;

/**
 * Created by john on 10/27/15.
 */
public class BaggedTest extends Bagger {

    public BaggedTest(Supplier<RecognizerAI> supplier, int numBags) {
        super(SOM::new, 100);
    }
}
