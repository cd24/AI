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
    double learningRate = 0.1, discount = 0.05, exploration = 0.06, explorationCap = 0.5;
    double explorationDecay = 0.01, learningDecay = 0.001, learningFloor = 0.01, explorationFloor = 0.01;

    @Override
    public void control(Simulator sim) {
        QState current = getState(sim);
        //System.out.println(sim.findClosest());

        if (learner == null){
            transitionCounts = new HashMap<>();
            learner = new QLearner(current, learningRate, discount);
            learner.exploration = exploration;
        }
        if (previous == null){
            previous = current;
            transitCounts = new double[previous.numStates()][previous.numStates()];
            return;
        }
        Action currentAction = learner.getAction(current);
        currentAction.applyTo(sim);

        if (sim.wasHit()){
            learner.updateValue(current, currentAction, -learningRate);
            learner.updateValue(previous, lastAction, -learningRate);
            if (learner.exploration < explorationCap) {
                learner.exploration += 0.1 * exploration;
                learner.learning_rate += learningDecay*learningRate;
            }
        }
        else if (lastAction == Action.FORWARD){
            learner.updateValue(previous, lastAction, learningRate);
        }
        else  if (lastAction != null){
            learner.updateValue(previous, lastAction, learningRate*learningRate);
        }
        previous = current;
        lastAction = currentAction;
        updateData(current);
        if (learner.learning_rate > learningFloor)
            learner.learning_rate -= (learner.learning_rate*learningDecay);
        if (learner.exploration > explorationFloor)
            learner.exploration -= (learner.exploration * explorationDecay);
    }

    public QState getState(Simulator sim){
        double distance = sim.findClosest();
        return QState.forDistance(distance);
    }

    public void updateData(QState current){
        transitCounts[previous.index()][current.index()] += 1;
        learner.updateMarkovWithData(current, transitCounts);
    }
}
