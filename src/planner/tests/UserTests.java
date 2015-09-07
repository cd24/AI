package planner.tests;

import maze.core.MazeTest;
import maze.gui.AIReflector;
import org.junit.Test;
import planner.core.*;
import search.core.BestFirstHeuristic;

import javax.swing.text.Style;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.PrintWriter;
import java.io.UnsupportedEncodingException;
import java.time.Instant;
import java.util.*;
import java.util.concurrent.SynchronousQueue;
import java.util.regex.Pattern;

public class UserTests {

    public static final String INDIVIDUAL_RUNS = "raw_data.csv",
            AGGREGATED = "user_heuristics.csv",
            SAVE_LOCATION = System.getProperty("user.home") + File.separator + "Desktop";
    public static final boolean LONG_RUN = true;

    @Test
    public void testUserHeuristics() throws ClassNotFoundException, IllegalAccessException, InstantiationException, FileNotFoundException, UnsupportedEncodingException {
        double start_time = System.currentTimeMillis(), end_time;
        String prefix = "planner.heuristics";
        AIReflector<BestFirstHeuristic<PlanStep>> reflector = new AIReflector<>(BestFirstHeuristic.class, prefix);
        HashMap<String, ArrayList<Result>> results = new HashMap<>();
        String raw_file = SAVE_LOCATION + File.separator + INDIVIDUAL_RUNS,
               aggregated_file = SAVE_LOCATION + File.separator + AGGREGATED;
        PrintWriter raw_writer = new PrintWriter(raw_file, "UTF-8"), aggregated_writer = new PrintWriter(aggregated_file, "UTF-8");
        String[] simpleFiles = getTestFiles(true);
        String[] toughFiles = getProblemFiles(false);
        String[] files = LONG_RUN ? toughFiles : simpleFiles;
        ArrayList<String> names = reflector.getTypeNames();
        if (LONG_RUN){
            System.out.println("Running with all possible tests.  This could take several hours and will fill no less than 4 gb of memory.");
        }
        for (String name : names){
            String qualifiedName = prefix + "." + name;
            System.out.println("Currently working on: " + name);
            int numTests = LONG_RUN ? 5 : 13;
            for (int i = 0; i < numTests; ++i){
                String test_file = files[i];
                System.out.println("\tWorking on test: " + test_file);
                performSimpleTest(qualifiedName, results, test_file);
                System.gc();
            }
        }

        writeRawData(raw_writer, results);
        raw_writer.close();
        aggregated_writer.close();
        end_time = System.currentTimeMillis();
        System.out.println("Total run time (MS): " + (end_time - start_time));
    }

    public String[] getTestFiles(boolean simple){
        String[] prefix = new String[]{"domains", (simple ? "blocks" : "blocks2")};
        String[] test_files = getProblemFiles(simple);
        ArrayList<String> files = new ArrayList<>();
        for (String file : test_files){
            String path = PlanGraphTest.path2String(prefix[0], prefix[1], file) + ".pddl";
            files.add(path);
        }
        return files.toArray(new String[]{});
    }

    public void performSimpleTest(String heuristicName, HashMap<String, ArrayList<Result>> resultMap, String test_file) throws ClassNotFoundException, IllegalAccessException, InstantiationException {
        BestFirstHeuristic<PlanStep> heuristic = (BestFirstHeuristic<PlanStep>) Class.forName(heuristicName).newInstance();
        Domain domain = new Domain(getDomain(true));
        BestFirstPlanner planner = new BestFirstPlanner(heuristic);
        Problem problem = new Problem(new File(test_file));
        long testStart = System.currentTimeMillis(), total_time;
        Plan p = planner.makePlan(domain, problem);
        total_time = System.currentTimeMillis() - testStart;

        Result result = new Result(planner.getNumNodes(), planner.getMaxDepth(), planner.getBranchingFactor(), p.length());
        result.succeeded = p.isPlanValid(problem);
        result.wasSimple = true;
        result.duration = total_time;
        String[] seperatedPath = test_file.split(Pattern.quote(File.separator));
        result.test_name = seperatedPath[seperatedPath.length - 1];

        if (!resultMap.containsKey(heuristicName)){
            resultMap.put(heuristicName, new ArrayList<>());
        }
        resultMap.get(heuristicName).add(result);
    }

    public String getDomain(boolean simple){
        return simple ?
                PlanGraphTest.path2String("domains", "blocks", "domain.pddl") :
                PlanGraphTest.path2String("domains", "blocks2", "domain.pddl");
    }

    public void writeRawData(PrintWriter raw_writer, HashMap<String, ArrayList<Result>> data){
        HashMap<String, HashMap<String, Result>> orderedResults = new HashMap<>();
        for (String key : data.keySet()){
            ArrayList<Result> results = data.get(key);
            for (Result result : results){
                String test_name = result.test_name;
                if (!orderedResults.containsKey(test_name)){
                    orderedResults.put(test_name, new HashMap<>());
                }
                orderedResults.get(test_name).put(key, result);
            }
        }
        Set<String> keys = orderedResults.keySet();
        Comparator<String> comparator = new Comparator<String>() {
            @Override
            public int compare(String o1, String o2) {
                String[] s1components = o1.split("\\-");
                String[] s2components = o2.split("\\-");
                Integer o1_1 = Integer.parseInt(s1components[1]), o1_2 = Integer.parseInt(s1components[2].substring(0, 1)),
                        o2_1 = Integer.parseInt(s2components[1]), o2_2 = Integer.parseInt(s2components[2].substring(0, 1));
                if (o1_1 > o2_1) {
                    return 1;
                }
                if (o1_1 > o2_1 && o1_2 > o2_2) {
                    return 1;
                }
                if (o1_1 < o2_1) {
                    return 1;
                }
                if (o1_1 < o2_1 && o1_2 < o2_2) {
                    return 1;
                }
                return 0;
            }
        };
        ArrayList<String> keylist = new ArrayList<String>();
        keylist.addAll(keys);
        Collections.sort(keylist, comparator);

        for (String test : keylist){
            HashMap<String, Result> heuristicResults = orderedResults.get(test);
            String header = test + ", nodes, depth, b*, length, duration (MS), MS/Node Expansion, simple?";
            raw_writer.println(header);
            for (String heuristic : heuristicResults.keySet()){
                Result result = heuristicResults.get(heuristic);
                String line;
                if (result.succeeded) {
                    line = heuristic.split("\\.")[2] + ", " +
                            result.num_nodes + ", " +
                            result.max_depth + ", " +
                            result.branching_factor + ", " +
                            result.num_steps + ", " +
                            result.duration + ", " +
                            (result.duration / result.num_nodes) + ", " +
                            result.wasSimple;
                }
                else {
                    line = heuristic.split("\\.")[2] + ", PRODUCED INVALID SOLUTION";
                }

                raw_writer.println(line);
            }
            raw_writer.println("");
        }
    }

    public String[] getProblemFiles(boolean simple){
        return simple ?
                new String[] {
                        "probBLOCKS-2-0",
                        "probBLOCKS-4-0",
                        "probBLOCKS-4-1",
                        "probBLOCKS-4-2",
                        "probBLOCKS-5-0",
                        "probBLOCKS-5-1",
                        "probBLOCKS-5-2",
                        "probBLOCKS-6-0",
                        "probBLOCKS-6-1",
                        "probBLOCKS-6-2",
                        "probBLOCKS-7-0",
                        "probBLOCKS-7-1",
                        "probBLOCKS-7-2",
                        "probBLOCKS-8-0",
                        "probBLOCKS-8-1",
                        "probBLOCKS-8-2",
                        "probBLOCKS-9-0",
                        "probBLOCKS-9-1",
                        "probBLOCKS-9-2",
                        "probBLOCKS-10-0",
                        "probBLOCKS-10-1",
                        "probBLOCKS-10-2",
                        "probBLOCKS-11-0",
                        "probBLOCKS-11-1",
                        "probBLOCKS-11-2",
                        "probBLOCKS-12-0",
                        "probBLOCKS-12-1",
                        "probBLOCKS-13-0",
                        "probBLOCKS-13-1",
                        "probBLOCKS-14-0",
                        "probBLOCKS-14-1",
                        "probBLOCKS-15-0",
                        "probBLOCKS-15-1",
                        "probBLOCKS-16-1",
                        "probBLOCKS-16-2",
                        "probBLOCKS-17-0"
                } :
                new String[] { // returns a small set, because these each take a year and a half to run (exaggeration), but feel free to add more.
                        "probblocks-17-1",
                        "probblocks-18-0",
                        "probblocks-18-1",
                        "probblocks-19-0",
                        "probblocks-19-1"
                };
    }

    public class Result extends MazeTest.TestResult{
        public double duration;
        public boolean wasSimple;
        String test_name;
        public Result(double nodes, double depth, double branching_factor, double num_steps) {
            super(nodes, depth, branching_factor, num_steps);
        }
    }
}
