package maze.core;

import static org.junit.Assert.*;

import junit.framework.TestResult;
import maze.gui.AIReflector;
import org.junit.Test;

import search.core.BestFirstHeuristic;
import search.core.BestFirstSearcher;

import java.io.*;
import java.util.ArrayList;
import java.util.HashMap;

public class MazeTest {
	final static int NUM_TESTS = 100;
	static int WIDTH = 10, HEIGHT = 15, NUM_TREASURES = 0;
	static double PERFECTION = 0.50;
	static String NAME = "No Treasure Tests";
	final static String homeDir = System.getProperty("user.home") + File.separator + "Desktop",
			customFunctionPerformanceFile = "UserPerformanceResults.csv" ;

	static HashMap<String, ArrayList<TestResult>> results = new HashMap<>();

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
		System.out.println("This test will take ~30 minutes to run.\nBe prepared to wait, or reduced the maze dimension using the helper function included on line 79 of MazeTest.java.");
		PrintWriter writer = new PrintWriter(homeDir + File.separator + customFunctionPerformanceFile, "UTF-8");
		setMazeDimensions(100, 100);
		testWithParameters(writer);

		for (double i = 0.25; i <= 1; i += 0.25){
			configurePerfections(i);
			testWithParameters(writer);
		}

		for (int i = 0; i <= 1000; i += 250){
			configureTreasure(i, true);
			testWithParameters(writer);
		}

		for (double i = 0.25; i <= 1; i += 0.25){
			configureSmallMazes(i);
			testWithParameters(writer);
		}

		writer.close();
	}

	public void configureTreasure(int num_treasures, boolean perfect){
		NAME = "Treasure Tests";
		NUM_TREASURES = num_treasures;
		PERFECTION = perfect ? 1 : 0.50;
	}

	public void configurePerfections(double perfection){
		NAME = "Perfection Test";
		NUM_TREASURES = 5;
		PERFECTION = perfection;
	}

	public void configureSmallMazes(double perfection){
		setMazeDimensions(10, 15);
		NAME = "Small Maze Tests";
		NUM_TREASURES = 5;
		PERFECTION = perfection;
	}

	public void setMazeDimensions(int width, int height){
		WIDTH = width;
		HEIGHT = height;
	}

	public void printResult(String name, TestResult result, PrintWriter writer){
		writer.println(name + "," + result.num_nodes + ", " + result.max_depth + ", " + result.branching_factor + ", " + result.num_steps + ", " + (result.succeeded ? "SUCCEEDED" : "FAILED") + ", " + 1);
	}

	public void testWithParameters(PrintWriter writer) throws IOException, ClassNotFoundException, IllegalAccessException, InstantiationException {
		String prefix = "maze.heuristics";
		AIReflector reflector = new AIReflector(BestFirstHeuristic.class, prefix);
		ArrayList<String> names = reflector.getTypeNames();
		Maze m = new Maze(WIDTH, HEIGHT);
		for (int i = 0; i < NUM_TESTS; ++i) {
			m.makeMaze(new MazeCell(m.getXMax(), m.getYMin()),
					new MazeCell(m.getXMin(), m.getYMax()),
					NUM_TREASURES,
					PERFECTION);
			for (String name : names) {
				if (!results.containsKey(name)){
					results.put(name, new ArrayList<>());
				}
				String qualifiedName = prefix + "." + name;
				BestFirstHeuristic<MazeExplorer> instance = (BestFirstHeuristic<MazeExplorer>) Class.forName(qualifiedName).newInstance();
				testUserHeuristic(instance, results.get(name), m);
			}
		}
		writeResultsToFile(writer);
		reset();
	}

	public void testUserHeuristic(BestFirstHeuristic hr, ArrayList<TestResult> res, Maze m) throws IOException {
		BestFirstSearcher<MazeExplorer> bestFirst = new BestFirstSearcher<>(hr);
		MazeExplorer startNode = new MazeExplorer(m, m.getStart());
		MazeExplorer endNode = new MazeExplorer(m, m.getEnd());
		bestFirst.solve(startNode, endNode);

		if(bestFirst.success()){
			double b = bestFirst.getBranchingFactor(0.01);
			b = (double)((int)(b * 100)) / 100;
			TestResult result = new TestResult(bestFirst.getNumNodes(), bestFirst.getMaxDepth(), b, bestFirst.numSteps());
			res.add(result);
		}
		else{
			res.add(new TestResult(false));
		}
		System.gc();
	}

	public void writeResultsToFile(PrintWriter writer){
		writer.println(NAME + ",nodes,depth,b*,solution length (Unreliable -- Averaging problem),times failed,number of trials,, Maze Details:, "  + "Maze (W " + WIDTH + "|H " + HEIGHT + "), Number Treasures: " + NUM_TREASURES + ", Maze Perfection: " + PERFECTION);

		for (String heuristic : results.keySet()){
			double num_nodes = 0, max_depth = 0, branching_factor = 0, num_steps = 0;
			int times_failed = 0, num_trials = results.get(heuristic).size();
			for (TestResult result: results.get(heuristic)){
				if (result.succeeded){
					num_nodes += result.num_nodes;
					max_depth += result.max_depth;
					branching_factor += result.branching_factor;
					num_steps += result.num_steps;
				}
				else{
					times_failed += 1;
					break;
				}
			}
			num_nodes = num_nodes/num_trials;
			max_depth = max_depth/num_trials;
			branching_factor = branching_factor/num_trials;
			num_steps = num_steps/num_trials;

			writer.println(heuristic + "," + num_nodes + ", " + max_depth + ", " + branching_factor + ", " + num_steps + ", " + times_failed + ", " + num_trials);
		}
	}

	public void reset(){
		results = new HashMap<>();
	}

	public void writeToFile(String line, PrintWriter fos) throws IOException {
		fos.println(line);
	}

	public static class TestResult {
		public double num_nodes, max_depth, branching_factor, num_steps, completeness = 1;
		public boolean succeeded = true;
		public int treasure_count = 0;
		String type = "No Treasure";
		public TestResult(double nodes, double depth, double branching_factor, double num_steps){
			this.num_nodes = nodes;
			this.max_depth = depth;
			this.branching_factor = branching_factor;
			this.num_steps = num_steps;
		}
		public TestResult(boolean succeeded){
			this.succeeded = succeeded;
		}
	}
}
