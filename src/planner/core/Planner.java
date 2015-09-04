package planner.core;

public interface Planner {
	Plan makePlan(Domain d, Problem p);
	
	int getNumNodes();
	
	double getBranchingFactor();
	
	int getMaxDepth();
}
