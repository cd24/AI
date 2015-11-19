package text.core;

import java.util.HashSet;
import java.util.Iterator;

import search.core.Histogram;

public class Sentence implements Iterable<String> {
	private String[] sentence;
	public final static boolean PURGE_STOP_WORDS = true;
	
	public final static HashSet<String> bannedWords = new HashSet<>();
	static {
		// From http://www.textfixer.com/resources/common-english-words.txt
		for (String bad: "a,able,about,across,after,all,almost,also,am,among,an,and,any,are,as,at,be,because,been,but,by,can,cannot,could,dear,did,do,does,either,else,ever,every,for,from,get,got,had,has,have,he,her,hers,him,his,how,however,i,if,in,into,is,it,its,just,least,let,like,likely,may,me,might,most,must,my,neither,no,nor,not,of,off,often,on,only,or,other,our,own,rather,said,say,says,she,should,since,so,some,than,that,the,their,them,then,there,these,they,this,tis,to,too,twas,us,wants,was,we,were,what,when,where,which,while,who,whom,why,will,with,would,yet,you,your".split(",")) {
			bannedWords.add(bad);
		}
	}
	
	public Sentence(String src) {
		sentence = src.split("\\s+");
	}
	
	public Histogram<String> wordCounts() {
		Histogram<String> result = new Histogram<String>();
		for (String word: sentence) {
			if (!(PURGE_STOP_WORDS && bannedWords.contains(word))) result.bump(word);
		}
		return result;
	}
	
	public int size() {return sentence.length;}

	@Override
	public Iterator<String> iterator() {
		return new Iterator<String>(){
			private int i = 0;

			@Override
			public boolean hasNext() {
				return i < sentence.length;
			}

			@Override
			public String next() {
				return sentence[i++];
			}};
	}
}
