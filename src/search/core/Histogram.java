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
		T highest = null;
		int highRep = -1;
		for (T key : counts.keySet()){
			int count = getCountFor(key);
			if (highRep < count){
				highest = key;
				highRep = count;
			}
		}
		return highest;
	}
}
