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
		documents.forEach(this::train);
		for(String word: bagOfWords.keySet()) {
			double conditionalProbability = ((double)(bagOfWords.get(word) + SMOOTHING)) /((double)(totalNumberOfWords + SMOOTHING * bc.getVocabularySize()));
			//System.out.println("Added : " + word + " " + conditionalProbability);
			conditionalProbabilities.put(word, conditionalProbability );
		}
	}
	public void train(Document document) {
		document.getWords().forEach(this::putWordTrain);
	}
	
	//Add a word
	public void putWordTrain(String word) {
		totalNumberOfWords++;
		if(bagOfWords.containsKey(word)) {
			bagOfWords.put(word, bagOfWords.get(word) + 1);
		} else {
			bagOfWords.put(word, 1);
		}
//		System.out.println("bag of words get :  " + bagOfWords.get(word) + "total nr : " + totalNumberOfWords + " vocabsize " + bc.getVocabularySize());
	}
	
	public double getDocumentConditionalProbability(Document doc) {
		
		ConProbsString();
		System.out.println(" Hoe dan");
		double score = 1;
		for(String word : doc.getWords()) {
			score =  score + getWordConditionalProbability(word);
			System.out.println("Score : " + score);
		}
		return score;
	}
	
	public double getWordConditionalProbability(String word) {
		double score;
		//System.out.println("Is it in the table?" + conditionalProbabilities.containsKey(word));
		if(conditionalProbabilities.containsKey(word)) {
			score = conditionalProbabilities.get(word);
		} else {
			score = Math.log10(((double)(0 + SMOOTHING)) /((double)(totalNumberOfWords + SMOOTHING * bc.getVocabularySize()))) / Math.log10(2.);
		}
		//System.out.println("Word score : " + score + " : " + word);
		return score;
	}

	public String getName() {
		return name;
	}

	@Override
	public String toString() {
		return "Class: " + name;
	}
	
	@Override
	public int hashCode() {
		int hash = 0;
		for (int i = 0; i < name.length(); i++) {
			hash = hash*31 + name.charAt(i);
		}
		return hash;
	}
	@Override
	public boolean equals(Object c) {
		return this.name.equals(((Class) c).getName());
	}
	
	public String ConProbsString() {
		String result = "";
		for(String word: conditionalProbabilities.keySet()) {
			//System.out.println(word + "prob: " + conditionalProbabilities.get(word));
		}
		return result;
	}
}