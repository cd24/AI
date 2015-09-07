package planner.core;

import java.util.*;

public class PlanGraph {
	private final static boolean DEBUG = false;
	private static HashMap<State,State> state2newState = new HashMap<>();
	private static HashMap<State,ArrayList<Action>> state2newLevel = new HashMap<>();
	
	private Set<Action> used;
	
	private ArrayList<ArrayList<Action>> actions;
	private Map<Predicate,Action> firstAdders;
	private State start, goals;
	private boolean allGoalsReached;
	
	public boolean allGoalsReached() {return allGoalsReached;}
	
	public Plan extractNoDeletePlan() {
		return extractNoDeletePlan(start);
	}
	
	public static Plan makeNoDeletePlan(Domain d, State current, Problem p) {
		return new PlanGraph(d, current, p).extractNoDeletePlan();
	}
	
	public Plan extractNoDeletePlan(State current) {
		Queue<Predicate> predicates = new LinkedList<>();
		predicates.addAll(current.unmetGoals(goals));
		ArrayList<Action> currentActions = new ArrayList<>();
		while (!predicates.isEmpty()){
			Predicate curr = predicates.poll();
			boolean isTrue = current.predIsTrue(curr);
			if (!isTrue){
				Action addedBy = firstAdders.get(curr);
				currentActions.add(0, addedBy);
				State preconditions = addedBy.getPreconditions();
				for (Predicate predicate : preconditions){
					predicates.add(predicate);
				}
			}
		}

		NoDeletePlan plan = new NoDeletePlan();
		for (Action action: currentActions){
			plan.appendAction(action);
		}

		return plan;
	}
	
	public PlanGraph(Domain d, State current, Problem p) {
		used = new HashSet<>();
		
		start = current;
		goals = p.getGoals();
		actions = new ArrayList<>();
		firstAdders = new HashMap<>();
		while (!current.allGoalsMet(goals)) {
			State prev = current;
			current = addNewLevel(current, d);
			if (prev.equals(current)) {
				allGoalsReached = false;
				return;
			}
		}
		allGoalsReached = true;
		if (DEBUG) {System.out.println("levels: " + actions.size());}
	}

	private State addNewLevel(State current, Domain d) {
		if (state2newState.containsKey(current)) {
			ArrayList<Action> level = state2newLevel.get(current);
			actions.add(level);
			for (Action act: level) {
				used.add(act);
				updateFirstAdders(act);
			}
			return state2newState.get(current);
		} else {
			State startState = current;
			ArrayList<Action> level = new ArrayList<Action>();
			long start = System.currentTimeMillis();
			Set<Action> doable = d.makeInstantiatedActions(current);
			if (DEBUG) System.out.println("To make actions took " + (System.currentTimeMillis() - start) + " ms");
			for (Action act: doable) {
				if (!used.contains(act)) {
					used.add(act);
					level.add(act);
					current = applyAction(act, current);
				}
			}
			actions.add(level);
			if (DEBUG) {System.out.println("level: " + actions.size() + " width: " + level.size() + " states: " + current.size() + " actions: " + doable.size());}
			state2newState.put(startState, current);
			state2newLevel.put(startState, level);
			return current;
		}
	}
	
	private void updateFirstAdders(Action act) {
		for (Predicate added: act.getAddEffects()) {
			if (!firstAdders.containsKey(added)) {
				firstAdders.put(added, act);
			}
		}
	}
	
	private State applyAction(Action act, State current) {
		updateFirstAdders(act);
		return new State(current, act.getAddEffects());
	}
	
	public int getGraphDepth() {
		return actions.size();
	}
}
