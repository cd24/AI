package handwriting.learners.decisiontree;

import handwriting.core.SampleData;
import search.core.Duple;
import handwriting.core.Drawing;

public class DTSampleData extends SampleData {
	public DTSampleData() {super();}
	
	public DTSampleData(SampleData src) {
		for (int i = 0; i < src.numDrawings(); i++) {
			this.addDrawing(src.getLabelFor(i), src.getDrawing(i));
		}
	}
	
	public double getGini() {
		double homogeneity = 0;
		for (String label : allLabels()){
			for (int i = 0; i < numDrawingsFor(label); ++i){
				homogeneity += Math.pow(numDrawingsFor(label)/numDrawings(), 2);
			}
		}
		return 1 - homogeneity;
	}
	
	public Duple<DTSampleData,DTSampleData> splitOn(int x, int y) {
		DTSampleData on = new DTSampleData();
		DTSampleData off = new DTSampleData();
		for (String label: allLabels()){
			for (int i = 0; i < numDrawingsFor(label); ++i){
				Drawing current = getDrawing(label, i);
				if (current.isSet(x, y))
					on.addDrawing(label, current);
				else
					off.addDrawing(label, current);
			}
		}
		return new Duple<>(on, off);
	}
}
