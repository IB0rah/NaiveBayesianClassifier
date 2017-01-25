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
	private int totalNumberOfWords = 0;
	private int totalNumberOfDocs = 0;
	private BayesianClassifier bc;
	private String name;
	private int nrOfFeatures = 2;
	Set<String> highestXChis = new HashSet<>();
	
	public Class(String name, BayesianClassifier classifier) {
		this.name = name;
		this.bc = classifier;
	}
	
	public int getTotalNumberOfWords() {
		int result = 0;
		for(String w: bagOfWords.keySet()) {
			result += getWordFreq(w);
		}
		return result;
	}
	
	public String getMax(Set<String> input){ 
		Iterator<String> iterator = input.iterator();
		double maxValue = 0;
		String maxValueString = "";
		if(iterator.hasNext()) {
			String nextWord = iterator.next();
			maxValue = bc.ChiSquaredValue(nextWord);
			maxValueString = nextWord;
		}

		while(iterator.hasNext()){ 
			String nextWord = iterator.next();
			if(bc.ChiSquaredValue(nextWord) > maxValue){ 
	         maxValue = bc.ChiSquaredValue(nextWord);
	         maxValueString = nextWord;
			} 
	    } 
	    return maxValueString; 
	}
	
	public Set<String> chiSquareFeatureSelection(int nrOfFeatures) {
		Set<String> words = new HashSet<String>();
		words.addAll(bagOfWords.keySet());
		Map<String, Double> chisquarevalues = new HashMap<String, Double>();
		for(String word : bagOfWords.keySet()) {
			double chiSquaredValue = bc.ChiSquaredValue(word);
			chisquarevalues.put(word, chiSquaredValue);
		}
		//System.out.println("Chi squared values size : " + chisquarevalues.keySet().size());
//		Set<String> highestXChis = new HashSet<String>();
		if(bagOfWords.keySet().size() <= this.nrOfFeatures) {
			highestXChis.addAll(bagOfWords.keySet());
		} else {
			for (int i = 0; i < nrOfFeatures; i++) {
				String maxWord = chisquarevalues.entrySet().stream().max((entry1, entry2) -> entry1.getValue() > entry2.getValue() ? 1 : -1).get().getKey();
				highestXChis.add(maxWord);
				chisquarevalues.remove(maxWord);
			}
		}
		for(String w : highestXChis) {
			//System.out.println("CONTAINING HTIS WORD : " + w);
		}
		
//		System.out.println("highestXChis size : " + highestXChis.size());
//		System.out.println("bagOfWordsSize : " + bagOfWords.size());
//		bagOfWords.keySet().stream().filter(word -> !highestXChis.contains(word));
//		conditionalProbabilities.keySet().stream().filter(word -> !highestXChis.contains(word));
		
		try {
			PrintWriter out = new PrintWriter("Features" + this.getName() + ".txt");
			for(String s: highestXChis) {
				out.println(s + " \n");
			}
			out.close();
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		}
		Map<String, Map<Document, Integer>> bagOfWordsCopy = new HashMap<String, Map<Document, Integer>>();
		bagOfWordsCopy.putAll(bagOfWords);
		for(String word: bagOfWords.keySet()) {
			if(!highestXChis.contains(word)) {
				bagOfWordsCopy.remove(word);
			}
		}
//		ConProbsString();
		return bagOfWordsCopy.keySet();
	}
	public void train(Set<Document> documents) {
		
		int totalWordCount = 0;
		for(Document d: documents) {
			totalWordCount += d.getWords().size();
		}
		
//		System.out.println("TOTAL WORD SIZE : " + totalWordCount);
		for(Document doc: documents) {
			this.train(doc);
		}

		//chiSquareFeatureSelection(300);
		//System.out.println("BagOfWords size : " + bagOfWords.size() + "conProbs Size : " + conditionalProbabilities.size() + " Total nr of words : " + totalNumberOfWords);
	}
	
	public Map<Document, Integer> getDocumentListWord(String word) {
		Map<Document, Integer> result = null;
		if(bagOfWords.containsKey(word)) {
			result = bagOfWords.get(word);
		}
		return result;
	}
	
	public void updateConditionalProbabilities() {
		Map<String, Map<Document, Integer>> bagOfWordsCopy = new HashMap<String, Map<Document, Integer>>();
		bagOfWordsCopy.putAll(bagOfWords);
		for(String word: bagOfWordsCopy.keySet()) {
			if(!highestXChis.contains(word)) {
				bagOfWords.remove(word);
			}
		}
		for(String word: bagOfWords.keySet()) {
//			System.out.println("THIS CALCULATION FOR THE PROB : " + getWordFreq(word) + " + " + SMOOTHING + " / " + this.getTotalNumberOfWords() + " + " + SMOOTHING + " * " + bc.getVocabularySize());
			double conditionalProbability = Math.log10(((double)(getWordFreq(word) + SMOOTHING)) /((double)(this.getTotalNumberOfWords() + SMOOTHING * bc.getVocabularySize()))) / Math.log10(2.);
//			System.out.println("CLASS : " + this.getName());
//			System.out.println("Added : " + word + " " + conditionalProbability);
			conditionalProbabilities.put(word, conditionalProbability );
		}
		this.notExistingProbability = Math.log10(((double)(0 + SMOOTHING)) /((double)(this.getTotalNumberOfWords() + SMOOTHING * bc.getVocabularySize()))) / Math.log10(2.);
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
		totalNumberOfWords++;
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

	public double getDocumentConditionalProbability(Document doc) {
		
//		ConProbsString();
//		System.out.println(" Hoe dan");
//		System.out.println("Found the following feautres for class:" + this.getName() +   "\n" );
		double score = 0;
		for(String word : doc.getWords()) {
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
			System.out.println(word + "prob: " + conditionalProbabilities.get(word));
		}
		return result;
	}
}