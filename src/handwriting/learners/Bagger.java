package handwriting.learners;

import java.util.ArrayList;
import java.util.DoubleSummaryStatistics;
import java.util.Random;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.function.Supplier;

import handwriting.core.Drawing;
import handwriting.core.RecognizerAI;
import handwriting.core.SampleData;
import planner.core.Domain;
import search.core.Histogram;

public class Bagger implements RecognizerAI {
	private ArrayList<RecognizerAI> bags;
	private Supplier<RecognizerAI> supplier;
	private int numBags;
	public int numRounds = 1;
	
	// For the "supplier" parameter, use the constructor; for example, 
	// b = new Bagger(DecisionTree::new, 30)
	public Bagger(Supplier<RecognizerAI> supplier, int numBags) {
		this.numBags = numBags;
		this.supplier = supplier;
		this.bags = new ArrayList<>();
	}

	@Override
	public void train(SampleData data, ArrayBlockingQueue<Double> progress) throws InterruptedException {
		bags.clear();
		for (int i = 0; i < numBags; ++i){
			System.out.print("Training bag: " + (i + 1) + " of " + numBags + "\r");
			RecognizerAI instance = supplier.get();
			trainInstance(instance, sampled(data), progress);
			bags.add(instance);
			System.out.println("Trained bag: " + (i + 1) + " of " + numBags);
		}
	}

	private void trainInstance(RecognizerAI input, SampleData data, ArrayBlockingQueue<Double> queue) throws InterruptedException {
		for (int i = 0; i < numRounds; ++i){
			input.train(data, queue);
		}
	}

	private SampleData sampled(SampleData data){
		SampleData sampleData = new SampleData();
		Random generator = new Random();
		int bound = data.numDrawings();
		for (int i = 0; i < bound; ++i){
			int randomIndex = generator.nextInt(bound);
			String label = data.getLabelFor(randomIndex);
			Drawing drawing = data.getDrawing(randomIndex);
			sampleData.addDrawing(label, drawing);
		}
		return sampleData;
	}

	@Override
	public String classify(Drawing d) {
		Histogram<String> counter = new Histogram<>();
		for (RecognizerAI ai : bags){
			String winner = ai.classify(d);
			counter.bump(winner);
		}
		return counter.getPluralityWinner();
	}

}
