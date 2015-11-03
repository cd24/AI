package robosim.core;

import javafx.scene.paint.Color;

public class Robot extends SimObject {
	private double heading;
	private double vTranslational, vAngular;
	
	public static final double RADIUS = 5.0;
	public static final double FORWARD_VELOCITY = 0.5, ANGULAR_VELOCITY = Math.PI / 4;
	public static final double SONAR_WIDTH = Math.PI / 6;
	
	public Robot(double x, double y, double heading) {
		super(x, y, RADIUS);
		this.heading = heading;
		this.vTranslational = 0;
		this.vAngular = 0;
	}
	
	public Robot() {
		this(0, 0, 0);
	}
	
	@Override
	public Color getColor() {return Color.RED;}
	
	public double getHeading() {return heading;}
	
	public double getTranslationalVelocity() {return vTranslational;}
	
	public double getAngularVelocity() {return vAngular;}
	
	public void stop() {
		vTranslational = 0;
		vAngular = 0;
	}
	
	public void translate(Direction direction) {
		vTranslational = direction.direct(FORWARD_VELOCITY);
		vAngular = 0;
	}
	
	public void turn(Direction direction) {
		vTranslational = 0.0;
		vAngular = direction.direct(ANGULAR_VELOCITY);
	}
	
	public void update() {
		update(Direction.FWD);
	}
	
	public void update(Direction direction) {
		heading += direction.direct(vAngular);
		moveBy(direction.direct(vTranslational * Math.cos(heading)), 
				direction.direct(vTranslational * Math.sin(heading)));
	}
	
	public boolean withinSonar(SimObject other) {
		return Math.abs(this.angularDistance(other) - heading) <= SONAR_WIDTH;
	}
}
