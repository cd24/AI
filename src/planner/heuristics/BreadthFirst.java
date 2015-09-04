package planner.heuristics;

import planner.core.PlanStep;
import search.core.BestFirstHeuristic;

public class BreadthFirst implements BestFirstHeuristic<PlanStep> {

	@Override
	public int getDistance(PlanStep node, PlanStep goal) {
		return 0;
	}

}
