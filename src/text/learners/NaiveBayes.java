package text.learners;

import search.core.Histogram;
import text.core.Sentence;
import text.core.TextLearner;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.Map;

public class NaiveBayes implements TextLearner {

	HashMap<String, Histogram<String>> counts = new HashMap<>();
	Histogram<String> labelCounts = new Histogram<>();
	Histogram<String> wordCounts = new Histogram<>();

	@Override
	public void train(Sentence words, String lbl) {
		// Count up the relevant values

		Histogram<String> countForLabel = counts.getOrDefault(lbl, new Histogram<>());
        Histogram<String> sentenceCounts = words.wordCounts();
        countForLabel.add(sentenceCounts);
        counts.put(lbl, countForLabel);

		labelCounts.bump(lbl);
		wordCounts.add(sentenceCounts);
	}

	@Override
	public String classify(Sentence words) {
		// Use the counted values for classification.
		double bestOdds = Double.MIN_VALUE;
		String bestLabel = "UNKOWN";
		for (String word : words){
			for (String label: labelCounts){
				double evidenceInCategory = (counts.get(label).probabilityOf(word) + 1)/(counts.get(label).getTotalCounts() + wordCounts.getTotalCounts());
				double category = labelCounts.probabilityOf(label);
				double evidence = wordCounts.probabilityOf(word);
				if (evidence > 0) {
					double odds = (evidenceInCategory * category) / evidence;
					if (odds > bestOdds){
						bestOdds = odds;
						bestLabel = label;
					}
				}
			}
		}
		return bestLabel;
	}
}
