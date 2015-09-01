package maze.heuristics;

import maze.core.MazeExplorer;
import search.core.BestFirstHeuristic;

/**
 * Created by John on 8/30/2015.
 */
public class TreasureTrailer  implements BestFirstHeuristic<MazeExplorer>{
    @Override
    public int getDistance(MazeExplorer node, MazeExplorer goal) {
        return node.distanceFromLastTreasure();
    }
}
