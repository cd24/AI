package evolution;

import handwriting.learners.MultiLayerBitwise;

import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.Random;


public class MutableMLP extends MultiLayerBitwise {
    Random random = new Random();
    double layerThreshold = 0.4;

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
}