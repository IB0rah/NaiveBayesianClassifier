package model;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;
import java.util.*;

public class Document {

    //List of all words  in the document
    public Set<String> words = new HashSet<>();

    public Document(Set<String> words) {
        this.words = words;
    }

    public Document(File file) {
        Set<String> fileWords = new HashSet<>();
        Scanner in = null;
        try {
            in = new Scanner(new FileReader(file));
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
        while (in.hasNext()) fileWords.add(in.next());
        words = DocumentUtils.tokenize(fileWords);
    }

    public Set<String> getWords() {
        return words;
    }

}
