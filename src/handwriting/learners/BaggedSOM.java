package handwriting.learners;

import handwriting.core.Drawing;
import handwriting.core.RecognizerAI;
import handwriting.core.SampleData;

import java.util.concurrent.ArrayBlockingQueue;
import java.util.function.Supplier;

public class BaggedSOM implements RecognizerAI {
    Bagger bagger = new Bagger(SOM::new, 15);
    @Override
    public void train(SampleData data, ArrayBlockingQueue<Double> progress) throws InterruptedException {
        System.out.println("Training SOM Bag");
        bagger.train(data, progress);
        System.out.println("Done training SOM Bag");
    }

    @Override
    public String classify(Drawing d) {
        return bagger.classify(d);
    }
}