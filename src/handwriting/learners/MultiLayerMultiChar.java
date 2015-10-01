package handwriting.learners;

import handwriting.core.Drawing;
import handwriting.core.SampleData;

import java.util.concurrent.ArrayBlockingQueue;


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
        perceptron = new MultiLayer(1600, num_hidden(), num_out());
        training_iter = 700;
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
        double[] outs = cleanArr(8);
        int index = labels.indexOf(label);
        for (int i = 0; i < 8; ++i){
            outs[i] = (index & (1 << i)) >> i;
        }
        return outs;
    }

    public String getLabelForOuts(double[] outs){
        int index = (int) Math.round(outs[0]);
        for (int i = 1; i < outs.length; ++i){
            index = index | (int) Math.round(outs[i]) << i;
        }
        if (index >= labels.size()){
            index = labels.size() - 1;
        }
        else if (index < 0 ){
            index = 0;
        }
        return labels.get(index);
    }
}
