package search.core;

import java.util.HashMap;
import java.util.Map.Entry;

public class Histogram<T> {
	private HashMap<T,Integer> counts = new HashMap<>();
	
	public void bump(T value) {
		counts.put(value, getCountFor(value) + 1);
	}
	
	public int getCountFor(T value) {
		return counts.getOrDefault(value, 0);
	}
	
	public int getTotalCounts() {
		int total = 0;
		for (Entry<T,Integer> entry: counts.entrySet()) {
			total += entry.getValue();
		}
		return total;
	}
	
	public T getPluralityWinner() {
		// TODO: Return the key with the highest count.
		return null;
	}
}
