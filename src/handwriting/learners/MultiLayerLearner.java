package handwriting.learners;

import handwriting.core.Drawing;
import handwriting.core.RecognizerAI;
import handwriting.core.SampleData;
import search.core.Duple;

import java.util.ArrayList;
import java.util.Set;
import java.util.concurrent.ArrayBlockingQueue;

public class MultiLayerLearner implements RecognizerAI {
    MultiLayer perceptron;
    private int num_inputs = 1600,
            num_hidden = 60,
            num_outputs = 1,
            training_iter = 200;
    private double rate = 0.1;
    public ArrayList<String> labels;

    public MultiLayerLearner(){
        perceptron = new MultiLayer(num_inputs, num_hidden, num_outputs);
    }

    @Override
    public void train(SampleData data, ArrayBlockingQueue<Double> progress) throws InterruptedException {
        labels = setToArrayList(data.allLabels());
        double prog = 0;
        for (int i = 0; i < data.numDrawings(); ++i){
            Duple<String, Drawing> dat = data.getLabelAndDrawing(i);
            Drawing drawing = dat.getSecond();
            double labelIndex = labels.indexOf(dat.getFirst()) == 1 ? 100 : -100;
            double[] inputs = getInputs(drawing);
            double[][] in_iter = new double[training_iter][inputs.length];
            double[][] outs = new double[training_iter][1];
            for (int k = 0; k < training_iter; ++k){
                in_iter[k] = inputs;
                outs[k] = new double[]{labelIndex};
            }
            perceptron.trainN(in_iter, outs, training_iter, rate);
            prog += 1.0/data.numDrawings();
            progress.add(prog);
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
        return labels.get(results[0] >= 1 ? 1 : 0);
    }

    public double[] getInputs(Drawing drawing){
        double[] inputs = new double[drawing.getWidth() * drawing.getHeight()];
        for (int j = 0; j < drawing.getHeight(); ++j){
            for (int k = 0; k < drawing.getWidth(); ++k){
                int index = j*(drawing.getWidth() -1) + k;
                inputs[index] = drawing.isSet(j, k) ? 1.0 : 0.0;
            }
        }
        return inputs;
    }
}
