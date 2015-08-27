package maze.heuristics;

import maze.core.MazeCell;
import maze.core.MazeExplorer;
import search.core.BestFirstHeuristic;

/**
 * Created by John on 8/26/2015.
 */
public class Manhattan implements BestFirstHeuristic<MazeExplorer> {
    public int getDistance(MazeExplorer node, MazeExplorer goal) {
        MazeCell goalCell = goal.getLocation();
        MazeCell nodeLocation = node.getLocation();
        int manhattanDistance = goalCell.getManhattanDist(nodeLocation);
        return manhattanDistance;
    }
}
