package planner.heuristics;

import planner.core.PlanStep;
import planner.core.Predicate;
import search.core.BestFirstHeuristic;

/**
 * Created by john on 9/10/15.
 */
public class PassengersToDeliver implements BestFirstHeuristic<PlanStep> {

    @Override
    public int getDistance(PlanStep node, PlanStep goal) {
        int peopleToDeliver = 0;
        for (Predicate g : node.getWorldState()){
            boolean isOn = g.getName().contains("on");
            if (isOn){
                peopleToDeliver += 1;
            }
        }
        return peopleToDeliver;
    }
}
