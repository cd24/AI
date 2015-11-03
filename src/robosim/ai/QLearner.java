package robosim.ai;

import handwriting.learners.DecisionTree;
import robosim.core.Action;
import robosim.core.Simulator;

import java.util.ArrayList;
import java.util.DoubleSummaryStatistics;
import java.util.HashMap;
import java.util.Random;
import java.util.concurrent.TransferQueue;


public class QLearner {
    QState current;
    HashMap<QState, HashMap<Action, Double>> values;
    HashMap<QState, ArrayList<Transition>> transitions;
    QState[] states;
    double learning_rate, discount, exploration = 0.3;
    Random random = new Random();
    public QLearner(QState current, double learning_rate, double discount){
        this.current = current;
        this.discount = discount;
        this.learning_rate = learning_rate;
        this.states = new QState[]{QState.MIDDLE, QState.NEAR, QState.FAR};
        this.values = new HashMap<>();
        this.transitions = new HashMap<>();

        fillTransitions();
    }

    private void fillTransitions(){
        double likelyhood = 1.0/states.length;
        for (QState state: states){
            ArrayList<Transition> transition = transitions.getOrDefault(state, new ArrayList<>());
            for (QState otherState : states){
                Transition trans = new Transition(state, otherState, likelyhood);
                transition.add(trans);
            }
            transitions.put(state, transition);
        }
    }

    public Action getAction(QState current){
        if (Math.random() < exploration){
            return Action.values()[random.nextInt(Action.values().length)];
        }
        Action greedyBest = Action.FORWARD;
        double bestVal = Double.MIN_VALUE;
        HashMap<Action, Double> vals = this.values.getOrDefault(current, new HashMap<>());
        for (Action action : vals.keySet()){
            double val = vals.get(action);
            if (bestVal < val) {
                bestVal = val;
                greedyBest = action;
            }
        }
        return greedyBest;
    }

    public double getValue(QState state, Action action){
        HashMap<Action, Double> info = values.getOrDefault(state, new HashMap<>());
        return info.getOrDefault(action, 0.0);
    }

    public void updateValue(QState state, Action action, double reward){
        HashMap<Action, Double> subset = values.getOrDefault(state, new HashMap<>());
        QState expectedState = getExpectedState(state, action);
        HashMap<Action, Double> expectedRow = values.getOrDefault(expectedState, new HashMap<>());
        double value = (1-learning_rate)*getValue(state, action) +
                        learning_rate*(reward + (discount*expectedRow.getOrDefault(action, 0.0)));
        subset.put(action, value);
        values.put(state, subset);
    }

    public QState getExpectedState(QState state, Action a){
        double percent = Math.random();
        ArrayList<Transition> trans = transitions.get(state);
        QState guess = QState.FAR;
        int count = 0;
        for (int i = 0; i < trans.size(); ++i){
            count += trans.get(i).probability;
            if (percent < count){
                guess = trans.get(i).destination;
                break;
            }
        }
        return guess;
    }

    public void updateMarkovWithData(QState source, HashMap<Transition, Double> counts){
        ArrayList<Transition> transits = new ArrayList<>();
        double total = 0;
        for (Transition key: counts.keySet()){
            total += counts.get(key);
        }
        for (Transition key: counts.keySet()){
            double count = counts.get(key);
            Transition transition = new Transition(source, key.destination, count/total);
            transits.add(transition);
        }
        transitions.put(source, transits);
    }

    public static class Transition{
        private QState source, destination;
        public double probability;
        public Transition(QState source, QState destination, double probability){
            this.source = source;
            this.destination = destination;
            this.probability = probability;
        }
    }
}
