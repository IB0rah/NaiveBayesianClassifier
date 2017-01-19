package model;

import java.io.File;
import java.util.*;

public class Document {
		
	//List of all words  in the document
	public List<String> words = new ArrayList<String>();

	public Document(File file) {
	}

	public List<String> getWords() {
		return words;
	}
	
}
