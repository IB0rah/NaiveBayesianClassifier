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
	
	public void train(Map<Class, Set<Document>> trainingsData) {
		this.vocabulary = extractVocabulary(trainingsData);
		this.documentCount = CountNumberOfDocs(trainingsData);
		for(Class c: trainingsData.keySet()) {
			double classPrior = Math.log10(((double)trainingsData.get(c).size()) / ((double)documentCount)) / Math.log10(2.);
			classes.put(c, classPrior);
			c.train(trainingsData.get(c));
		}
		for(Class c: classes.keySet()) {
			c.chiSquareFeatureSelection(300);
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
	
	public Set<String> extractVocabulary(Map<Class, Set<Document>> trainingsData) {
		Set<String> result = new HashSet<String>();
		for(Class c: trainingsData.keySet()) {
			Set<Document> documents = trainingsData.get(c);
			for(Document doc: documents) {
				Set<String> words = doc.getWords();
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
		int TotalNrOfDocsContainingWord = totalAmountOfDocs(word);
		int TotalNrOfDocsNotContainingWords = documentCount - TotalNrOfDocsContainingWord;
		for(Class c: classes.keySet()) {
			double MContaining = 0;
			if(c.getDocumentListWord(word) != null) {
				 MContaining = c.getDocumentListWord(word).keySet().size();
			}
			double MNotContaining = c.getTotalNrOfDocs() - MContaining;
			double ExpContaining = (((double)(TotalNrOfDocsContainingWord * c.getTotalNrOfDocs())) / (double)documentCount); 
			double ExpNotContaining = (((double)(TotalNrOfDocsNotContainingWords * c.getTotalNrOfDocs())) / (double)documentCount ); 
			double ChiSquaredValueContaining = ((double)Math.pow((MContaining - ExpContaining), 2)) / ExpContaining;
			double ChiSquaredValueNotContaining = ((double)Math.pow((MNotContaining - ExpNotContaining), 2)) / ExpNotContaining;
			result = result + ChiSquaredValueContaining + ChiSquaredValueNotContaining;
		}
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
//			System.out.println(c.toString() + " Probability : " + classProbability);
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
	}
}
