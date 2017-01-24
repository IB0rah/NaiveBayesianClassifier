package model;

import java.io.FileNotFoundException;
import java.io.PrintWriter;
import java.util.*;

public class Class {
	public static final double SMOOTHING = 1;
	//map of all words that belong to this class + their frequencies
	Map<String, Map<Document, Integer>> bagOfWords = new HashMap<String, Map<Document, Integer>>();
	Map<String, Double> conditionalProbabilities = new HashMap<String, Double>();
	private int totalNumberOfWords = 0;
	private int totalNumberOfDocs = 0;
	private BayesianClassifier bc;
	private String name;
	private int nrOfFeatures = 1000;
	
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
	public Set<String> chiSquareFeatureSelection(int nrOfFeatures) {
		Map<String, Double> chisquarevalues = new HashMap<String, Double>();
		for(String word : bagOfWords.keySet()) {
			double chiSquaredValue = bc.ChiSquaredValue(word);
			chisquarevalues.put(word, chiSquaredValue);
		}
		//System.out.println("Chi squared values size : " + chisquarevalues.keySet().size());
		Set<String> highestXChis = new HashSet<String>();
		if(chisquarevalues.size() <= this.nrOfFeatures) {
			highestXChis.addAll(chisquarevalues.keySet());
		} else {
			for (int i = 0; i < nrOfFeatures; i++) {
				String maxWord = chisquarevalues.entrySet().stream().max((entry1, entry2) -> entry1.getValue() > entry2.getValue() ? 1 : -1).get().getKey();
				highestXChis.add(maxWord);
				chisquarevalues.remove(maxWord);
			}
		}
		
		System.out.println("highestXChis size : " + highestXChis.size());
		System.out.println("bagOfWordsSize : " + bagOfWords.size());
		Map<String, Map<Document, Integer>> bagOfWordsCopy = new HashMap<String, Map<Document, Integer>>();
		bagOfWordsCopy.putAll(bagOfWords);
		for(String word: bagOfWordsCopy.keySet()) {
			if(!highestXChis.contains(word)) {
				bagOfWords.remove(word);
				conditionalProbabilities.remove(word);
			}
			
		}
		
		try {
			PrintWriter out = new PrintWriter("Features" + this.getName() + ".txt");
			for(String s: conditionalProbabilities.keySet()) {
				out.println(s + " \n");
			}
			out.close();
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		}
		ConProbsString();
		return bagOfWords.keySet();
	}
	public void train(Set<Document> documents) {
		
		int totalWordCount = 0;
		for(Document d: documents) {
			totalWordCount += d.getWords().size();
		}
		
//		System.out.println("TOTAL WORD SIZE : " + totalWordCount);
		documents.forEach(this::train);

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
		for(String word: bagOfWords.keySet()) {
		double conditionalProbability = Math.log10(((double)(getWordFreq(word) + SMOOTHING)) /((double)(this.getTotalNumberOfWords() + SMOOTHING * bc.getVocabularySize()))) / Math.log10(2.);
		System.out.println("CLASS : " + this.getName());
		System.out.println("Added : " + word + " " + conditionalProbability);
		conditionalProbabilities.put(word, conditionalProbability );
	}
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
			score = Math.log10(((double)(0 + SMOOTHING)) /((double)(this.getTotalNumberOfWords() + SMOOTHING * bc.getVocabularySize()))) / Math.log10(2.);
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