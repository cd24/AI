package text.core;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.Iterator;

import search.core.Duple;
import search.core.Funcs;
import search.core.Histogram;

public class LabeledWords implements Iterable<String> {
	private ArrayList<Duple<String,Sentence>> sentences = new ArrayList<>();
	private Histogram<String> countsFor = new Histogram<>();
	
	public LabeledWords() {}
	
	public static LabeledWords fromSentiment(File src) throws FileNotFoundException {
		LabeledWords set = new LabeledWords();
		String text = Funcs.fromFile(src);
		for (String line: text.split("\\n")) {
			String[] info = line.split("\\t");
			if (info.length >= 2 && info[1].length() > 0) {
				String lbl = info[1].charAt(0) == '0' ? "NEGATIVE" : "POSITIVE";
				set.add(new Sentence(info[0]), lbl);
			}
		}
		return set;
	}
	
	public ArrayList<Sentence> allWith(String lbl) {
		ArrayList<Sentence> result = new ArrayList<>();
		for (Duple<String,Sentence> dup: sentences) {
			if (dup.getFirst().equals(lbl)) {
				result.add(dup.getSecond());
			}
		}
		return result;
	}
	
	public void add(Sentence words, String lbl) {
		sentences.add(new Duple<>(lbl, words));
		countsFor.bump(lbl);
	}
	
	public String getLabel(int i) {return sentences.get(i).getFirst();}
	
	public Sentence getWords(int i) {return sentences.get(i).getSecond();}
	
	public int size() {return sentences.size();}
	
	public double numWith(String lbl) {
		return countsFor.getCountFor(lbl);
	}
	
	public Histogram<String> allCounts() {return new Histogram<>(countsFor);}

	public Duple<LabeledWords,LabeledWords> split() {
		LabeledWords even = new LabeledWords();
		LabeledWords odd = new LabeledWords();
		for (String lbl: countsFor) {
			ArrayList<Sentence> sentences = allWith(lbl);
			for (int i = 0; i < sentences.size(); i++) {
				LabeledWords target = i % 2 == 0 ? even : odd;
				target.add(sentences.get(i), lbl);
			}
		}
		return new Duple<>(even, odd);
	}

	@Override
	public Iterator<String> iterator() {
		return countsFor.iterator();
	}
}
