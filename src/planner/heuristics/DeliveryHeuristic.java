package planner.heuristics;

import planner.core.PlanStep;
import planner.core.Predicate;
import search.core.BestFirstHeuristic;

import java.util.HashMap;

/**
 * Created by john on 9/10/15.
 */
public class DeliveryHeuristic implements BestFirstHeuristic<PlanStep> {

    @Override
    public int getDistance(PlanStep node, PlanStep goal) {
        HashMap<String, String> connected = new HashMap<>();
        for (Predicate state : node.getWorldState()){
            if (state.getName().contains("can-move")){
                connected.put("robot", state.getParams().get(0));
            }
            else if (state.getName().contains("on")){
                connected.put(state.getParams().get(0), state.getParams().get(1));
            }
        }

        int counter = 0;

        for (Predicate state : node.getWorldState()){
            if (state.getName().contains("connected")){
                String r1 = state.getParams().get(0), r2 = state.getParams().get(1);
                if (r1 == connected.get("robot") || r2 == connected.get("robot")){
                    ++counter;
                }
            }
        }
        return counter;
    }
}
