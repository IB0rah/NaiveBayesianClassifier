package model;

import java.util.*;

public class DocumentUtils {
	
	private static List<String> stopwords = new ArrayList<String>();
	
	public static List<String> tokenize(Document doc) {
		List<String> result = new ArrayList<String>();
		for(String word: doc.getWords()) {
			String lowercased = word.toLowerCase();
			if(!stopwords.contains(lowercased)) {
				result.add(lowercased);
			}
		}
		
		return result;
	}
}
