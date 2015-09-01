package maze.heuristics;

import maze.core.MazeExplorer;
import search.core.BestFirstHeuristic;

/**
 * Created by John on 8/30/2015.
 */
public class Cartesian implements BestFirstHeuristic<MazeExplorer> {
    @Override
    public int getDistance(MazeExplorer node, MazeExplorer goal) {
        return (int) node.cartesianDistance(goal.getLocation());
    }
}
