package handwriting.learners;

import handwriting.core.Drawing;
import handwriting.core.RecognizerAI;
import handwriting.core.SampleData;
import search.core.Duple;

import java.util.ArrayList;
import java.util.BitSet;
import java.util.Set;
import java.util.concurrent.ArrayBlockingQueue;

public class MultiLayerLearner implements RecognizerAI {
    MultiLayer perceptron;
    private int num_inputs = 1600, num_hidden = 60, num_outputs = 1;
    private double rate = 0.1;
    public ArrayList<String> labels;

    public MultiLayerLearner(){
        perceptron = new MultiLayer(num_inputs, num_hidden, num_outputs);
    }

    @Override
    public void train(SampleData data, ArrayBlockingQueue<Double> progress) throws InterruptedException {
        System.out.println("Training perceptron");
        labels = setToArrayList(data.allLabels());
        for (int i = 0; i < data.numDrawings(); ++i){
            Duple<String, Drawing> dat = data.getLabelAndDrawing(i);
            Drawing drawing = dat.getSecond();
            double labelIndex = labels.indexOf(dat.getFirst());
            if (!labels.contains(dat.getFirst()))
                labels.add(dat.getFirst());
            double[] inputs = getInputs(drawing);
            perceptron.train(inputs, new double[]{labelIndex}, rate);
            progress.add(100.0/data.numDrawings());
        }
    }

    public ArrayList<String> setToArrayList(Set<String> input){
        ArrayList<String> labels = new ArrayList<>();
        labels.addAll(input);
        return labels;
    }

    @Override
    public String classify(Drawing d) {
        double[] in = getInputs(d);
        double[] results = perceptron.compute(in);
        return labels.get(results[0] > 0.5 ? 1 : 0);
    }

    public double[] getInputs(Drawing drawing){
        BitSet bits = drawing.getBits();
        double[] inputs = new double[bits.size()];
        for (int j = 0; j < drawing.getHeight(); ++j){
            for (int k = 0; k < drawing.getWidth(); ++k){
                int index = j*(drawing.getWidth() -1) + k;
                inputs[index] = drawing.isSet(j, k) ? 1.0 : 0.0;
            }
        }
        return inputs;
    }
}
