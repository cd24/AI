package search.core;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map.Entry;
import java.util.Random;

public class Histogram<T> implements Iterable<T> {
	private HashMap<T,Double> counts = new HashMap<>();
	private Random rand = new Random();
	public Histogram() {}
	
	public Histogram(Histogram<T> other) {
		this.counts.putAll(other.counts);
		if (this.counts.size() != other.counts.size()) {
			throw new IllegalStateException("Huh? " + this.counts.size() + ": " + other.counts.size());
		}
	}
	
	public void bump(T value) {
		bumpBy(value, 1);
	}
	
	public void bumpBy(T value, double numBumps) {
		counts.put(value, getCountFor(value) + numBumps);
	}
	
	public Double getCountFor(T value) {
		return counts.getOrDefault(value, 0.0);
	}
	
	public Double getTotalCounts() {
		double total = 0;
		for (Entry<T,Double> entry: counts.entrySet()) {
			total += entry.getValue();
		}
		return total;
	}
	
	@Override
	public Iterator<T> iterator() {
		return counts.keySet().iterator();
	}
	
	public T getPluralityWinner() {
		Entry<T, Double> best = null;
		for (Entry<T, Double> element : counts.entrySet()){
			if ( best == null || best.getValue() < element.getValue()){
				best = element;
			}
		}
		return best.getKey();
	}

	public void add(Histogram<T> elements){
		for (T key : elements){
			bumpBy(key, elements.getCountFor(key));
		}
	}

	public double probabilityOf(T element){
		return getCountFor(element)/getTotalCounts();
	}

	public Histogram<T> combine(Histogram<T> other, double combinationFactor){
		Histogram<T> child = new Histogram<>();
		for (T key : other){
			double newCount = (other.getCountFor(key) - getCountFor(key))*combinationFactor + getCountFor(key);
			child.bumpBy(key, newCount);
		}
		return child;
	}

	public double sumExcluded(Histogram<T> other){
		double distance = 0;
		for (T key : other){
			if (!counts.containsKey(key))
				distance += other.getCountFor(key);
		}
		for (T key : counts.keySet()){
			if (!other.counts.containsKey(key))
				distance += getCountFor(key);
		}
		return distance;
	}

	public double distanceTo(Histogram<T> other){
		double distance = 0;
		for (Entry<T, Double> element : counts.entrySet()){
			distance += Math.pow(element.getValue() - other.getCountFor(element.getKey()), 2);
		}
		return distance;
	}

	public double sum(int power){
		double sum = 0;
		for (Entry<T, Double> element : counts.entrySet()){
			sum += Math.pow(element.getValue(), power);
		}
		return sum;
	}
	public double sum(Histogram<T> other, int power){
		double sum = 0;
		for (Entry<T, Double> element : counts.entrySet()){
			sum += Math.pow(element.getValue() * other.getCountFor(element.getKey()), power);
		}
		return sum;
	}

	public double cosineSimilarity(Histogram<T> other){
		double sumAB = sum(other, 1),
				sumA2 = sum(2),
				sumB2 = other.sum(2);
		return sumAB / (sumA2 + sumB2);
	}
}
