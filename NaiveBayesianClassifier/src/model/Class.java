package model;

import java.util.*;

public class Class {
	
	//map of all words that belong to this class + their frequencies
	Map<String, Integer> bagOfWords = new HashMap<String, Integer>();
	
	public void train(Document document) {
		for(String word: document.getWords()) {
			putWord(word);
		}
	}
	
	//Add a word
	public void putWord(String word) {
		if(bagOfWords.containsKey(word)) {
			bagOfWords.put(word, bagOfWords.get(word) + 1);
		} else {
			bagOfWords.put(word, 1);
		}
	}
}
