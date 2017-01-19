package model;

import java.util.ArrayList;
import java.util.List;

public class DocumentUtils {
    private static List<String> stopwords = new ArrayList<String>();

    public static List<String> tokenize(List<String> strings) {
        List<String> result = new ArrayList<>();
        for (String word : strings) {
            String lowercased = word.toLowerCase();
            if (!stopwords.contains(lowercased)) {
                result.add(lowercased);
            }
        }

        return result;
    }
}
