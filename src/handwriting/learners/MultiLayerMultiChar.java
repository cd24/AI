package handwriting.learners;

import handwriting.core.Drawing;
import handwriting.core.SampleData;

import java.util.concurrent.ArrayBlockingQueue;

/**
 * Created by john on 9/30/15.
 */
public class MultiLayerMultiChar extends MultiLayerLearner {
    @Override
    public int num_hidden(){
        return 40;
    }

    @Override
    public int num_out() {
        return 8;
    }

    @Override
    public double training_rate() {
        return 0.3;
    }

    @Override
    public void train(SampleData data, ArrayBlockingQueue<Double> progress) throws InterruptedException {
        labels = setToArrayList(data.allLabels());
        double prog = 0;
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
            prog += 1.0/training_iter;
            progress.add(prog);
        }
    }

    @Override
    public String classify(Drawing d) {
        double[] inputs = getInputs(d);
        double[] outs = perceptron.compute(inputs);
        return getLabelForOuts(outs);
    }

    private double[] getOutputForLabel(String label){
        int num_labels = labels.size();
        double[] outs = new double[8];
        int index = labels.indexOf(label);
        if (index > ((1/2.0)*num_labels)){
            for (int i = 0; i < 4; ++i){
                outs[i] = 0.0;
            }
            if (index > ((3/4.0)*num_labels)){
                outs[4] = 0.0;
                outs[5] = 0.0;
                outs[6] = 1;
                outs[7] = 1;
            } else{
                outs[4] = 1;
                outs[5] = 1;
                outs[6] = 0.0;
                outs[7] = 0.0;
            }
        }
        else {
            for (int i = 4; i < 8; ++i){
                outs[i] = 0.0;
            }
            if (index < ((1/4.0)*num_labels)){
                outs[0] = 1;
                outs[1] = 1;
                outs[2] = 0.0;
                outs[3] = 0.0;
            }
            else {
                outs[0] = 0.0;
                outs[1] = 0.0;
                outs[2] = 1;
                outs[3] = 1;
            }
        }
        return outs;
    }

    public String getLabelForOuts(double[] outs){
        double total = 0.0;
        int first = 0, second = 0;
        for (int i = 0; i < outs.length; ++i){
            total += outs[i];
            if (Math.round(outs[i]) > 1){ //not sure where to go with this...
                if (first == 0) {
                    first = i;
                }
                else if (second == 0){
                    second = i;
                }
            }
        }
        int index = (int) Math.round(total);
        return labels.get(index);
    }
}
