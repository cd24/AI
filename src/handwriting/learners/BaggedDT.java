package handwriting.learners;

import handwriting.core.Drawing;
import handwriting.core.RecognizerAI;
import handwriting.core.SampleData;

import java.util.concurrent.ArrayBlockingQueue;

/**
 * Created by john on 10/23/15.
 */
public class BaggedDT implements RecognizerAI{
    Bagger bag;
    @Override
    public void train(SampleData data, ArrayBlockingQueue<Double> progress) throws InterruptedException {
        bag = new Bagger(DecisionTree::new, 30);
        bag.train(data, progress);
    }

    @Override
    public String classify(Drawing d) {
        return bag.classify(d);
    }
}
