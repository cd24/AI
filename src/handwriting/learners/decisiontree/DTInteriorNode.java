package handwriting.learners.decisiontree;

import handwriting.core.Drawing;

public class DTInteriorNode implements DTNode {
    int featureX = 0, featureY = 0;
    DTNode left, right;

    public DTInteriorNode(int x, int y){
        this(x, y, null, null);
    }

    public DTInteriorNode(int x, int y, DTNode left, DTNode right){
        this.featureX = x;
        this.featureY = y;
        this.left = left;
        this.right = right;
    }

    public void setLeft(DTNode node){
        this.left = node;
    }
    public void setRight(DTNode node){
        this.right = node;
    }

    @Override
    public String classify(Drawing d) {
        if (hasFeature(d))
            return this.right.classify(d);
        else
            return this.left.classify(d);
    }

    public boolean hasFeature(Drawing d){
        return d.isSet(this.featureX, this.featureY);
    }
}
