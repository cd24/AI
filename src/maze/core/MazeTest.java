package maze.core;

import static org.junit.Assert.*;

import maze.gui.AIReflector;
import org.junit.Test;

import search.core.BestFirstHeuristic;
import search.core.BestFirstSearcher;

import java.io.*;
import java.util.ArrayList;

public class MazeTest {
	final static int NUM_TESTS = 100;
	final static int WIDTH = 10, HEIGHT = 15;
	final static String homeDir = System.getProperty("user.home") + File.separator + "Desktop",
			customFunctionPerformanceFile = "UserPerformanceResults.csv" ;

	@Test
	public void testNoTreasure() {
		for (int i = 0; i < NUM_TESTS; ++i) {
			Maze m = new Maze(WIDTH, HEIGHT);
			m.makeMaze(new MazeCell(0, 0), new MazeCell(WIDTH - 1, HEIGHT - 1), 0, 1);
			BestFirstSearcher<MazeExplorer> searcher = new BestFirstSearcher<>(new maze.heuristics.BreadthFirst());
			MazeExplorer endNode = new MazeExplorer(m, m.getEnd());
			searcher.solve(new MazeExplorer(m, m.getStart()), endNode);
			assertTrue(searcher.success());
			MazePath path = new MazePath(searcher, m);
			assertTrue(path.solvesMaze(m));
		}		
	}

	@Test
	public void testMany() {
		for (int i = 0; i < NUM_TESTS; ++i) {
			Maze m = new Maze(WIDTH, HEIGHT);
			m.makeMaze(new MazeCell(0, 0), new MazeCell(WIDTH - 1, HEIGHT - 1), 2, 1);
			BestFirstSearcher<MazeExplorer> searcher = new BestFirstSearcher<>(new maze.heuristics.BreadthFirst());
			MazeExplorer endNode = new MazeExplorer(m, m.getEnd());
			endNode.addTreasures(m.getTreasures());
			searcher.solve(new MazeExplorer(m, m.getStart()), endNode);
			assertTrue(searcher.success());
			MazePath path = new MazePath(searcher, m);
			assertTrue(path.solvesMaze(m));
		}
	}
	
	@Test
	public void testBestFirst() {
		int totalBest = 0, totalBreadth = 0;
		for (int i = 0; i < NUM_TESTS; ++i) {
			Maze m = new Maze(WIDTH, HEIGHT);
			m.makeMaze(new MazeCell(0, 0), new MazeCell(WIDTH - 1, HEIGHT - 1), 0, 1);
			BestFirstSearcher<MazeExplorer> breadthFirst = new BestFirstSearcher<>(new maze.heuristics.BreadthFirst());
			BestFirstSearcher<MazeExplorer> bestFirst = new BestFirstSearcher<>((n, goal) -> goal.getLocation().X() - n.getLocation().X());
			MazeExplorer startNode = new MazeExplorer(m, m.getStart());
			MazeExplorer endNode = new MazeExplorer(m, m.getEnd());
			breadthFirst.solve(startNode, endNode);
			bestFirst.solve(startNode, endNode);
			assertTrue(breadthFirst.success());
			assertTrue(bestFirst.success());
			totalBest += bestFirst.getNumNodes();
			totalBreadth += breadthFirst.getNumNodes();
		}
		assertTrue(totalBest < totalBreadth);
	}

	@Test
	public void testUserDefined() throws ClassNotFoundException, IllegalAccessException, InstantiationException, IOException {
		PrintWriter writer = new PrintWriter(homeDir + File.separator + customFunctionPerformanceFile, "UTF-8");
		String prefix = "maze.heuristics";
		AIReflector reflector = new AIReflector(BestFirstHeuristic.class, prefix);
		ArrayList<String> names = reflector.getTypeNames();

		for (String name : names){
			writeToFile("nodes, depth, b*, solution length, " + name, writer);
			String qualifiedName = prefix + "." + name;
			BestFirstHeuristic<MazeExplorer> instance = (BestFirstHeuristic<MazeExplorer>) Class.forName(qualifiedName).newInstance();
			testUserHeuristic(instance, writer);
		}
		writer.close();
	}

	public void testUserHeuristic(BestFirstHeuristic hr, PrintWriter writer) throws IOException {
		for (int i = 0; i < NUM_TESTS; ++i) {
			Maze m = new Maze(WIDTH, HEIGHT);
			m.makeMaze(new MazeCell(0, 0), new MazeCell(WIDTH - 1, HEIGHT - 1), 0, 1);
			BestFirstSearcher<MazeExplorer> bestFirst = new BestFirstSearcher<>(hr);
			MazeExplorer startNode = new MazeExplorer(m, m.getStart());
			MazeExplorer endNode = new MazeExplorer(m, m.getEnd());
			bestFirst.solve(startNode, endNode);
			assertTrue(bestFirst.success());
			writeToFile(bestFirst.getNumNodes() + ", " + bestFirst.getMaxDepth() + ", " + bestFirst.getBranchingFactor(Double.MAX_VALUE) + ", " + bestFirst.numSteps(), writer);
		}
	}

	public void writeToFile(String line, PrintWriter fos) throws IOException {
		fos.println(line);
	}
}
