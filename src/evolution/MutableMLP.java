package evolution;

import handwriting.core.Drawing;
import handwriting.core.SampleData;
import handwriting.learners.MultiLayer;
import handwriting.learners.MultiLayerBitwise;
import search.core.Duple;

import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.Random;


public class MutableMLP extends MultiLayerBitwise implements Comparable<MutableMLP> {
    Random random = new Random();
    double layerThreshold = 0.4;
    public double score = 0;

    public MutableMLP crossover(MutableMLP other){
        try {
            MutableMLP child = (MutableMLP) this.clone();
            int severity = random.nextInt(this.num_hidden);
            for (int i = 0; i < severity; ++i){
                int hiddenIndex = random.nextInt(this.perceptron.numHiddenNodes());
                child.pullin(hiddenIndex, other);
            }
            return child;
        } catch (CloneNotSupportedException e) {
            e.printStackTrace();
            return null;
        }
    }

    public void pullin(int hiddenIndex, MutableMLP other){
        double[][] otherWeights = other.perceptron.inputToHidden.weights;
        for (int i = 0; i < this.num_inputs; ++i){
            this.perceptron.inputToHidden.weights[i][hiddenIndex] = otherWeights[i][hiddenIndex];
        }
        for (int i = 0; i < this.num_outputs; ++i){
            this.perceptron.hiddenToOutput.weights[hiddenIndex][i] = otherWeights[hiddenIndex][i];
        }
    }

    public void train(SampleData data){
        labels = setToArrayList(data.allLabels());
        perceptron = new MultiLayer(1600, num_hidden(), num_out());
        training_iter = 1;
        for (int i = 0; i < training_iter; ++i){
            for (String label : labels){
                double[] outs = getOutputForLabel(label);
                int num_drawings = data.numDrawingsFor(label);
                for (int k = 0; k < num_drawings; ++k){
                    Drawing drawing = data.getDrawing(label, k);
                    double[] inputs = getInputs(drawing);
                    perceptron.train(inputs, outs, rate);
                }
            }
            perceptron.updateWeights();
        }
    }

    private double[] getOutputForLabel(String label){
        double[] outs = cleanArr(8);
        int index = labels.indexOf(label);
        for (int i = 0; i < 8; ++i){
            outs[i] = (index & (1 << i)) >> i;
        }
        return outs;
    }

    public void setLabels(ArrayList<String> labels){
        this.labels = labels;
    }

    public MutableMLP mutate(double chance){
        MutableMLP thing = this.mutate();
        int max_edge_mod = 10;
        for (int i = 0; i < max_edge_mod; ++i){
            if (Math.random() < chance)
                thing.mutateInPlace();
        }
        return thing;
    }

    public void mutateInPlace(){
        double layerRandom = Math.random();
        Duple<Integer, Integer> indexRandom = getMutateIndex(layerRandom);
        if (layerRandom < layerThreshold) {
            double[][] weightsTemp = perceptron.inputToHidden.weights;
            weightsTemp[indexRandom.getFirst()][indexRandom.getSecond()] += Math.random();
            perceptron.inputToHidden.weights = weightsTemp;
        }
        else {
            double[][] weightsTemp = perceptron.hiddenToOutput.weights;
            weightsTemp[indexRandom.getFirst()][indexRandom.getSecond()] += Math.random();
            perceptron.hiddenToOutput.weights = weightsTemp;
        }
    }

    public MutableMLP mutate(){

        try {
            MutableMLP clone = (MutableMLP) this.clone();
            clone.mutateInPlace();
            return clone;
        } catch (CloneNotSupportedException e) {
            e.printStackTrace();
            return this;
        }
    }

    public Duple<Integer, Integer> getMutateIndex(double layer){
        Integer index = 0, index2 = 0;
        if (layer < layerThreshold){
            index = random.nextInt(this.perceptron.numInputNodes());
            index2 = random.nextInt(this.perceptron.numHiddenNodes());
        }
        else {
            index = random.nextInt(this.perceptron.numHiddenNodes());
            index2 = random.nextInt(this.perceptron.numOutputNodes());
        }
        return new Duple(index, index2);
    }

    public String toString(){
        StringBuilder builder = new StringBuilder();
        builder.append("Layer 1\n");
        for (int i = 0; i < perceptron.inputToHidden.weights.length; ++i){
            for (int j = 0; j < perceptron.inputToHidden.weights[0].length; ++j){
                builder.append(perceptron.inputToHidden.weights[i][j] + ",");
            }
            builder.append("\n");
        }
        builder.append("Layer 2\n");
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
        if (this.score < o.score) return 1;
        if (this.score > o.score) return -1;
        return 0;
    }

    @Override
    protected Object clone() throws CloneNotSupportedException {
        MutableMLP clone = new MutableMLP();
        clone.perceptron = this.perceptron;
        return clone;
    }
}
