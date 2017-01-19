package model;

import java.util.*;

public class Class {
	public static final int SMOOTHING = 1;
	//map of all words that belong to this class + their frequencies
	Map<String, Integer> bagOfWords = new HashMap<String, Integer>();
	Map<String, Double> conditionalProbabilities = new HashMap<String, Double>();
	private int totalNumberOfWords = 0;
	private BayesianClassifier bc;
	private String name;
	
	public Class(String name, BayesianClassifier classifier) {
		this.name = name;
		this.bc = classifier;
	}
	
	public void train(List<Document> documents) {
		for(Document doc: documents) {
			this.train(doc);
		}
	}
	public void train(Document document) {
		for(String word: document.getWords()) {
			putWord(word);
		}
	}
	
	//Add a word
	public void putWord(String word) {
		totalNumberOfWords++;
		if(bagOfWords.containsKey(word)) {
			bagOfWords.put(word, bagOfWords.get(word) + 1);
		} else {
			bagOfWords.put(word, 1);
		}
		double conditionalProbability = (bagOfWords.get(word) + SMOOTHING) /(totalNumberOfWords + SMOOTHING * bc.getVocabularySize());   
		conditionalProbabilities.put(word, conditionalProbability );
	}
	
	public double getDocumentConditionalProbability(Document doc) {
		double score = 0;
		for(String word : doc.getWords()) {
			score =  score * getWordConditionalProbability(word);
		}
		return score;
	}
	
	public double getWordConditionalProbability(String word) {
		double score;
		if(conditionalProbabilities.containsKey(word)) {
			score = (bagOfWords.get(word) + SMOOTHING) /(totalNumberOfWords + SMOOTHING * bc.getVocabularySize());
		} else {
			score = (0 + SMOOTHING) /(totalNumberOfWords + SMOOTHING * bc.getVocabularySize());
		}
		return score;
	}
	
	@Override
	public String toString() {
		return name;
	}
	
	@Override
	public int hashCode() {
		return Integer.parseInt(name);
		
	}
	@Override
	public boolean equals(Object c) {
		return this.name.equals(c.toString());
	}
}
