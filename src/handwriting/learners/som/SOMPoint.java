package handwriting.learners.som;

import java.util.ArrayList;

public class SOMPoint {
	private int x, y;
	
	public SOMPoint(int x, int y) {
		this.x = x;
		this.y = y;
	}
	
	public int x() {return x;}
	public int y() {return y;}
	
	public int hashCode() {return x * 10000 + y;}
	public String toString() {return String.format("(%d,%d)", x, y);}
	public boolean equals(Object other) {
		if (other instanceof SOMPoint) {
			SOMPoint that = (SOMPoint)other;
			return this.x == that.x && this.y == that.y;
		} else {
			return false;
		}
	}

	public double distanceTo(int x, int y) {
		return Math.sqrt(Math.pow(x - this.x, 2) + Math.pow(y - this.y, 2));
	}

	public SOMPoint[] getNeighbors(double radius){
		ArrayList<SOMPoint> points = new ArrayList<>();
		for (int i = 0; i < radius; ++i){
			for (int k = 0; k < radius; ++k){
				SOMPoint point = new SOMPoint(i, k);
				if (distanceTo(i, k) <= radius && !this.equals(point))
					points.add(point);
			}
		}
		SOMPoint[] pts = new SOMPoint[points.size()];
		return points.toArray(pts);
	}
}
