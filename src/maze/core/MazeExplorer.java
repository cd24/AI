package maze.core;

import java.util.ArrayList;
import java.util.Collection;
import java.util.TreeSet;

import search.core.BestFirstObject;

public class MazeExplorer implements BestFirstObject<MazeExplorer> {
	private Maze maze;
	private MazeCell location;
	private TreeSet<MazeCell> treasureFound; 
	
	public MazeExplorer(Maze m, MazeCell location) {
		this.maze = m;
		this.location = location;
		treasureFound = new TreeSet<>();
		if (maze.isTreasure(location))
			this.treasureFound.add(location);
	}
	
	public MazeCell getLocation() {return location;}

	@Override
	public ArrayList<MazeExplorer> getSuccessors() {
		ArrayList<MazeExplorer> result = new ArrayList<MazeExplorer>();
		MazeExplorer explorer;
		ArrayList<MazeCell> cells = maze.getNeighbors(this.location);
		for (MazeCell cell : cells){
			if (!maze.blocked(this.location, cell)){
				explorer = new MazeExplorer(this.maze, cell);
				explorer.treasureFound.addAll(this.treasureFound);
				result.add(explorer);
			}
		}
        return result;
	}
	
	public void addTreasures(Collection<MazeCell> treasures) {
		treasureFound.addAll(treasures);
	}
	
	public String toString() {
		StringBuilder treasures = new StringBuilder();
		for (MazeCell t: treasureFound) {
			treasures.append(";");
			treasures.append(t.toString());
		}
		return "@" + location.toString() + treasures.toString();
	}
	
	@Override
	public int hashCode() {return toString().hashCode();}
	
	@Override
	public boolean equals(Object other) {
		if (other instanceof MazeExplorer) {
			return achieves((MazeExplorer)other);
		} else {
			return false;
		}
	}

	@Override
	public boolean achieves(MazeExplorer goal) {
		return this.location.equals(goal.location) && this.treasureFound.equals(goal.treasureFound);
	}

}
