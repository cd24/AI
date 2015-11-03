package robosim.core;

import static org.junit.Assert.*;

import org.junit.Test;

public class SimulatorTest {

	@Test
	public void test() {
		Simulator sim = new Simulator(10, 20);
		SimObject s1 = new SimObject(1, 2, 2);
		sim.add(s1);
		SimObject s2 = new SimObject(10, 10, 2);
		sim.add(s2);
		
		Robot bot = new Robot(5, 10, 0);
		sim.add(bot);
		
		assertTrue(bot.withinSonar(s2));
		assertFalse(bot.withinSonar(s1));
		
		assertEquals(5.0, sim.findClosestEdge(), 0.01);
		bot.turn(Direction.FWD);
		for (int i = 0; i < 8; i++) bot.update();
		assertEquals(10.0, sim.findClosestEdge(), 0.01);
		for (int i = 0; i < 8; i++) bot.update();
		assertEquals(5.0, sim.findClosestEdge(), 0.01);
		for (int i = 0; i < 8; i++) bot.update();
		assertEquals(10.0, sim.findClosestEdge(), 0.01);
	}

}
