package model;

import java.util.*;
public class BayesianClassifier {
	
	private Set<String> vocabulary = new HashSet<String>();
	private Map<Class, Double> classes = new HashMap<Class, Double>();
	public int documentCount = 0;
	
	public BayesianClassifier() {
		
	}
	
	public int getVocabularySize() {
		return vocabulary.size();
	}
	
	public Map<Class, Double> getClasses() {
		return classes;
	}
	
	public void train(Map<Class, Set<Document>> trainingsData) {
		//this.vocabulary = extractVocabulary(trainingsData);
		this.documentCount = CountNumberOfDocs(trainingsData);
		for(Class c: trainingsData.keySet()) {
			double classPrior = Math.log10(((double)trainingsData.get(c).size()) / ((double)documentCount)) / Math.log10(2.);
			classes.put(c, classPrior);
			c.train(trainingsData.get(c));
			System.out.println("CLASS PRIOR : " + c.getName() + " : " + classes.get(c));
		}
//		for(Class c : classes.keySet()) {
//			System.out.println(c.getName() + " : " + c.getConProbs().keySet().size() + " \n");
//		}
		for(Class c: classes.keySet()) {
			this.vocabulary.addAll(c.chiSquareFeatureSelection(1000));
//			System.out.println("VOCAB SIZE : " + vocabulary.size());
			for(String w : vocabulary) {
//				System.out.println("WORD : " + w);
			}
		}
		for(Class c: classes.keySet()) {
			c.updateConditionalProbabilities();
		}
//		for(Class c : classes.keySet()) {
//			System.out.println(c.getName() + " : " + c.getConProbs().keySet().size());
//		}
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
	
	public Set<String> extractVocabulary(Map<Class, Set<Document>> trainingsData) {
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
		chiSquareTable[0][classes.keySet().size()] = totalValueRow1;
		chiSquareTable[1][classes.keySet().size()] = totalValueRow2;
		chiSquareTable[2][classes.keySet().size()] = totalValueRow3;
		
		for(i = 0; i < 3; i++) {
			for(int j = 0; j < classes.keySet().size(); j++) {
				//System.out.println(" DOING THIS CALCULATION : " + chiSquareTable[i][classes.keySet().size()] + " * " + chiSquareTable[2][j] + " / " + chiSquareTable[2][classes.keySet().size()] + "\n");
				double expected = chiSquareTable[i][classes.keySet().size()] * chiSquareTable[2][j] / chiSquareTable[2][classes.keySet().size()]; 
				//System.out.println("EXPECTED : " + expected);
				result += Math.pow(chiSquareTable[i][j] - expected, 2) / expected;
				//System.out.println(result);
			}
		}
		
//		System.out.println("Word : " + word + " \n" + "Chi2: " + result);
//		for(i = 0; i < 3; i++) {
//			for(int j = 0; j < classes.keySet().size(); j++) {
//				System.out.println(i + ", " + j + "value : " + chiSquareTable[i][j]);
//			}
//		}
		
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
		for(Class c: classes.keySet()) {
//			System.out.println("prior : " + classes.get(c) + c.getName());
//			System.out.println("Conditional probability: " + c.getDocumentConditionalProbability(doc));
			double classProbability = classes.get(c) + c.getDocumentConditionalProbability(doc);
//			System.out.println(c.toString() + " " + classProbability );
			//System.out.println(c.toString() + " Probability : " + classProbability);
			if(classProbability >= resultValue) {
				resultValue = classProbability;
				result = c;
			}
		}
		System.out.println("Classified as: " + result.getName());
		return result;
	}
	
	public void train(Document document, Class c) {
		double oldDocCount = documentCount;
		documentCount++;
		for(Class cl: classes.keySet()) {
			if(cl.equals(c)) {
				classes.put(c, ((double)((classes.get(c) + 1) * oldDocCount)) / ((double)documentCount));
			} else {
				classes.put(c, ((double)(classes.get(c) * oldDocCount)/ (double)documentCount));
			}
		}
		document.getWords().stream().filter(word -> !vocabulary.contains(word)).forEach(word -> {
			vocabulary.add(word);
		});
		Set<Document> documentAsList = new HashSet<Document>();
		documentAsList.add(document);
		c.train(documentAsList);
//		for(Class cl: classes.keySet()) {
//			cl.chiSquareFeatureSelection(300);
//		}
	}
}
