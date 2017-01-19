package controller;

import model.BayesianClassifier;
import model.Class;
import model.Document;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class Controller {

    BayesianClassifier baysianClassifier;
    Map<Class, List<Document>> classAndDocs;
    private boolean isTrained;

    public Controller() {
        baysianClassifier = new BayesianClassifier();
        classAndDocs = new HashMap<>();
        isTrained = false;
    }

    public void addClassWithDocs(Class aClass, List<Document> documents) {
        if (classAndDocs.size() > 0) System.out.println(aClass.equals(classAndDocs.keySet().toArray()[0]));
        if (classAndDocs.containsKey(aClass)) {
            List<Document> docs = classAndDocs.get(aClass);
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

    public void classify() {
        //TODO
    }
}