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

	public SOMPoint[] getNeighbors(double radius){
		ArrayList<SOMPoint> points = new ArrayList<>();
		for (int i = 0; i < radius; ++i){
			for (int k = 0; k < radius; ++k){
				if (Math.sqrt((i*i) + (k*k)) < radius)
					points.add(new SOMPoint(i, k));
			}
		}
		SOMPoint[] pts = new SOMPoint[points.size()];
		return points.toArray(pts);
	}
}
