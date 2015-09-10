package planner.heuristics;

import planner.core.PlanStep;
import search.core.BestFirstHeuristic;

/**
 * Created by john on 9/10/15.
 */
public class DeliveryHeuristic implements BestFirstHeuristic<PlanStep> {

    @Override
    public int getDistance(PlanStep node, PlanStep goal) {
        return 0;
    }
}
