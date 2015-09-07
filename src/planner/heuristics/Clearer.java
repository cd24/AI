package planner.heuristics;

import planner.core.PlanStep;
import planner.core.Predicate;
import search.core.BestFirstHeuristic;

import java.util.Set;

public class Clearer implements BestFirstHeuristic<PlanStep> {
    @Override
    public int getDistance(PlanStep node, PlanStep goal) {
        Set<Predicate> preds =  node.getWorldState().unmetGoals(goal.getWorldState());
        int num_clear = 0;
        for (Predicate pred : preds){
            if (pred.getName().contains("clear")){
                num_clear += 1;
            }
        }
        return num_clear;
    }
}
