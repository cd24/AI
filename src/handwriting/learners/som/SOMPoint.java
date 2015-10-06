package handwriting.learners.som;

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
		return new SOMPoint[]{
				new SOMPoint(x + 1, y),
				new SOMPoint(x + 1, y + 1),
				new SOMPoint(x + 1, y - 1),
				new SOMPoint(x - 1, y),
				new SOMPoint(x - 1, y + 1),
				new SOMPoint(x - 1, y - 1),
				new SOMPoint(x, y + 1),
				new SOMPoint(x, y - 1)
		};
	}
}
