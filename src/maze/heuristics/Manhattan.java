package maze.heuristics;

import maze.core.MazeExplorer;
import search.core.BestFirstHeuristic;

/**
 * Created by John on 8/26/2015.
 */
public class Manhattan implements BestFirstHeuristic<MazeExplorer> {
    public int getDistance(MazeExplorer node, MazeExplorer goal) {
        return 0;
    }
}
