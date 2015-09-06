package planner.heuristics;

import planner.core.PlanGraph;
import planner.core.PlanStep;
import search.core.BestFirstHeuristic;

/**
 * Created by John on 9/6/2015.
 */
public class NoDelete implements BestFirstHeuristic<PlanStep> {
    @Override
    public int getDistance(PlanStep node, PlanStep goal) {
        return new PlanGraph(node.getDomain(), node.getWorldState(), goal.getProblem())
                .extractNoDeletePlan()
                .length();
    }
}
