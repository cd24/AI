package handwriting.learners;

import handwriting.core.Drawing;
import handwriting.core.RecognizerAI;
import handwriting.core.SampleData;

import java.util.ArrayList;
import java.util.Set;
import java.util.concurrent.ArrayBlockingQueue;

public class MultiLayerLearner implements RecognizerAI {
    MultiLayer perceptron;
    protected int num_inputs = 1600,
            num_hidden = num_hidden(),
            num_outputs = num_out(),
            training_iter = 500;
    protected double rate = training_rate();
    public ArrayList<String> labels;

    public MultiLayerLearner(){
        perceptron = new MultiLayer(num_inputs, num_hidden, num_outputs);
    }

    @Override
    public void train(SampleData data, ArrayBlockingQueue<Double> progress) throws InterruptedException {
        labels = setToArrayList(data.allLabels());
        double prog = 0;
        int goal = 0;
        for (int i = 0; i < training_iter; ++i){
            for (String label : labels){
                int num_drawings = data.numDrawingsFor(label);
                for (int k = 0; k < num_drawings; ++k){
                    Drawing drawing = data.getDrawing(label, k);
                    double[] inputs = getInputs(drawing);
                    int expected_out = labels.indexOf(label);
                    double[] outPut = {expected_out};
                    perceptron.train(inputs, outPut, rate);
                }
            }
            perceptron.updateWeights();
            prog += 1.0/training_iter;
            progress.add(prog);
        }
    }

    public void old_train(SampleData data, ArrayBlockingQueue<Double> progress){
        double prog = 0;
        int goal = 0;
        for (String label : data.allLabels()){
            ArrayList<Drawing> drawings = new ArrayList<>();
            for (int i = 0; i < data.numDrawingsFor(label); ++i){
                drawings.add(data.getDrawing(label, i));
            }
            ArrayList<Double[]> reps = new ArrayList<>();
            for (Drawing d : drawings){
                //reps.add(getInputs(d));
            }

            double[][] in_iter = new double[training_iter][num_inputs];
            double[][] outs = new double[training_iter][1];
            for (int k = 0; k < training_iter; ++k){
                in_iter[k] = doubleToDouble(
                        reps.get(k%reps.size()));
                outs[k] = new double[]{goal};
            }
            perceptron.trainN(in_iter, outs, training_iter, rate);
            prog += 1.0/data.numLabels();
            progress.add(prog);
            ++goal;
        }
    }

    public ArrayList<String> setToArrayList(Set<String> input){
        ArrayList<String> labels = new ArrayList<>();
        labels.addAll(input);
        return labels;
    }

    double[] doubleToDouble(Double[] obj){
        System.out.println("obj" + obj);
        double[] arr = new double[obj.length];
        for (int i = 0; i < obj.length; ++i){
            arr[i] = obj[i];
        }
        return arr;
    }

    @Override
    public String classify(Drawing d) {
        double[] in = getInputs(d);
        double[] results = perceptron.compute(in);
        return labels.get(results[0] >= 0.5 ? 1 : 0);
    }

    public double[] getInputs(Drawing drawing){
        double[] inputs = new double[drawing.getWidth() * drawing.getHeight()];
        for (int j = 0; j < drawing.getHeight(); ++j){
            for (int k = 0; k < drawing.getWidth(); ++k){
                int index = j*(drawing.getWidth()) + k;
                inputs[index] = drawing.isSet(j, k) ? 1.0 : 0.0;
            }
        }
        return inputs;
    }

    public double[] cleanArr(int length){
        //not sure if this problem exists in java, but it could.
        double[] arr = new double[length];
        for (int i =0; i < length; ++i){
            arr[i] = 0.0;
        }
        return arr;
    }

    public int num_hidden(){
        return 30;
    }

    public int num_out(){
        return 1;
    }

    public double training_rate(){
        return 0.1;
    }
}
