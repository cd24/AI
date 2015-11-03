package robosim.ai;

import planner.core.Domain;
import robosim.core.Action;
import robosim.core.Simulator;

import java.util.ArrayList;
import java.util.HashMap;

public class QLearingAI implements Controller {
    HashMap<QState, HashMap<QLearner.Transition, Double>> transitionCounts;
    QLearner learner;
    QState previous;
    Action lastAction;
    public static final double close = 0.5, middle = 2.0, far = 4.0;
    double learningRate = 0.3, discount = 0.1;

    @Override
    public void control(Simulator sim) {
        QState current = getState(sim);

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
            return;
        }
        HashMap<QLearner.Transition, Double> values = transitionCounts.getOrDefault(previous, new HashMap<>());
        QLearner.Transition trans = new QLearner.Transition(previous, current, 0);
        double value = values.getOrDefault(trans, 0.0) + 1;
        values.put(trans, value);
        learner.updateMarkovWithData(current, values);
        transitionCounts.put(previous, values); // it is important to note that I have stopped caring about memory pressures at this point.
    }
}
