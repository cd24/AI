package maze.heuristics;

import maze.core.MazeExplorer;
import search.core.BestFirstHeuristic;

public class Openness implements BestFirstHeuristic<MazeExplorer> {
    /*
        Calculates beased on the number of open spaces around the cell.
    * */
    public int getDistance(MazeExplorer node, MazeExplorer goal) {
        int numOpenNeighbors = node.getSuccessors().size();
        return 4 - numOpenNeighbors;  // a cell with fewer neighbors is preferred to cells with more.
    }
}
