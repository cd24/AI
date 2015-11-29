package evolution.MLP;

import evolution.interfaces.Input;

public class MLPIn implements Input<double[]> {
    private double[] drawing;

    public double[] get(){
        return drawing;
    }
}
