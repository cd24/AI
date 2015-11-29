package evolution.MLP;

import evolution.interfaces.Input;
import evolution.interfaces.Output;
import search.core.Duple;

public class SLPLearner  extends PerceptronES {
    public SLPLearner(int numInputs, int numOutputs) {
        super(numInputs, numOutputs);
    }

    public void train(Duple<Input, Output>[] data){

    }
}
