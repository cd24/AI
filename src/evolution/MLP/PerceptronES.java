package evolution.MLP;

import evolution.interfaces.Evolvable;
import evolution.interfaces.Input;
import evolution.interfaces.Output;
import search.core.Duple;

public class PerceptronES implements Evolvable {
    private double[][] weights, deltas; //single layer
    private double[] outputs, errors;
    private int trainingRounds = 500, numInputs, numOutputs;
    private double learningRate = 0.02;

    public PerceptronES(int numInputs, int numOutputs){
        weights = new double[numInputs][numOutputs];
        this.numInputs = numInputs;
        this.numOutputs = numOutputs;

        for (int i = 0; i < numInputs + 1; ++i){
            for (int j = 0; j < numOutputs; ++j){
                weights[i][j] = Math.random();
                deltas[i][j] = 0.0;
            }
        }

        for (int i = 0; i < numOutputs; ++i){
            outputs[i] = 0.0;
            errors[i] = 0.0;
            weights[numInputs][i] = 0.0;
        }
    }

    public PerceptronES(PerceptronES other){
        numInputs = other.numInputs;
        numOutputs = other.numOutputs;

        for (int i = 0; i < numInputs + 1; ++i){
            for (int j = 0; j < numOutputs; ++j){
                weights[i][j] = Math.random();
                deltas[i][j] = 0.0;
            }
        }

        for (int j = 0; j < numInputs; ++j) {
            for (int i = 0; i < numOutputs; ++i) {
                weights[j][i] = other.weights[j][i];
                deltas[j][i] = 0.0;
            }
        }

        for (int i = 0; i < numOutputs; ++i) {
            outputs[i] = 0.0;
            errors[i] = 0.0;
            weights[numInputs][i] = 0.0;
        }
    }

    private void initMemebrs(){
        weights = new double[numInputs][numOutputs];
        deltas = new double[numInputs][numOutputs];
        outputs = new double[numOutputs];
        errors = new double[numOutputs];
    }

    private MLPOut compute(double[] input){
        //todo: Check Compute
        for (int out = 0; out < outputs.length; ++out){
            for (int in = 0; in < input.length; ++in){
                double weight = weights[in][out];
                outputs[out] = weight * input[in];
            }
            double sum = outputs[out];
            sum -= weights[this.numInputs][out];
            outputs[out] = sigmoid(sum);
        }

        int[] data = new int[outputs.length];
        for (int i = 0; i < outputs.length; ++i){
            data[i] = (int) outputs[i];
        }

        return new MLPOut(data);
    }

    public void addToWeightDeltas(double[] inputs, double rate) {
        compute(inputs);
        for (int out = 0; out < outputs.length; ++out){
            double output = outputs[out];
            double error = errors[out];
            double gradient = gradient(output);
            for (int in = 0; in < inputs.length; ++in){
                double input = inputs[in];
                deltas[in][out] += input * error * rate * gradient;
            }
            deltas[this.numInputs][out] -= error * rate * gradient;
        }
    }

    public static double sigmoid(double x) {
        return 1.0 / (1.0 + Math.exp(-x));
    }

    @Override
    public Output classify(Input in) {
        double[] values = (double[])in.get();
        return compute(values);
    }

    @Override
    public Evolvable crossover() {
        return this;
    }

    @Override
    public Evolvable mutate() {
        return this;
    }

    @Override
    public void train(Duple<Input, Output>[] data) {
        //todo: add support for progress reporting
        for (int i = 0; i < trainingRounds; ++i){
            for (int j = 0; j < data.length; ++j){
                //forced cast is OK becase of internal classify method
                MLPOut output = (MLPOut) classify(data[j].getFirst());
                MLPIn input = (MLPIn) data[j].getFirst();
                errors = output.distances(data[j].getSecond(), 8);
                addToWeightDeltas(input.get(), learningRate);
            }
        }
    }

    public void updateWeights() {
        for (int j = 0; j < numInputs; ++j) {
            for (int i = 0; i < numOutputs; ++i) {
                weights[j][i] += deltas[j][i];
                deltas[j][i] = 0.0;
            }
        }
    }

    public static double gradient(double fOfX) {
        return fOfX * (1.0 - fOfX);
    }
}
