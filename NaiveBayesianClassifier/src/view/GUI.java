package view;

import controller.Controller;
import javafx.application.Application;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ProgressIndicator;
import javafx.scene.control.TextField;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.VBox;
import javafx.scene.text.Text;
import javafx.stage.FileChooser;
import javafx.stage.Modality;
import javafx.stage.Stage;
import model.Class;
import model.Document;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

public class GUI extends Application {

    private static final String CLASS = "class name";
    private static final String CHI = "0";
    private static final String CLASSLISTSTART = "Classes in the system:";
    private static final String CLASSIFYTEXT = "file to classify";
    private static final String SELECTTEXT = "file(s) for training";
    private static final String CHIINSTRUCTION = "Set A Chi Value";
    private static final String TRAINISTRUCTION = "Select Files Per Class";
    private static final String CLASSIFYINSTRUCTION = "Select A File To Classify";
    private static final String ADDINSTRUCTION = "Name of Class for selected file(s):";
    private Label instruction;
    private Controller controller;
    private FileChooser fileChooser;
    private Set<Document> inputDocuments;
    private VBox right;
    private TextField className;
    private Text classList;
    private Button fileInput;
    private Button addInput;
    private Button train;
    private Button classify;
    private TextField ChiValue;
    private Button ChiSet;
    private boolean classifying;
    private Class resultClass;
    private ProgressIndicator pi;

    @Override
    public void start(Stage primaryStage) throws Exception {
        controller = new Controller();
        fileChooser = new FileChooser();
        ChiValue = new TextField();
        ChiSet = new Button();
        BorderPane pane = new BorderPane();
        VBox left = new VBox();
        right = new VBox();
        classList = new Text();
        className = new TextField();
        fileInput = new Button();
        addInput = new Button();
        train = new Button();
        classify = new Button();
        classifying = false;
        instruction = new Label();
        pi = new ProgressIndicator(-1);

        fileInput.setText("Training Files");
        addInput.setText("add");
        train.setText("train");
        classify.setText("classify");
        ChiSet.setText("Set Chi value");

        pane.setLeft(left);
        pane.setRight(right);

        right.setAlignment(Pos.CENTER);
        right.setPadding(new Insets(40));

        left.getChildren().add(classList);

        setInitState();
        updateClassList();

        ChiSet.setOnAction(event -> {
            if (validChi()) {
                controller.setChi(Integer.parseInt(ChiValue.getText().trim()));
                setSelectState();
            } else {
                showPopup("Please insert a numeric value", primaryStage);
            }
        });

        fileInput.setOnAction(event -> {
            System.out.println("fileInput");
            inputDocuments = new HashSet<>();
            fileInput.setDisable(true);
            right.getChildren().add(pi);
            inputDocuments.addAll(fileChooser.showOpenMultipleDialog(primaryStage).stream().map(Document::new).collect(Collectors.toList()));
            setAddedState();
        });

        addInput.setOnAction(event -> {
            if (validClass()) {
                System.out.println("AddInput");
                controller.addClassWithDocs(new Class(className.getText(), controller.getBaysianClassifier()), inputDocuments);
                setSelectState();
            } else {
                showPopup("Please select a classname", primaryStage);
            }
        });

        train.setOnAction(event -> {
            train.setDisable(true);
            right.getChildren().add(pi);
            controller.train();
            setClassifyState();
        });

        classify.setOnAction(event -> {
            if (inputDocuments.size() == 1) {
            	ArrayList<Document> documentsAsList = new ArrayList<Document>();
            	documentsAsList.addAll(inputDocuments);
                resultClass = controller.classify(documentsAsList.get(0));
                showResult(resultClass, primaryStage);
            } else {
                showPopup("Please only select one file to Classify", primaryStage);
            }
        });
        Scene scene = new Scene(pane);
        primaryStage.setScene(scene);
        primaryStage.show();
    }

    private boolean validChi() {
        return ChiValue.getText().trim().matches("\\d+") && !ChiValue.getText().trim().equals(CHI);
    }

    private void setInitState() {
        setInvisRight();
        instruction.setText(CHIINSTRUCTION);
        right.getChildren().add(instruction);
        right.getChildren().add(ChiValue);
        right.getChildren().add(ChiSet);
    }

    private void showPopup(String s, Stage primaryStage) {
        Stage dialog = new Stage();
        VBox dialogVbox = new VBox();
        dialogVbox.setAlignment(Pos.CENTER);

        dialog.initOwner(primaryStage);
        dialog.initModality(Modality.APPLICATION_MODAL);
        dialogVbox.getChildren().add(new Text(s));

        Scene dialogScene = new Scene(dialogVbox);
        dialog.setScene(dialogScene);
        dialog.show();
    }

    private void setSelectState() {
        setInvisRight();
        instruction.setText(TRAINISTRUCTION);
        right.getChildren().add(instruction);
        right.getChildren().add(fileInput);
        fileInput.setText(SELECTTEXT);
        className.setText(CLASS);
        displayTrainClassify();
        updateClassList();
    }

    private void setAddedState() {
        if (!classifying) {
            setInvisRight();
            instruction.setText(ADDINSTRUCTION);
            right.getChildren().add(instruction);
            right.getChildren().add(className);
            right.getChildren().add(addInput);
            displayTrainClassify();
            updateClassList();
        } else {
            fileInput.setDisable(false);
            displayTrainClassify();
        }
    }

    private void setClassifyState() {
        setInvisRight();
        instruction.setText(CLASSIFYINSTRUCTION);
        right.getChildren().add(instruction);
        right.getChildren().add(fileInput);
        fileInput.setText(CLASSIFYTEXT);
        displayTrainClassify();
        classifying = true;
        updateClassList();
    }

    private void showResult(Class resultClass, Stage primaryStage) {
        Stage dialog = new Stage();
        VBox dialogVbox = new VBox();
        dialogVbox.setAlignment(Pos.CENTER);
        dialogVbox.setPadding(new Insets(15));

        Label result = new Label();
        result.setText(resultClass.toString());

        Button btnAcceptClassification = new Button();
        Button btnDenyClassification = new Button();

        btnAcceptClassification.setText("Correct");
        btnDenyClassification.setText("Choose another class");

        dialogVbox.getChildren().addAll(result, btnAcceptClassification, btnDenyClassification);

        btnAcceptClassification.setOnAction(event -> {
        	ArrayList<Document> documentsAsList = new ArrayList<Document>();
        	documentsAsList.addAll(inputDocuments);
            controller.getBaysianClassifier().train(documentsAsList.get(0), resultClass);
            dialog.close();
        });

        btnDenyClassification.setOnAction(event -> {
            dialogVbox.getChildren().clear();
            for (Class c : controller.getClassAndDocs().keySet()) {
                Button btn = new Button();
                btn.setText(c.toString());
                btn.setOnAction( event1 -> {
                	ArrayList<Document> documentsAsList = new ArrayList<Document>();
                	documentsAsList.addAll(inputDocuments);
                    controller.getBaysianClassifier().train(documentsAsList.get(0), c);
                    dialog.close();
                });
                dialogVbox.getChildren().add(btn);

            }
        });
        dialog.initOwner(primaryStage);
        dialog.initModality(Modality.APPLICATION_MODAL);
        Scene dialogScene = new Scene(dialogVbox);
        dialog.setScene(dialogScene);
        dialog.show();
    }

    private void displayTrainClassify() {
        if (controller.canTrain() && !controller.canClassify()) {
            right.getChildren().add(train);
        }
        if (controller.canClassify() && classifying) {
            right.getChildren().add(classify);
        }
    }

    private void setInvisRight() {
        right.getChildren().clear();
        fileInput.setDisable(false);
        train.setDisable(false);
        classify.setDisable(false);
    }

    private boolean validClass() {
        return !className.getText().equals(CLASS) && !className.getText().equals("");
    }

    private void updateClassList() {
        String list = CLASSLISTSTART;
        for (Class c : controller.getClassAndDocs().keySet()) {
            list += "\n\t" + c.getName() + "\t\t" + controller.getClassAndDocs().get(c).size();
        }
        classList.setText(list);
    }
}