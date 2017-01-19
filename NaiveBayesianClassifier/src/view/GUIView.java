//<<<<<<< HEAD
////package view;
////
////import controller.Controller;
////import javafx.application.Application;
////import javafx.geometry.Insets;
////import javafx.geometry.Pos;
////import javafx.scene.Scene;
////import javafx.scene.control.Button;
////import javafx.scene.control.TextField;
////import javafx.scene.layout.BorderPane;
////import javafx.scene.layout.VBox;
////import javafx.scene.text.Text;
////import javafx.stage.FileChooser;
////import javafx.stage.Modality;
////import javafx.stage.Stage;
////import model.Class;
////import model.Document;
////
////import java.io.File;
////import java.io.FileNotFoundException;
////import java.util.ArrayList;
////import java.util.List;
////import java.util.stream.Collectors;
////
////public class GUIView extends Application {
////
////    private static final String CLASSES = "Classes in the System:";
////    private final String classFieldSTRING = "Class Name";
////    private Controller controller;
////
////    public static void main(String[] args) {
////        launch(args);
////    }
////
////    @Override
////    public void start(Stage primaryStage) throws Exception {
////        controller = new Controller();
////        FileChooser fileChooser = new FileChooser();
////        List<Document> documents = new ArrayList<>();
////        primaryStage.setTitle("Naive Bayesian Classifier");
////        fileChooser.setTitle("Open Resource File");
////
////        BorderPane border = new BorderPane();
////        VBox leftClasses = new VBox(8);
////        VBox rightInteract = new VBox(12);
////        rightInteract.setAlignment(Pos.CENTER);
////        rightInteract.setPadding(new Insets(10));
////        border.setLeft(leftClasses);
////        border.setRight(rightInteract);
////
////
////
////        Text classes = new Text();
////        classes.setText(CLASSES);
////        TextField classField = new TextField();
////        classField.setText(classFieldSTRING);
////        Button add = new Button();
////        add.setText("Add Files");
////        Button slt = new Button();
////        slt.setText("Select Files");
////        Button trn = new Button();
////        trn.setText("Train");
////        Button cls = new Button();
////        cls.setText("Classify");
////
////<<<<<<< HEAD
////        leftClasses.getChildren().add(classes);
////
////        initState(classField, classes, add, slt, trn, cls, border);
////
////
////        add.setOnAction(event -> {
////            String className = classField.getText().trim();
////            if (!className.equals(null) && !className.equals(classFieldSTRING)) {
////                controller.addClassWithDocs(new Class(className, controller.getBaysianClassifier()), documents);
////                documents.clear();
////                initState(classField, classes, add, slt, trn, cls, border);
////                update(trn, cls, border);
////            } else {
////                dialogBox("Well hello there", primaryStage);
////=======
////        Button slt = new Button();
////        slt.setText("Select Files");
////        slt.setOnAction(event -> {
////            String classText= classField.getText().trim();
////            if (!(classText.equals("Class...") || classText.equals(""))) {
////                List<File> files = fileChooser.showOpenMultipleDialog(primaryStage);
////                List<Document> documents = new ArrayList<>();
////                if (files != null) {
////                	documents.addAll(files.stream().map(Document::new).collect(Collectors.toList()));
////                }
////                controller.addClassWithDocs(new Class(classText, controller.getBaysianClassifier()), documents);
////                if (controller.canTrain() && !controller.canClassify()) trn.setVisible(true);
////                if (controller.canClassify()) cls.setVisible(true);
////>>>>>>> 46ed0121774da95de38e6860cfd8a984551f7d8d
////            }
////        });
////
////        slt.setOnAction(event -> {
////                    List<File> files = fileChooser.showOpenMultipleDialog(primaryStage);
////                    if (files != null) {
////                        documents.addAll(files.stream().map(Document::new).collect(Collectors.toList()));
////                        if (!border.getChildren().contains(classField) && !border.getChildren().contains(add)) {
////                            sltState(classField, add, slt, border);
////                        }
////                    }
////                }
////        );
////
////        Scene scene = new Scene(border);
////        primaryStage.setScene(scene);
////        primaryStage.show();
////    }
////
////    private void dialogBox(String s, Stage primaryStage) {
////        Stage dialog = new Stage();
////        dialog.initModality(Modality.APPLICATION_MODAL);
////        dialog.initOwner(primaryStage);
////        VBox dialogVbox = new VBox(20);
////        dialogVbox.getChildren().add(new Text(s));
////        Scene dialogScene = new Scene(dialogVbox, 300, 200);
////        dialog.setScene(dialogScene);
////        dialog.show();
////    }
////
////    public void initState(TextField classField, Text classes, Button add, Button slt, Button trn, Button cls, BorderPane border) {
////        ((VBox) border.getRight()).getChildren().removeAll(classField, add, slt, trn, cls);
////        ((VBox) border.getRight()).getChildren().add(slt);
////    }
////
////    public void update(Button trn, Button cls, BorderPane border) {
////        if (controller.canTrain()) {
////            ((VBox) border.getRight()).getChildren().add(trn);
////        }
////        if (controller.canClassify()) {
////            ((VBox) border.getRight()).getChildren().add(cls);
////        }
////        if (controller.getClassAndDocs().keySet().size() > 0) {
////            String result = CLASSES;
////            for (Class c : controller.getClassAndDocs().keySet()) {
////                ((Text) ((VBox) border.getLeft()).getChildren().get(0)).setText(result + "\n\t" + c.getName() + "\t" + controller.getClassAndDocs().get(c).size());
////            }
////        }
////    }
////
////    public void sltState(TextField classField, Button add, Button slt, BorderPane border) {
////        ((VBox) border.getRight()).getChildren().add(classField);
////        ((VBox) border.getRight()).getChildren().add(add);
////        ((VBox) border.getRight()).getChildren().remove(slt);
////    }
////}
//=======
//package view;
//
//import controller.Controller;
//import javafx.application.Application;
//import javafx.scene.Node;
//import javafx.scene.Scene;
//import javafx.scene.control.Button;
//import javafx.scene.control.TextField;
//import javafx.scene.layout.BorderPane;
//import javafx.scene.layout.VBox;
//import javafx.scene.text.Text;
//import javafx.stage.FileChooser;
//import javafx.stage.Stage;
//import model.Class;
//import model.Document;
//
//import java.util.ArrayList;
//import java.util.List;
//import java.util.stream.Collectors;
//
//public class GUIView extends Application {
//
//    private static final String STANDARDTEXT = "class name";
//    private static final String CLASSLISTSTART = "Classes in the system:";
//    private Controller controller;
//    private FileChooser fileChooser;
//    private List<Document> inputDocuments;
//    private VBox right;
//    private TextField className;
//    private Text classList;
//    private Button fileInput;
//    private Button addInput;
//    private Button train;
//    private Button classify;
//    private Text result;
//
//    @Override
//    public void start(Stage primaryStage) throws Exception {
//        controller = new Controller();
//        fileChooser = new FileChooser();
//        inputDocuments = new ArrayList<>();
//        BorderPane pane = new BorderPane();
//        VBox left = new VBox();
//        right = new VBox();
//        classList = new Text();
//        className = new TextField();
//        fileInput = new Button();
//        addInput = new Button();
//        train = new Button();
//        classify = new Button();
//        result = new Text();
//
//        fileInput.setText("select");
//        addInput.setText("add");
//        train.setText("train");
//        classify.setText("classify");
//
//        pane.setLeft(left);
//        pane.setRight(right);
//
//        left.getChildren().add(classList);
//        right.getChildren().add(className);
//        right.getChildren().add(fileInput);
//        right.getChildren().add(addInput);
//        right.getChildren().add(train);
//        right.getChildren().add(classify);
//        right.getChildren().add(result);
//
//        setSelectState();
//        updateClassList();
//
//        fileInput.setOnAction(event -> {
//            System.out.println("fileInput");
//            inputDocuments = new ArrayList<>();
//            inputDocuments.addAll(fileChooser.showOpenMultipleDialog(primaryStage).stream().map(Document::new).collect(Collectors.toList()));
//            setAddedState();
//        });
//
//        addInput.setOnAction(event -> {
//            if (validClass()) {
//                System.out.println("AddInput");
//                controller.addClassWithDocs(new Class(className.getText(), controller.getBaysianClassifier()), inputDocuments);
//                setSelectState();
//            } else {
//                showPopup("Please select a classname");
//            }
//        });
//
//        train.setOnAction(event -> {
//            controller.train();
//            setSelectState();
//        });
//
//        classify.setOnAction(event -> {
//        	System.out.println(inputDocuments.size());
//            if (inputDocuments.size() == 1) {
//            	Document documentToClassify = inputDocuments.get(0);
//                Class resultClass = controller.classify(documentToClassify);
//                showResult(resultClass);
//            } else {
//                showPopup("Please only select one file to Classify");
//            }
//        });
//
//        Scene scene = new Scene(pane);
//        primaryStage.setScene(scene);
//        primaryStage.show();
//    }
//
//    private void showPopup(String s) {
//        //TODO stud
//    }
//
//    private void showResult(Class resultClass) {
//        result.setText(resultClass.toString());
//    }
//
//    private void setAddedState() {
//        setInvisRight();
//        addInput.setVisible(true);
//        className.setVisible(true);
//        displayTrainClassify();
//        updateClassList();
//    }
//
//    private void setSelectState() {
//        setInvisRight();
//        fileInput.setVisible(true);
//        displayTrainClassify();
//        updateClassList();
//    }
//
//    private void displayTrainClassify() {
//        if (controller.canTrain() && !addInput.isVisible()) {
//            train.setVisible(true);
//        }
//        if (controller.canClassify() && !fileInput.isVisible()) {
//            classify.setVisible(true);
//        }
//    }
//
//    private void setInvisRight() {
//        for (Node n : right.getChildren()) n.setVisible(false);
//    }
//
//    private boolean validClass() {
//        return !className.getText().equals(STANDARDTEXT) && !className.getText().equals("");
//    }
//
//    private void updateClassList() {
//        String list = CLASSLISTSTART;
//        for (Class c : controller.getClassAndDocs().keySet()) {
//            list += "\n\t" + c.getName() + "\t\t" + controller.getClassAndDocs().get(c).size();
//        }
//        classList.setText(list);
//    }
//}
//>>>>>>> a8c54d7c91bbbad780e326f52dfe767b5d2ad8e5
