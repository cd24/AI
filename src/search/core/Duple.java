package search.core;

public class Duple<X,Y> {
	private X x;
	private Y y;
	
	public Duple(X x, Y y) {
		this.x = x;
		this.y = y;
	}
	
	public X getFirst() {return x;}
	public Y getSecond() {return y;}
}
