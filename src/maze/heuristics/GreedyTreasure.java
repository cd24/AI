package maze.heuristics;

import maze.core.MazeCell;
import maze.core.MazeExplorer;
import search.core.BestFirstHeuristic;


public class GreedyTreasure implements BestFirstHeuristic<MazeExplorer>{
    @Override
    public int getDistance(MazeExplorer node, MazeExplorer goal) {
        MazeCell closest_treasure = node.getClosestTreasure();
        //only do treasure search if there is a treasure.
        if (closest_treasure != null)
            return (int) node.cartesianDistance(closest_treasure);
        else
            return 0;
    }
}
