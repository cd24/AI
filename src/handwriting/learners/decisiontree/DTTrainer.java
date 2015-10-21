package handwriting.learners.decisiontree;

import java.util.concurrent.ArrayBlockingQueue;

import handwriting.core.SampleData;
import search.core.Duple;
import search.core.Triple;

public class DTTrainer {
	private ArrayBlockingQueue<Double> progress;
	private DTSampleData baseData;
	private double currentProgress, tick;
	
	public DTTrainer(SampleData data, ArrayBlockingQueue<Double> progress) throws InterruptedException {
		baseData = new DTSampleData(data);
		this.progress = progress;
		this.currentProgress = 0;
		progress.put(currentProgress);
		this.tick = 1.0 / data.numDrawings();
	}
	
	public DTNode train() throws InterruptedException {
		return train(baseData);
	}
	
	private DTNode train(DTSampleData data) throws InterruptedException {
		if (data.numLabels() == 1) {
			// TODO: Create a leaf node
			// Update the progress bar
			return null;
		} else {
			// TODO: Create an interior node
			// Use recursion to create the children of that node
			return null;
		}
	}
}
