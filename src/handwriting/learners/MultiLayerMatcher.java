package handwriting.learners;

import handwriting.core.Drawing;
import handwriting.core.SampleData;

import java.util.concurrent.ArrayBlockingQueue;

public class MultiLayerMatcher extends MultiLayerLearner {
    MultiLayer perceptron = new MultiLayer(1600, 30, 1);
    @Override
    public void train(SampleData data, ArrayBlockingQueue<Double> progress) throws InterruptedException {
        int num_labels = data.numLabels();
        labels = setToArrayList(data.allLabels());
        perceptron = new MultiLayer(1600, 50, num_labels);
        rate = 0.2;
        training_iter = 500;
        double prog = 0;
        for (int i = 0; i < training_iter; ++i){
            for (String label : labels){
                double[] outs = cleanArr(num_labels);
                outs[labels.indexOf(label)] = 1;
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
        double[] data = getInputs(d);
        double[] outs = perceptron.compute(data);
        int index = 0;
        for (int i = 0; i < outs.length; ++i){
            if (Math.round(outs[i]) == 1){
                index = i;
            }
        }
        return labels.get(index);
    }
}
