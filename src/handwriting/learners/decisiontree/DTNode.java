package handwriting.learners.decisiontree;

import handwriting.core.Drawing;

public interface DTNode {
	public String classify(Drawing d);
}
