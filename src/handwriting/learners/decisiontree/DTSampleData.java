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
		// TODO: Implement Gini coefficient for this set
		double homogeneityParent = this.homogeneity();
		return 0.0;
	}

	public double homogeneity(){
		int drawingWidth = getDrawing(0).getWidth();
		int drawingHeight = getDrawing(0).getHeight();
		double homogeneity = 0;
		for (int i = 0; i < drawingWidth; ++i){
			for (int j = 0; j < drawingHeight; ++j){
				for (String label : allLabels()){
					for (int k = 0; k < numDrawingsFor(label); ++k){
						homogeneity += getDrawing(label, k).isSet(i, j) ? 1 : 0;
					}
				}
			}
		}
		return homogeneity/numDrawings();
	}
	
	public Duple<DTSampleData,DTSampleData> splitOn(int x, int y) {
		DTSampleData on = new DTSampleData();
		DTSampleData off = new DTSampleData();
		// TODO: Add all elements with (x, y) set to "on"
		//       Add all elements with (x, y) not set to "off"
		return new Duple<>(on, off);
	}
}
