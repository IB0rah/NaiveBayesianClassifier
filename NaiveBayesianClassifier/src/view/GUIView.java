package view;

import controller.Controller;
import javafx.application.Application;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.TextField;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.VBox;
import javafx.scene.text.Text;
import javafx.stage.FileChooser;
import javafx.stage.Stage;
import model.Class;
import model.Document;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public class GUIView extends Application {

    private static final String STANDARDTEXT = "class name";
    private static final String CLASSLISTSTART = "Classes in the system:";
    private Controller controller;
    private FileChooser fileChooser;
    private List<Document> inputDocuments;
    private VBox right;
    private TextField className;
    private Text classList;
    private Button fileInput;
    private Button addInput;
    private Button train;
    private Button classify;
    private Text result;

    @Override
    public void start(Stage primaryStage) throws Exception {
        controller = new Controller();
        fileChooser = new FileChooser();
        inputDocuments = new ArrayList<>();
        BorderPane pane = new BorderPane();
        VBox left = new VBox();
        right = new VBox();
        classList = new Text();
        className = new TextField();
        fileInput = new Button();
        addInput = new Button();
        train = new Button();
        classify = new Button();
        result = new Text();

        fileInput.setText("select");
        addInput.setText("add");
        train.setText("train");
        classify.setText("classify");

        pane.setLeft(left);
        pane.setRight(right);

        left.getChildren().add(classList);
        right.getChildren().add(className);
        right.getChildren().add(fileInput);
        right.getChildren().add(addInput);
        right.getChildren().add(train);
        right.getChildren().add(classify);
        right.getChildren().add(result);

        setSelectState();
        updateClassList();

        fileInput.setOnAction(event -> {
            System.out.println("fileInput");
            inputDocuments = new ArrayList<>();
            inputDocuments.addAll(fileChooser.showOpenMultipleDialog(primaryStage).stream().map(Document::new).collect(Collectors.toList()));
            setAddedState();
        });

        addInput.setOnAction(event -> {
            if (validClass()) {
                System.out.println("AddInput");
                controller.addClassWithDocs(new Class(className.getText(), controller.getBaysianClassifier()), inputDocuments);
                setSelectState();
            } else {
                showPopup("Please select a classname");
            }
        });

        train.setOnAction(event -> {
            controller.train();
            setSelectState();
        });

        classify.setOnAction(event -> {
        	System.out.println(inputDocuments.size());
            if (inputDocuments.size() == 1) {
            	Document documentToClassify = inputDocuments.get(0);
                Class resultClass = controller.classify(documentToClassify);
                showResult(resultClass);
            } else {
                showPopup("Please only select one file to Classify");
            }
        });

        Scene scene = new Scene(pane);
        primaryStage.setScene(scene);
        primaryStage.show();
    }

    private void showPopup(String s) {
        //TODO stud
    }

    private void showResult(Class resultClass) {
        result.setText(resultClass.toString());
    }

    private void setAddedState() {
        setInvisRight();
        addInput.setVisible(true);
        className.setVisible(true);
        displayTrainClassify();
        updateClassList();
    }

    private void setSelectState() {
        setInvisRight();
        fileInput.setVisible(true);
        displayTrainClassify();
        updateClassList();
    }

    private void displayTrainClassify() {
        if (controller.canTrain() && !addInput.isVisible()) {
            train.setVisible(true);
        }
        if (controller.canClassify() && !fileInput.isVisible()) {
            classify.setVisible(true);
        }
    }

    private void setInvisRight() {
        for (Node n : right.getChildren()) n.setVisible(false);
    }

    private boolean validClass() {
        return !className.getText().equals(STANDARDTEXT) && !className.getText().equals("");
    }

    private void updateClassList() {
        String list = CLASSLISTSTART;
        for (Class c : controller.getClassAndDocs().keySet()) {
            list += "\n\t" + c.getName() + "\t\t" + controller.getClassAndDocs().get(c).size();
        }
        classList.setText(list);
    }
}