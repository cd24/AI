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
			DTLeaf leaf = new DTLeaf(data.getLabelFor(0));
			currentProgress += tick;
			//progress.add(currentProgress);
			return leaf;
		} else {
			int featureX = 0, featureY = 0;
			double score = -10;
			double parentScore = data.getGini();
			for (int i = 0; i < data.getDrawingHeight(); ++i){
				for (int j = 0; j < data.getDrawingWidth(); ++j){
					Duple<DTSampleData, DTSampleData> split = data.splitOn(j, i);
					double newScore = parentScore - (split.getFirst().getGini() + split.getSecond().getGini());
					if (newScore > score) {
						score = newScore;
						featureX = j;
						featureY = i;
					}
				}
			}
			Duple<DTSampleData, DTSampleData> split = data.splitOn(featureX, featureY);
			DTInteriorNode node = new DTInteriorNode(featureX,
													featureY,
													train(split.getSecond()),
													train(split.getFirst()));
			currentProgress += tick;
			//progress.add(currentProgress);
			return node;
		}
	}
}
