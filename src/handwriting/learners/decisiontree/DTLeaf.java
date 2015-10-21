package handwriting.learners.decisiontree;

import handwriting.core.Drawing;

public class DTLeaf implements DTNode {
	private String label;
	
	public DTLeaf(String label) {this.label = label;}

	@Override
	public String classify(Drawing d) {
		return label;
	}

}
