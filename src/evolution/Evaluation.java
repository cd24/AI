package evolution;

import evolution.interfaces.Evolvable;
import evolution.interfaces.Input;
import evolution.interfaces.Output;
import search.core.Duple;

public class Evaluation implements Comparable<Evaluation>{
    private Evolvable child;
    private Duple<Input, Output>[] data;
    private double[] distance;

    public Evaluation(Evolvable toEvaluate, Duple<Input, Output>[] data, boolean evaluating){
        this.child = toEvaluate;
        this.data = data;
        this.distance = new double[data.length];
        if (evaluating)
            evaluate();
    }

    public Evaluation(Evolvable toEvaluate, Duple<Input, Output>... data){
        this(toEvaluate, data, true);
    }

    public void evaluate(){
        for (int i = 0; i < data.length; ++i){
            Duple<Input, Output> curr = data[i];
            Output response = child.classify(curr.getFirst());
            distance[i] = response.distanceTo(curr.getSecond());
        }
    }

    public int compareTo(Evaluation other){
        Double difference = 0.0;
        for (int i = 0; i < distance.length; ++i){
            difference += distance[i];
        }
        return difference.intValue();
    }

    public Evolvable getSubject(){
        return child;
    }
}
