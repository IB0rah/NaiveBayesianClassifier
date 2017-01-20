package model;

import java.util.*;
public class BayesianClassifier {
	
	private List<String> vocabulary = new ArrayList<String>();
	private Map<Class, Double> classes = new HashMap<Class, Double>();
	private int documentCount = 0;
	
	public BayesianClassifier() {
		
	}
	
	public int getVocabularySize() {
		return vocabulary.size();
	}
	
	public void train(Map<Class, List<Document>> trainingsData) {
		this.vocabulary = extractVocabulary(trainingsData);
		this.documentCount = CountNumberOfDocs(trainingsData);
		for(Class c: trainingsData.keySet()) {
			double classPrior = Math.log10(((double)trainingsData.get(c).size()) / ((double)documentCount)) / Math.log10(2.);
			classes.put(c, classPrior);
			c.train(trainingsData.get(c));
		}
		System.out.println("Done training");
	}
	
	public int CountNumberOfDocs(Map<Class, List<Document>> trainingsData) {
		int result = 0;
		for(Class c: trainingsData.keySet()) {
			int docsCount = trainingsData.get(c).size();
			result += docsCount;
		}
		return result;
	}
	
	public List<String> extractVocabulary(Map<Class, List<Document>> trainingsData) {
		List<String> result = new ArrayList<String>();
		for(Class c: trainingsData.keySet()) {
			List<Document> documents = trainingsData.get(c);
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
	
	public Class classify(Document doc) {
		Class result = null;
		double resultValue = -Double.MAX_VALUE;
		for(Class c: classes.keySet()) {
			System.out.println("prior : " + classes.get(c) + c.getName());
			System.out.println("Conditional probability: " + c.getDocumentConditionalProbability(doc));
			double classProbability = classes.get(c) + c.getDocumentConditionalProbability(doc);
//			System.out.println(c.toString() + " " + classProbability );
			System.out.println(c.toString() + " Probability : " + classProbability);
			if(classProbability >= resultValue) {
				resultValue = classProbability;
				result = c;
			}
		}
		System.out.println("Classified as: " + result.getName());
		return result;
	}
	
	public void train(Document document, Class c) {
		double oldClassPrior = classes.get(c);
		double oldNrOfDocumentsClass = oldClassPrior * documentCount;
		documentCount++;
		document.getWords().stream().filter(word -> !vocabulary.contains(word)).forEach(word -> {
			vocabulary.add(word);
		});
		classes.put(c, oldNrOfDocumentsClass + 1 / documentCount);
		c.train(document);
		
	}
}
