package planner.tests;

import org.junit.Before;
import org.junit.Test;
import planner.core.Domain;
import planner.core.Plan;
import planner.core.PlanGraph;
import planner.core.Problem;

import java.io.File;

import static org.junit.Assert.assertTrue;

public class PlanGraphTest {
	static String path2String(String... pathStuff) {
		String result = "";
		for (String s: pathStuff) {
			result += s + File.separatorChar;
		}
		return result.substring(0, result.length() - 1);
	}
	
	Domain d;
	
	@Before
	public void setup() {
		d = new Domain(path2String("domains", "blocks", "domain.pddl"));
	}
	
	public void test(String... path) {
		Problem p = new Problem(path2String(path));
		PlanGraph pg = new PlanGraph(d, p.getStartState(), p);
		Plan noDelete = pg.extractNoDeletePlan();
		assertTrue(noDelete.length() > 0);
		assertTrue(noDelete.isPlanValid(p));
	}

	@Test
	public void test1() {
		test("domains", "blocks", "probBLOCKS-4-0.pddl");
	}


	@Test
	public void test2() {
		test("domains", "blocks", "probBLOCKS-4-1.pddl");
	}

	@Test
	public void test3() {
		test("domains", "blocks", "probBLOCKS-4-2.pddl");
	}
}
