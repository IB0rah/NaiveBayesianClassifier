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

    public Document(File file) throws FileNotFoundException {
        List<String> fileWords = new ArrayList<>();
        Scanner in = new Scanner(new FileReader(file));
        while (in.hasNext()) fileWords.add(in.next());
        words = DocumentUtils.tokenize(fileWords);
    }

    public List<String> getWords() {
        return words;
    }

}
