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
		List<String> vocabulary = extractVocabulary(trainingsData);
		int docsCount = CountNumberOfDocs(trainingsData);
		for(Class c: trainingsData.keySet()) {
			double classPrior = trainingsData.get(c).size() / docsCount;
			classes.put(c, classPrior);
			c.train(trainingsData.get(c));
		}
		
		this.vocabulary = vocabulary;
		this.documentCount = docsCount;
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
		double resultValue = 0;
		for(Class c: classes.keySet()) {
			double classProbability = Math.log10(classes.get(c) + c.getDocumentConditionalProbability(doc)) / Math.log10(2.);
			if(classProbability >= resultValue) {
				resultValue = classProbability;
				result = c;
			}
		}
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
