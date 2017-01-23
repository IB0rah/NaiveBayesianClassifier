package controller;

import model.BayesianClassifier;
import model.Class;
import model.Document;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

public class Controller {

    BayesianClassifier baysianClassifier;
    Map<Class, Set<Document>> classAndDocs;
    private boolean isTrained;

    public Controller() {
        baysianClassifier = new BayesianClassifier();
        classAndDocs = new HashMap<>();
        isTrained = false;
    }

    public void addClassWithDocs(Class aClass, Set<Document> documents) {
        if (classAndDocs.containsKey(aClass)) {
            Set<Document> docs = classAndDocs.get(aClass);
            docs.addAll(documents);
            classAndDocs.put(aClass, docs);
        } else {
            classAndDocs.put(aClass, documents);
        }
        System.out.println("classAndDocs = " + classAndDocs.toString());
    }

    public void train() {
        isTrained = true;
        baysianClassifier.train(classAndDocs);
    }

    public boolean canClassify() {
        return isTrained;
    }

    public boolean canTrain() {
        return classAndDocs.keySet().size() > 0;
    }

    public BayesianClassifier getBaysianClassifier() {
        return baysianClassifier;
    }


    public Class classify(Document doc) {
        return baysianClassifier.classify(doc);
    }

    public Map<Class, Set<Document>> getClassAndDocs() {
        return classAndDocs;
    }
}