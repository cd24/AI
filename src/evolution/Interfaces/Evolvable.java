package evolution.interfaces;

import search.core.Duple;

public interface Evolvable {
    Output classify(Input in);

    Evolvable crossover();
    Evolvable mutate();

    void train(Duple<Input, Output>[] data);
}
