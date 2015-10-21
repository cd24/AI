package search.core;

import java.util.HashMap;

public class Histogram<T> {
	private HashMap<T,Integer> counts = new HashMap<>();
	
	public void bump(T value) {
		counts.put(value, getCountFor(value) + 1);
	}
	
	public int getCountFor(T value) {
		return counts.getOrDefault(value, 0);
	}
	
	public T getPluralityWinner() {
		// TODO: Return the key with the highest count.
		return null;
	}
}
