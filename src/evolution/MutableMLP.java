package evolution;

import handwriting.learners.MultiLayerBitwise;

import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.Random;


public class MutableMLP extends MultiLayerBitwise implements Comparable<MutableMLP> {
    Random random = new Random();
    double layerThreshold = 0.4;
    public double score = 0;


    public MutableMLP crossover(MutableMLP other){
        //simple crossover
        MutableMLP child = new MutableMLP();
        child.perceptron.inputToHidden = other.perceptron.inputToHidden;
        child.perceptron.hiddenToOutput = this.perceptron.hiddenToOutput;
        return child;
    }

    public void setLabels(ArrayList<String> labels){
        this.labels = labels;
    }

    public MutableMLP mutate(){
        double layerRandom = Math.random();
        int indexRandom = getMutateIndex(layerRandom);
        try {
            MutableMLP clone = (MutableMLP) this.clone();
            if (layerRandom < layerThreshold) {
                double[][] weightsTemp = clone.perceptron.inputToHidden.weights;
                for (int i = 0; i < weightsTemp[0].length; ++i) {
                    weightsTemp[indexRandom][i] += Math.random();
                }
            }
            return clone;
        } catch (CloneNotSupportedException e) {
            e.printStackTrace();
            return this;
        }
    }

    public int getMutateIndex(double layer){
        int index = 0;
        if (layer < layerThreshold){
            index = random.nextInt(this.perceptron.numInputNodes());
        }
        else {
            index = random.nextInt(this.perceptron.numHiddenNodes());
        }
        return index;
    }

    public String toString(){
        StringBuilder builder = new StringBuilder();
        builder.append("Layer 1");
        for (int i = 0; i < perceptron.inputToHidden.weights.length; ++i){
            for (int j = 0; j < perceptron.inputToHidden.weights[0].length; ++j){
                builder.append(perceptron.inputToHidden.weights[i][j] + ",");
            }
            builder.append("\n");
        }
        builder.append("Layer 2");
        for (int i = 0; i < perceptron.hiddenToOutput.weights.length; ++i){
            for (int j = 0; j < perceptron.hiddenToOutput.weights[0].length; ++j){
                builder.append(perceptron.hiddenToOutput.weights[i][j] + ",");
            }
            builder.append("\n");
        }
        return builder.toString();
    }

    @Override
    public int compareTo(MutableMLP o) {
        return (int) (this.score - o.score);
    }

    @Override
    protected Object clone() throws CloneNotSupportedException {
        MutableMLP newGuy = crossover(this);
        return newGuy;
    }
}
