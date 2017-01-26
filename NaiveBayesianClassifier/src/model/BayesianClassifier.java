package model;

import java.io.FileNotFoundException;
import java.io.PrintWriter;
import java.util.*;
public class BayesianClassifier {
	
	
	private Set<String> featureVocabulary = new HashSet<String>();
	private Map<Class, Double> classes = new HashMap<Class, Double>();
	public int documentCount = 0;
	//FOR SPAM/HAM: Optimal at ~2100 features (99.3%), FOR BLOGS THIS IS 300 (74%)
	public int nrOfFeatures = 300;
	
	public BayesianClassifier() {
		
	}
	
	public int getfeatureVocabularySize() {
		return featureVocabulary.size();
	}
	
	public Map<Class, Double> getClasses() {
		return classes;
	}
	
	Set<String> selectFeatures(int nrOfFeatures) {
		Map<String, Double> chiSquareValues = new HashMap<String, Double>();
		for(String word: featureVocabulary) {
			
			chiSquareValues.put(word, this.ChiSquaredValue(word));
		}
		
		Set<String> highestXChis = new HashSet<>();
		if(featureVocabulary.size() <= nrOfFeatures) {
			highestXChis.addAll(featureVocabulary);
		} else {
			for (int i = 0; i < nrOfFeatures; i++) {
				String maxWord = chiSquareValues.entrySet().stream().max((entry1, entry2) -> entry1.getValue() > entry2.getValue() ? 1 : -1).get().getKey();
				highestXChis.add(maxWord);
				chiSquareValues.remove(maxWord);
			}
		}
		
		try {
			PrintWriter out = new PrintWriter("SelectedFeatures.txt");
			for(String word : highestXChis) {
				out.write(word + " : " + chiSquareValues.get(word) + "\n");
			}
			out.close();
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return highestXChis;
	}
	public void train(Map<Class, Set<Document>> trainingsData) {
		this.featureVocabulary = extractfeatureVocabulary(trainingsData);
		this.documentCount = CountNumberOfDocs(trainingsData);
		for(Class c: trainingsData.keySet()) {
			double classPrior = Math.log10(((double)trainingsData.get(c).size()) / ((double)documentCount)) / Math.log10(2.);
			classes.put(c, classPrior);
			c.train(trainingsData.get(c));
			System.out.println("CLASS PRIOR : " + c.getName() + " : " + classes.get(c));
		}
		Set<String> selectedFeatures = this.selectFeatures(this.nrOfFeatures);
		this.featureVocabulary = selectedFeatures;
		for(Class c: classes.keySet()) {
			c.updateOnFeatures(selectedFeatures);
		}

		System.out.println("Done training");
	}
	
	public int CountNumberOfDocs(Map<Class, Set<Document>> trainingsData) {
		int result = 0;
		for(Class c: trainingsData.keySet()) {
			int docsCount = trainingsData.get(c).size();
			result += docsCount;
		}
		return result;
	}
	
	public Set<String> extractfeatureVocabulary(Map<Class, Set<Document>> trainingsData) {
		Set<String> result = new HashSet<String>();
		for(Class c: trainingsData.keySet()) {
			Set<Document> documents = trainingsData.get(c);
			for(Document doc: documents) {
				List<String> words = doc.getWords();
				for(String w: words) {
					if(!result.contains(w)) {
						result.add(w);
					}
				}
			}
		}
		
		return result;
	}
	
	public double ChiSquaredValue(String word) {
		double result = 0;
		double[][] chiSquareTable = new double[2 + 1][classes.keySet().size() + 1];
		int i = 0;
		for(Class c: classes.keySet()) {
			
			if(c.getDocumentListWord(word) != null) {
				chiSquareTable[0][i] = c.getDocumentListWord(word).size();
			} else {
				chiSquareTable[0][i] = 0;
			}
			chiSquareTable[1][i] = c.getTotalNrOfDocs() - chiSquareTable[0][i];
			chiSquareTable[2][i] = c.getTotalNrOfDocs();
			i++;
		}
		
		double totalValueRow1 = 0;
		double totalValueRow2 = 0;
		double totalValueRow3 = 0;
		
		for(i = 0; i < classes.keySet().size(); i++) {
			totalValueRow1 += chiSquareTable[0][i];
			totalValueRow2 += chiSquareTable[1][i];
			totalValueRow3 += chiSquareTable[2][i];
		}
		if(totalValueRow1 <= 3) {
			return -1.0;
		}
		chiSquareTable[0][classes.keySet().size()] = totalValueRow1;
		chiSquareTable[1][classes.keySet().size()] = totalValueRow2;
		chiSquareTable[2][classes.keySet().size()] = totalValueRow3;
		
		for(i = 0; i < 3; i++) {
			for(int j = 0; j < classes.keySet().size(); j++) {
				
				double expected = chiSquareTable[i][classes.keySet().size()] * chiSquareTable[2][j] / chiSquareTable[2][classes.keySet().size()]; 
				//System.out.println( "THIS WORD : " + word);
				if(expected == 0) {
					expected = Double.MIN_VALUE;
				}
//				System.out.println("FACTOR :" + (double)(Math.pow(chiSquareTable[i][j] - expected, 2)) /(double)(expected) );
				result += (double)(Math.pow(chiSquareTable[i][j] - expected, 2)) /(double)(expected);
				//System.out.println(result);
			}
		}
		
//		System.out.println("Word : " + word + "Chi2: " + result);
		for(i = 0; i < 3; i++) {
			for(int j = 0; j < classes.keySet().size(); j++) {
				//System.out.println(i + ", " + j + "value : " + chiSquareTable[i][j]);
			}
		}
		
//		System.out.println("word : " + word +  "Chi squared value : " + result);
		return result;
	}
	
	public int totalAmountOfDocs(String word) {
		int result = 0;
		for(Class c: classes.keySet()) {
			if(c.getDocumentListWord(word) != null) {
				result += c.getDocumentListWord(word).keySet().size();
			}
		}
		return result;
	}
	
	public Class classify(Document doc) {
		Class result = null;
		double resultValue = -Double.MAX_VALUE;
		List<String> wordsToEvaluate = new ArrayList<String>();
		for(String w: doc.getWords()) {
			if(featureVocabulary.contains(w)) {
				wordsToEvaluate.add(w);
			}
		}
//		doc.words = wordsToEvaluate;
		for(Class c: classes.keySet()) {
//			System.out.println("prior : " + classes.get(c) + c.getName());
//			System.out.println("Conditional probability: " + c.getDocumentConditionalProbability(doc));
			double classProbability = classes.get(c) + c.getDocumentConditionalProbability(wordsToEvaluate);
//			System.out.println(c.toString() + " " + classProbability );
//			System.out.println(c.toString() + " Probability : " + classProbability);
			if(classProbability > resultValue) {
				resultValue = classProbability;
				result = c;
			}
		}
//		System.out.println("Classified as: " + result.getName());
		return result;
	}
	
	public void train(Document document, Class c) {
		
		document.getWords().stream().filter(word -> !featureVocabulary.contains(word)).forEach(word -> {
			featureVocabulary.add(word);
		});
//		for(Class cl : classes.keySet()) {
//			System.out.println("CLASS prior before : " + classes.get(cl));
//		}
		Set<Document> documentAsList = new HashSet<Document>();
		documentAsList.add(document);
		c.train(documentAsList);
		Set<String> selectedFeatures = this.selectFeatures(this.nrOfFeatures);
		this.featureVocabulary = selectedFeatures;
		for(Class cl: classes.keySet()) {
			cl.updateOnFeatures(selectedFeatures);
		}
		double totalNumberOfDocs = 0;
		for(Class cl : classes.keySet()) {
			totalNumberOfDocs += cl.getTotalNrOfDocs();
		}
		for(Class cl: classes.keySet()) {
			classes.put(cl, Math.log10(((double)cl.getTotalNrOfDocs() / (double)totalNumberOfDocs)) / Math.log10(2.));
		}
//		for(Class cl : classes.keySet()) {
//			System.out.println("CLASS prior after : " + classes.get(cl));
//		}
	}
}
