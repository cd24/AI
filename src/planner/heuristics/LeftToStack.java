package planner.heuristics;

import planner.core.PlanStep;
import planner.core.Predicate;
import search.core.BestFirstHeuristic;

import java.util.Set;

public class LeftToStack implements BestFirstHeuristic<PlanStep> {
    @Override
    public int getDistance(PlanStep node, PlanStep goal) {
        Set<Predicate> unmetGoals = node.getWorldState().unmetGoals(goal.getWorldState());
        int toStack = 0;
        for (Predicate predicate : unmetGoals){
            if (predicate.getName().contains("stack"))
                ++toStack;
        }
        return toStack;
    }
}
