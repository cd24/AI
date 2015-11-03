package robosim.ai;

import planner.core.Domain;
import robosim.core.Action;
import robosim.core.Simulator;

import java.util.ArrayList;
import java.util.HashMap;

public class QLearingAI implements Controller {
    HashMap<QState, HashMap<QLearner.Transition, Double>> transitionCounts;
    double[][] transitCounts; //Source, Destination;
    QLearner learner;
    QState previous;
    Action lastAction;
    public static final double close = 25, middle = 50, far = 100;
    double learningRate = 0.3, discount = 0.1;

    @Override
    public void control(Simulator sim) {
        QState current = getState(sim);
        System.out.println(sim.findClosest());

        if (learner == null){
            transitionCounts = new HashMap<>();
            learner = new QLearner(current, learningRate, discount);
        }

        if (sim.wasHit()){
            if (lastAction != null) {
                learner.updateValue(current, lastAction, -learningRate);
                if (lastAction != Action.BACKWARD){
                    lastAction = Action.BACKWARD;
                    lastAction.applyTo(sim);
                }
            }
        }
        else if (lastAction == Action.FORWARD && lastAction != null){
            learner.updateValue(current, lastAction, learningRate);
        }
        else  if (lastAction != null){
            learner.updateValue(current, lastAction, learningRate*learningRate);
        }

        updateData(current);
        lastAction = learner.getAction(current);
        lastAction.applyTo(sim);
    }

    public QState getState(Simulator sim){
        QState current;
        double distance = sim.findClosest();
        if (distance < close)
            current = QState.NEAR;
        else if (distance < middle)
            current = QState.MIDDLE;
        else
            current = QState.FAR;
        return current;
    }

    public void updateData(QState current){
        if (previous == null){
            previous = current;
            transitCounts = new double[previous.numStates()][previous.numStates()];
            return;
        }
        transitCounts[previous.index()][current.index()] += 1;
        learner.updateMarkovWithData(current, transitCounts);
        previous = current;
    }
}
