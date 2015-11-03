package robosim.ai;

import handwriting.learners.DecisionTree;
import robosim.core.Action;

import java.util.ArrayList;
import java.util.DoubleSummaryStatistics;
import java.util.HashMap;


public class QLearner {
    QState current;
    HashMap<QState, HashMap<Action, Double>> values; //source, transitions
    QState[] states;
    double learning_rate;
    public QLearner(QState current, double learning_rate){
        this.current = current;
        this.states = new QState[]{QState.MIDDLE, QState.NEAR, QState.FAR};
        this.values = new HashMap<>();
        this.learning_rate = learning_rate;
    }

    public double getValue(QState state, Action action){
        HashMap<Action, Double> info = values.getOrDefault(state, new HashMap<>());
        return info.getOrDefault(action, 0.0);
    }

    public void updateValue(QState state, Action action){
        HashMap<Action, Double> subset = values.getOrDefault(state, new HashMap<>());
        double reward = (1-learning_rate) * getValue(state, action) + learning_rate;
    }
}
