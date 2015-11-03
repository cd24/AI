package robosim.core;

import javafx.scene.paint.Color;

public class SimObject {
	private double x, y, radius;
	
	public SimObject(double x, double y, double radius) {
		this.x = x;
		this.y = y;
		this.radius = radius;
	}
	
	public double getRadius() {return radius;}
	
	public double getX() {return x;}
	public double getY() {return y;}
	
	public Color getColor() {return Color.GREEN;}
	
	public double distanceTo(SimObject other) {
		return Math.sqrt(Math.pow(this.x - other.x, 2) + Math.pow(this.y - other.y, 2));
	}
	
	public double angularDistance(SimObject other) {
		return Math.atan2(other.y - this.y, other.x - this.x);
	}
	
	public boolean isHitting(SimObject other) {
		return this.radius + other.radius >= this.distanceTo(other);
	}
	
	public void moveBy(double dx, double dy) {
		x += dx;
		y += dy;
	}
}
