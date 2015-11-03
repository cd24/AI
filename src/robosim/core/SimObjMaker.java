package robosim.core;

import robosim.gui.SimController;

public enum SimObjMaker {
	OB {
		@Override
		public SimObject makeAt(double x, double y) {
			return new SimObject(x, y, SimController.RADIUS);
		}
	}, RBT {
		@Override
		public SimObject makeAt(double x, double y) {
			return new Robot(x, y, 0);
		}
	};
	
	abstract public SimObject makeAt(double x, double y);
}
