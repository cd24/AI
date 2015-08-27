package maze.heuristics;

import maze.core.MazeExplorer;
import search.core.BestFirstHeuristic;

/**
 * Created by John on 8/27/2015.
 */
public class TreasureProximity implements BestFirstHeuristic<MazeExplorer> {
    public int getDistance(MazeExplorer node, MazeExplorer goal) {
        int treasuresInNeighbors = 0;
        for (MazeExplorer explorer : node.getSuccessors()){
            treasuresInNeighbors += explorer.getNumTreasures() > node.getNumTreasures() ? 1 : 0;
        }
        return treasuresInNeighbors;
    }
}
