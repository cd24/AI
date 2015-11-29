package evolution.MLP;

import evolution.interfaces.Output;

public class MLPOut implements Output<MLPOut> {
    private int[] bits;

    public MLPOut(int... bits){
        this.bits = bits;
    }
    public MLPOut(int bitCount){
        this.bits = new int[bitCount];
    }

    @Override
    public double distanceTo(Output<MLPOut> other) {
        //construct integer representation and do a difference
        //assumed integer values - more code required to extend
        MLPOut otherVal = other.get();
        return this.getIntValue() - otherVal.getIntValue();
    }

    @Override
    public double[] distances(Output<MLPOut> other, int numDivisions) {
        if (!validate(other)){
            System.out.println("Mismatched sizes!");
            return new double[0];
        }
        double[] err = new double[bits.length];
        int[] target = other.get().bits;
        for (int i = 0; i < bits.length; ++i){
            err[i] = target[i] - this.bits[i];
        }
        return err;
    }

    @Override
    public MLPOut get(){
        return this;
    }

    public int getIntValue(){
        int value = 0;
        for (int i = 0; i < bits.length; ++i){
            int val = bits[i];
            value = value | (val << i);
        }
        return value;
    }

    public boolean validate(Output<MLPOut> other){
        return other.get().bits.length == this.bits.length;
    }

}
