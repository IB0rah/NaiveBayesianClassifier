package view;

import controller.Controller;
import model.Class;

import javafx.application.Application;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.TextField;
import javafx.scene.layout.GridPane;
import javafx.stage.FileChooser;
import javafx.stage.Stage;
import model.Document;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public class GUIView extends Application {

    public static void main(String[] args) {
        launch(args);
    }

    @Override
    public void start(Stage primaryStage) throws Exception {
        Controller controller = new Controller();
        FileChooser fileChooser = new FileChooser();
        primaryStage.setTitle("Naive Baysian Classifier");
        fileChooser.setTitle("Open Resource File");

        GridPane grid = new GridPane();
        grid.setAlignment(Pos.CENTER);
        grid.setHgap(10);
        grid.setVgap(10);
        grid.setPadding(new Insets(25, 25, 25, 25));

        Scene scene = new Scene(grid, 300, 275);
        primaryStage.setScene(scene);

        TextField classField = new TextField();
        classField.setText("Class...");

        Button trn = new Button();
        trn.setText("Train");
        trn.setOnAction(event -> {
            if (controller.canTrain()) controller.train();
        });
        grid.add(trn, 1, 1);
        trn.setVisible(false);


        Button cls = new Button();
        cls.setText("Classify");
        cls.setOnAction(event -> {
            if (controller.canClassify()) controller.classify();
        });
        grid.add(cls, 1, 1);
        cls.setVisible(false);

        Button slt = new Button();
        slt.setText("Select Files");
        slt.setOnAction(event -> {
            String classText= classField.getText().trim();
            if (!(classText.equals("Class...") || classText.equals(""))) {
                List<File> files = fileChooser.showOpenMultipleDialog(primaryStage);
                List<Document> documents = new ArrayList<>();
                if (files != null) documents.addAll(files.stream().map(Document::new).collect(Collectors.toList()));
                controller.addClassWithDocs(new Class(classText, controller.getBaysianClassifier()), documents);
                if (controller.canTrain() && !controller.canClassify()) trn.setVisible(true);
                if (controller.canClassify()) cls.setVisible(true);
            }
        });


        grid.add(classField, 1, 0);
        grid.add(slt, 2, 0);
//        if (controller.canTrain()) grid.add(trn, 1, 1);
//        if (controller.canClassify()) grid.add(cls, 1, 1);
        primaryStage.show();
    }
}
