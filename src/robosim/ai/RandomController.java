package robosim.ai;

import java.util.Random;

import robosim.core.Action;
import robosim.core.Simulator;

public class RandomController implements Controller {
	private Random random = new Random();
	
	@Override
	public void control(Simulator sim) {
		Action.values()[random.nextInt(Action.values().length)].applyTo(sim);
	}
}
