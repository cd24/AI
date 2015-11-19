package text.core;

import search.core.Histogram;

public class TestResult {
	private Histogram<String> correctForLabel;
	private Histogram<String> numberForLabel;
	
	public TestResult(TextLearner learner, LabeledWords testSet) {
		correctForLabel = learner.test(testSet);
		numberForLabel = testSet.allCounts();
		
		System.out.println("TestResult: TotalCounts: " + numberForLabel.getTotalCounts() + " testSet: " + testSet.size());
	}
	
	public Iterable<String> allLabels() {return numberForLabel;}
	
	public double getCorrectFor(String lbl) {
		return correctForLabel.getCountFor(lbl);
	}
	
	public double getTotalFor(String lbl) {
		return numberForLabel.getCountFor(lbl);
	}
	
	public double getRatioFor(String lbl) {
		return (double)getCorrectFor(lbl) / getTotalFor(lbl);
	}
	
	public Double getCorrect() {
		return correctForLabel.getTotalCounts();
	}
	
	public Double getTotal() {
		return numberForLabel.getTotalCounts();
	}
	
	public double getRatio() {
		return (double)getCorrect() / getTotal();
	}
}
