package model;

import java.io.FileNotFoundException;
import java.io.PrintWriter;
import java.util.*;

public class Class {
	public static final double SMOOTHING = 1;
	//map of all words that belong to this class + their frequencies
	Map<String, Map<Document, Integer>> bagOfWords = new HashMap<String, Map<Document, Integer>>();
	Map<String, Double> conditionalProbabilities = new HashMap<String, Double>();
	private double notExistingProbability;
	private int totalNumberOfDocs = 0;
	private BayesianClassifier bc;
	private String name;
	
	public Class(String name, BayesianClassifier classifier) {
		this.name = name;
		this.bc = classifier;
	}
	
	public int getTotalNumberOfFeatures() {
		int result = 0;
		for(String w: conditionalProbabilities.keySet()) {
			result += getWordFreq(w);
		}
		return result;
	}
	
	public void updateOnFeatures(Set<String> features) {
		conditionalProbabilities.clear();
		for(String word: bagOfWords.keySet()) {
			if(features.contains(word)) {
				conditionalProbabilities.put(word, 0.0);
			}
		}
		this.updateConditionalProbabilities();
	}
	
	public void train(Set<Document> docs) {
		for(Document doc: docs) {
			this.train(doc);
		}
	}

	
	public Map<Document, Integer> getDocumentListWord(String word) {
		Map<Document, Integer> result = null;
		if(bagOfWords.containsKey(word)) {
			result = bagOfWords.get(word);
		}
		return result;
	}
	
	public void updateConditionalProbabilities() {
		for(String word: conditionalProbabilities.keySet()) {
//		System.out.println("THIS CALCULATION FOR THE PROB : " + getWordFreq(word) + " + " + SMOOTHING + " / " + this.getTotalNumberOfFeatures() + " + " + SMOOTHING + " * " + bc.getfeatureVocabularySize());
			double conditionalProbability = Math.log10(((double)(getWordFreq(word) + SMOOTHING)) /((double)(this.getTotalNumberOfFeatures() + SMOOTHING * bc.getfeatureVocabularySize()))) / Math.log10(2.);
//			System.out.println("CLASS : " + this.getName());
//			System.out.println("Added : " + word + " " + conditionalProbability);
			conditionalProbabilities.put(word, conditionalProbability );
		}
		this.notExistingProbability = Math.log10(((double)(0 + SMOOTHING)) /((double)(this.getTotalNumberOfFeatures() + SMOOTHING * bc.getfeatureVocabularySize()))) / Math.log10(2.);
	}
	
	public int getTotalNrOfDocs() {
		return totalNumberOfDocs;
	}
	

	public void train(Document document) {
		
		totalNumberOfDocs++;
		for(String word: document.getWords()) {
			this.putWordTrain(word, document);
		}
		
	}
	
	public Map<String, Double> getConProbs() {
		return this.conditionalProbabilities;
	}
	
	public int getWordFreq(String word) {
		int result = 0;
		Map<Document, Integer> documentsList = bagOfWords.get(word);
		for(Document d : documentsList.keySet()) {
			result += bagOfWords.get(word).get(d);
		}
		return result;
	}
	
	//Add a word
	public void putWordTrain(String word, Document document) {
		if(bagOfWords.containsKey(word) && !bagOfWords.get(word).containsKey(document)) {
			bagOfWords.get(word).put(document, 1);
		} else if(bagOfWords.containsKey(word) && bagOfWords.get(word).containsKey(document)) {
			bagOfWords.get(word).put(document, bagOfWords.get(word).get(document) + 1);
		} else if(!bagOfWords.containsKey(word)) {
			Map<Document, Integer> docMap = new HashMap<Document, Integer>();
			docMap.put(document, 1);
			bagOfWords.put(word, docMap);
		}
//		System.out.println("bag of words get :  " + bagOfWords.get(word) + "total nr : " + totalNumberOfWords + " vocabsize " + bc.getVocabularySize());
//		System.out.println("BAGOFWORDSSIZE OP DEZE PUNT " + bagOfWords.size());
	}

	public double getDocumentConditionalProbability(List<String> docwords) {
		
//		ConProbsString();
//		System.out.println(" Hoe dan");
//		System.out.println("Found the following feautres for class:" + this.getName() +   "\n" );
		double score = 0;
		for(String word : docwords) {
			score =  score + getWordConditionalProbability(word);
			//System.out.println("Score : " + score);
		}
		return score;
	}
	
	public double getWordConditionalProbability(String word) {
		double score;
		//System.out.println("Is it in the table?" + conditionalProbabilities.containsKey(word));
		//System.out.println("DIT IS DE GROOTTE VAN MIJ CONDITIONAL PROBABILITY TABLE : " + conditionalProbabilities.size() );
		if(conditionalProbabilities.containsKey(word)) {
			score = conditionalProbabilities.get(word);
//			System.out.println(word + "\n" + score );
		} else {
//			System.out.println("TOTAAL AANTAL WOORDEN : " + this.getTotalNumberOfWords());
			score = this.notExistingProbability;
//			score = 0;
//			System.out.println("VOOR " + this.getName() + " IS DIT DE SCORE ALS IE NIET VOORKOMT : " + score);
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
		int hash = 12;
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
//			System.out.println(word + "prob: " + conditionalProbabilities.get(word));
		}
		return result;
	}
}