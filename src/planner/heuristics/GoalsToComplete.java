package planner.heuristics;

import planner.core.PlanStep;
import search.core.BestFirstHeuristic;

public class GoalsToComplete implements BestFirstHeuristic<PlanStep> {
    @Override
    public int getDistance(PlanStep node, PlanStep goal) {
        return node.getWorldState().unmetGoals(goal.getWorldState()).size();
    }
}
