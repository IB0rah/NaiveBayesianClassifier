package model;

import java.util.*;

public class DocumentUtils {
	
	public static List<String> tokenize(Document doc) {
		List<String> result = new ArrayList<String>();
		for(String word: doc.getWords()) {
			String lowercased = word.toLowerCase();
		}
	}
}
