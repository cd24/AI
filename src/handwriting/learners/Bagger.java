package handwriting.learners;

import java.util.ArrayList;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.function.Supplier;

import handwriting.core.Drawing;
import handwriting.core.RecognizerAI;
import handwriting.core.SampleData;
import search.core.Histogram;

public class Bagger implements RecognizerAI {
	private ArrayList<RecognizerAI> bags;
	private Supplier<RecognizerAI> supplier;
	private int numBags;
	
	// For the "supplier" parameter, use the constructor; for example, 
	// b = new Bagger(DecisionTree::new, 30)
	public Bagger(Supplier<RecognizerAI> supplier, int numBags) {
		this.numBags = numBags;
		this.supplier = supplier;
		this.bags = new ArrayList<>();
	}

	@Override
	public void train(SampleData data, ArrayBlockingQueue<Double> progress) throws InterruptedException {
		// TODO: Reset "bags" to be empty.  Then create "numBags" instances 
		// of whatever learner is being bagged.  For each of these instances,
		// recreate the training data by random sampling with replacement. 
		// Then train that instance using the rebuilt data.
	}

	@Override
	public String classify(Drawing d) {
		// TODO: Use a Histogram (from search.core) to count the labels
		// returned by calling "classify(d)" on each learner.  Then
		// return the plurality winner.
		return "Unknown";
	}

}
