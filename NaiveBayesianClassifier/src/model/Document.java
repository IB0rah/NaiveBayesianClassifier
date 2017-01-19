package model;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

public class Document {

    //List of all words  in the document
    public List<String> words = new ArrayList<>();

    public Document(File file){
        List<String> fileWords = new ArrayList<>();
        Scanner in;
		try {
			in = new Scanner(new FileReader(file));
			while (in.hasNext()) fileWords.add(in.next());
			words = DocumentUtils.tokenize(fileWords);
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
    }

    public List<String> getWords() {
        return words;
    }

}