package maze.core;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Set;
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
	}
	
	public MazeCell getLocation() {return location;}

	@Override
	public ArrayList<MazeExplorer> getSuccessors() {
		ArrayList<MazeExplorer> result = new ArrayList<MazeExplorer>();
		ArrayList<MazeCell> cells = maze.getNeighbors(this.location);
		for (MazeCell cell : cells){
			if (!maze.blocked(this.location, cell)){
				MazeExplorer explorer = new MazeExplorer(this.maze, cell);
				explorer.treasureFound.addAll(this.treasureFound);
				if (maze.isTreasure(cell)) {
					explorer.treasureFound.add(cell);
				}
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

	public int getNumTreasures(){
		return treasureFound.size();
	}

	public MazeCell getClosestTreasure(){
		Set<MazeCell> treasures = maze.getTreasures();
		MazeCell closest = null;
		double closest_distance = 0;
		for (MazeCell t : treasures){
			if (closest == null){
				closest = t;
				closest_distance = cartesianDistance(t);
				continue;
			}
			else if (treasureFound.contains(t)){
				continue;
			}
			double curr_dist = cartesianDistance(t);
			if (curr_dist < closest_distance){
				closest = t;
				closest_distance = curr_dist;
			}
		}
		return closest;
	}

	public int distanceFromLastTreasure(){
		if (treasureFound.size() > 0) {
			MazeCell last_treasure = treasureFound.last();
			int distance = last_treasure.getManhattanDist(this.location);
			return distance;
		}
		return 0;
	}

	public double cartesianDistance(MazeCell other){
		int a = Math.abs(this.location.X() - other.X());
		int b = Math.abs(this.location.Y() - other.Y());
		return Math.sqrt(a*a + b*b);
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
