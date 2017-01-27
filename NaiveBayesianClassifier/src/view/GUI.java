package view;

import controller.Controller;
import javafx.application.Application;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.chart.LineChart;
import javafx.scene.chart.NumberAxis;
import javafx.scene.chart.XYChart;
import javafx.scene.control.*;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.ColumnConstraints;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.Pane;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.scene.text.FontPosture;
import javafx.scene.text.Text;
import javafx.stage.FileChooser;
import javafx.stage.Stage;
import model.Class;
import model.Document;

import java.io.File;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

import static java.util.Collections.max;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;

public class GUI extends Application {

    /**
     * Controller interacts with the Bayesian Classifier
     */
    private Controller controller;
    /**
     * FileChooser
     */
    private FileChooser fileChooser;
    /**
     *
     */
    private Pane pane;
    /**
     * +--------------+----------+-----------------+
     * |    + / -     |          |                 |
     * | "class name" |  select  |      Train      |
     * | "class name" |  select  |     Classify    |
     * |              |          |                 |
     * |              |          |                 |
     * |              |          |                 |
     */
    private GridPane grid;
    /**
     * Map with TextFields which represent classes and add select option
     */
    private Map<Integer, Node[]> classes;
    /**
     * Map with Integers corresponding to the nodes with the documents
     */
    private Map<Integer, Set<Document>> documents;
    /**
     * Map with Classes and files
     */
    private Map<Class, Set<Document>> classWithDocs;
    /**
     * Capsule for addition and deletion of class options
     */
    private GridPane addDeletePane;
    /**
     * Buttons for global access
     */
    private Button train;
    private Button determine;
    private TextField chi;

    public static void main(String[] args) {
        launch(args);
    }

    private void setup() {
        pane = new Pane();
        grid = new GridPane();
        classes = new HashMap<>();
        documents = new HashMap<>();
        controller = new Controller();
        fileChooser = new FileChooser();
        classWithDocs = new HashMap<>();
        Label l = new Label();
        ColumnConstraints column1 = new ColumnConstraints(200);
        ColumnConstraints column2 = new ColumnConstraints(100);
        grid.getColumnConstraints().addAll(column1, column2);
        l.setText("Classes: ");
        grid.add(l, 0, 0);
        pane.getChildren().add(grid);
    }

    /**
     * When an extra classes is needed this methods is called.
     */
    private void addClassOption(Stage primaryStage) {
        int i = classes.size();
        TextField t = new TextField();
        Button b = new Button("Select");
        classes.put(i, new Node[]{t, b});
        t.setPromptText("classname " + (i + 1));
        b.setOnAction(event -> {
            System.out.println("documents = " + documents.toString());
            documents.put(i, fileChooser.showOpenMultipleDialog(primaryStage).stream().map(Document::new).collect(Collectors.toSet()));
            b.setText("Slected: " + documents.get(i).size());
            checkTrain();
        });
        grid.add(t, 0, classes.size() + 1);
        grid.add(b, 1, classes.size() + 1);
    }

    private void checkTrain() {
        boolean enable = true;
        for (Integer i : documents.keySet()) {
            if (documents.keySet().size() == classes.keySet().size() && documents.get(i).size() > 0) {
                enable = false;
                System.out.println("enable = " + enable);
                break;
            }
        }
        train.setDisable(enable);
    }

    private void checkDetermine(Map<Class, List<Document>> preAnalytic) {
        determine.setDisable(!(preAnalytic.size() == classWithDocs.size()));
    }

    /**
     * When a class needs to be removed this method is called. It ensures
     * there are always two or more classes selection open.
     */
    private void deleteClassOption() {
        if (classes.size() > 2) {
            int index = max(classes.keySet());
            grid.getChildren().removeAll(classes.get(index));
            classes.remove(index);
        }
    }

    private void train() {
        if (validChi()) {
            controller.setChi(Integer.parseInt(chi.getText().trim()));
        } else {
            controller.setChi(300);
        }
        updateClassNames();
        for (Class c : classWithDocs.keySet()) {
            controller.addClassWithDocs(c, classWithDocs.get(c));
        }
        controller.train();
    }

    private boolean validChi() {
        return chi.getText().trim().matches("\\d+");
    }

    private void updateClassNames() {
        for (Integer i : documents.keySet()) {
            String name = ((TextField) classes.get(i)[0]).getText().trim();
            if (!name.contains("classname ")) {
                classWithDocs.put(new Class(name, controller.getBaysianClassifier()), documents.get(i));
            } else {
                //TODO Error PopUp
            }
        }
    }

    /**
     * Declaration of main buttons and Grid structure.
     *
     * @param primaryStage
     */
    @Override
    public void start(Stage primaryStage) {
        setup();
        addClassOption(primaryStage);
        addClassOption(primaryStage);

        /*
         * Button deceleration and capsule
         */
        // +/-
        addDeletePane = new GridPane();
        Button addClass = new Button("+");
        Button deleteClass = new Button("-");
        addDeletePane.add(addClass, 0, 0);
        addDeletePane.add(deleteClass, 1, 0);
        // Train
        train = new Button("Train");
        train.setDisable(true);
        // Chi
        Label chiValue = new Label("Number of Features: ");
        chi = new TextField();
        chi.setPromptText("300");
        chi.setMaxWidth(46);

        /*
         * Buttons.setOnAction();
         */
        addClass.setOnAction(event -> addClassOption(primaryStage));
        deleteClass.setOnAction(event -> deleteClassOption());
        train.setOnAction(event -> {
            train();
            tabs(primaryStage);
        });

        /*
         * Button added to grid
         */
        grid.setAlignment(Pos.CENTER);
        grid.setPadding(new Insets(5));
        grid.add(addDeletePane, 1, 0);
        grid.add(chiValue, 2, 2);
        grid.add(chi, 3, 2);
        grid.add(train, 3, 3);

        /*
         * Set Stage and Alignment
         */
        Scene scene = new Scene(pane, pane.getMaxWidth(), 600);
        primaryStage.setScene(scene);
        primaryStage.show();
    }

    /**
     * Initializes the two tab view to either classify one file or determine
     * statistics
     *
     * @param primaryStage
     */
    private void tabs(Stage primaryStage) {
        pane.getChildren().clear();
        TabPane tabs = new TabPane();
        tabs.setTabMinWidth((pane.getWidth() - 38) / 2);
        tabs.setTabClosingPolicy(TabPane.TabClosingPolicy.UNAVAILABLE);
        Tab singleTab = new Tab("Classify File");
        Tab statisticTab = new Tab("Determine Statistics");
        singleTab.setContent(singleClassify(primaryStage));
        statisticTab.setContent(statisticClassify(primaryStage));
        tabs.getTabs().add(singleTab);
        tabs.getTabs().add(statisticTab);
        pane.getChildren().add(tabs);
    }

    private Node statisticClassify(Stage primaryStage) {
        AnchorPane classifyPane = new AnchorPane();
        GridPane classifyGrid = new GridPane();
        classifyPane.getChildren().add(classifyGrid);
        classifyGrid.setMinHeight(pane.getHeight());

        ColumnConstraints column1 = new ColumnConstraints(pane.getWidth() / 3);
        ColumnConstraints column2 = new ColumnConstraints(pane.getWidth() / 3);
        ColumnConstraints column3 = new ColumnConstraints(pane.getWidth() / 3);
        classifyGrid.getColumnConstraints().addAll(column1, column2, column3);

        Map<Class, List<Document>> preAnalytic = new HashMap<>();

        int row = 1;
        for (Class c : classWithDocs.keySet()) {
            Label l = new Label("Select Data For Class: ");
            Button b = new Button(c.getName());
            b.setOnAction(event -> {
                List<Document> d = fileChooser.showOpenMultipleDialog(primaryStage).stream().map(Document::new).collect(Collectors.toList());
                preAnalytic.put(c, d);
                b.setText(c.getName() + " (" + d.size() + ")");
                checkDetermine(preAnalytic);
            });
            classifyGrid.add(l, 0, row);
            classifyGrid.add(b, 1, row);
            row++;
        }

        int totalCorrectCount = 0;
        Map<Class, Integer> correctPerClass = new HashMap<>();

        CheckBox learning = new CheckBox("Learning");
        classifyGrid.add(learning, 2, 0);

        determine = new Button("Determine");
        determine.setOnAction(event1 -> {
            NumberAxis xAxis = new NumberAxis(0, 1, 0.1);
            NumberAxis yAxis = new NumberAxis(0.3, 1, 0.01);
            yAxis.setAutoRanging(false);
            xAxis.setLabel("% of data classified");
            //creating the chart
            final LineChart<Number,Number> lineChart =
                    new LineChart<Number,Number>(xAxis,yAxis);

            lineChart.setTitle("Accuracy over time :");
            lineChart.setCreateSymbols(false);
            Map<Class, XYChart.Series> classSeries = new HashMap<Class, XYChart.Series>();
            for(Class c: preAnalytic.keySet()){
                correctPerClass.put(c, 0);
                XYChart.Series classSerie = new XYChart.Series();
                classSerie.setName(c.getName() + " accuracy" );
                classSeries.put(c, classSerie);
            }
            XYChart.Series seriesTotal = new XYChart.Series();
            seriesTotal.setName("Overall accuracy");
            GridPane confusionMatrix = new GridPane();
            double lowestBound = calculateAnslytics(preAnalytic, totalCorrectCount, correctPerClass,
                    				learning.isSelected(), classSeries, seriesTotal, confusionMatrix);
            for(Class c : classSeries.keySet()) {
                lineChart.getData().add(classSeries.get(c));
            }
            lineChart.getData().add(seriesTotal);
            yAxis.setLowerBound(lowestBound);
            classifyPane.getChildren().clear();
            GridPane gridLayout = new GridPane();
            gridLayout.add(lineChart, 0, 0);
            gridLayout.add(confusionMatrix, 0, 1);
            gridLayout.setAlignment(Pos.CENTER);
            classifyPane.getChildren().add(gridLayout);
            
        });
        
        
        
        /*
               TODO Waardes printen van:
                           - totalCorrectCount
                           - correctPerClass.forEach()
                           - eventuele waardes die nog berekend worden op basis van de vorige twee.
                           - kusjes
         */
        
        determine.setDisable(true);
        classifyGrid.add(determine, 2, 3);

        return classifyPane;
    }

    private int determineMaxIndex(Map<Class, List<Document>> preAnalytic) {
        Set<Integer> docListSizes = new HashSet<Integer>();
        for( Class c : preAnalytic.keySet()) {
            docListSizes.add(preAnalytic.get(c).size());
        }

        return Collections.max(docListSizes);
    }
    private double calculateAnslytics(Map<Class, List<Document>> preAnalytic, int totalCorrectCount, Map<Class, Integer> correctPerClass, boolean learning, Map<Class, XYChart.Series> classSeries, XYChart.Series totalSeries, GridPane confusionMatrix) {
    	double lowestBound = 1;
        Map<Class, Integer> classCounts = new HashMap<Class, Integer>();
        Map<Class, Map<Class, Integer>> wrongClassified = new HashMap<>();
        List<Class> classList = new ArrayList<Class>();
        classList.addAll(correctPerClass.keySet());
        for(Class c: preAnalytic.keySet()) {
            classCounts.put(c, 0);
        }
        int totalDocuments = 0;
        for(Class c : preAnalytic.keySet()) {
            totalDocuments += preAnalytic.get(c).size();
        }
        double totalClassified = 0;
        for(int i = 0; i < determineMaxIndex(preAnalytic); i++) {
//            System.out.println("Iteration: " + i);
        	
            for(Class c: preAnalytic.keySet()) {
                if(i < preAnalytic.get(c).size()) {
                    if(controller.getBaysianClassifier().classify(preAnalytic.get(c).get(i)).equals(c)) {
                        totalCorrectCount++;
                        if(correctPerClass.containsKey(c)) {
                            correctPerClass.put(c, correctPerClass.get(c) + 1);
                        } else {
                            correctPerClass.put(c, 1);
                        }
                        
                    } else {
                    	Class classification = controller.getBaysianClassifier().classify(preAnalytic.get(c).get(i));
                    	if(wrongClassified.containsKey(c)) {
                    		if(wrongClassified.get(c).containsKey(classification)) {
                    			wrongClassified.get(c).put(classification, wrongClassified.get(c).get(classification) + 1);
                    		} else {
                    			wrongClassified.get(c).put(classification, 1);
                    		}
                    	} else {
                    		Map<Class, Integer> newMap = new HashMap<>();
                    		newMap.put(classification, 1);
                    		wrongClassified.put(c, newMap);
                    	}
                    }

                    classCounts.put(c, classCounts.get(c) + 1);
                }
                if(learning) {
                    controller.getBaysianClassifier().train(preAnalytic.get(c).get(i), c);
                }
            }
            double percentageClassified = 0;
            totalClassified = 0;
            for(Class c: classCounts.keySet()) {
                totalClassified += classCounts.get(c);
                percentageClassified += ((double)classCounts.get(c)) / ((double)totalDocuments);
            }
            
            if(percentageClassified > 0.1) {
            	for(Class c: classSeries.keySet()) {
                    classSeries.get(c).getData().add(new XYChart.Data(percentageClassified, ((double)(correctPerClass.get(c)) / ((double) classCounts.get(c)) )));
                    if(((double)(correctPerClass.get(c)) / ((double) classCounts.get(c)) ) < lowestBound) {
                    	lowestBound = ((double)(correctPerClass.get(c)) / ((double) classCounts.get(c)) );
                    }
                }
                totalSeries.getData().add(new XYChart.Data(percentageClassified, ((double)totalCorrectCount) / ((double) totalClassified )));
            }
        }
        confusionMatrix.setAlignment(Pos.CENTER);
       
        for(Class c1: classList) {
        	confusionMatrix.add(new Label(c1.getName() + " Actual:  "), classList.indexOf(c1) + 1, 0);
        	confusionMatrix.add(new Label(c1.getName() + " Predicted:  "), 0, classList.indexOf(c1) + 1 );
        	confusionMatrix.add(new Label(" " + correctPerClass.get(c1)), classList.indexOf(c1) + 1, classList.indexOf(c1) + 1);
        	confusionMatrix.add(new Label(" " + preAnalytic.get(c1).size() ), classList.indexOf(c1) + 1, classList.size() + 1);
        	for(Class c2: classList) {
        		if(!c2.equals(c1)) {
        			if(wrongClassified.get(c1) != null && wrongClassified.get(c1).get(c2) != null) {
        				confusionMatrix.add(new Label(" " + wrongClassified.get(c1).get(c2)), classList.indexOf(c1) + 1, classList.indexOf(c2) + 1);
        			} else {
        				confusionMatrix.add(new Label(" " + 0), classList.indexOf(c1) + 1, classList.indexOf(c2) + 1);
        			}
        		}
        	}
        }
        
        confusionMatrix.add(new Label("Accuracy : " + ((double)totalCorrectCount) / ((double) totalClassified)), 0, classList.size() + 2);
        
        return lowestBound;
    }

    /**
     * Handles the tab for single classifaction
     *
     * @param primaryStage
     * @return
     */
    private Node singleClassify(Stage primaryStage) {
        GridPane classifyGrid = new GridPane();
        classifyGrid.setMinHeight(pane.getHeight());
        classifyGrid.setAlignment(Pos.TOP_CENTER);
        ColumnConstraints column1 = new ColumnConstraints(pane.getWidth() / 2);
        ColumnConstraints column2 = new ColumnConstraints(pane.getWidth() / 2);
        
        classifyGrid.getColumnConstraints().addAll(column1, column2);

        Button b = new Button("File to Classify");
        Button classify = new Button("Classify");
        classifyGrid.add(b, 0, 1);

        final Document[] d = new Document[1];
        b.setOnAction(event -> {
            File f = fileChooser.showOpenDialog(primaryStage);
            d[0] = new Document(f);
            b.setText(f.getName().toString());

            Text t = new Text(f.getName().toString());
            t.setFill(Color.RED);
            t.setFont(Font.font("Verdana", FontPosture.ITALIC, 20));
            t.textProperty().bind(b.textProperty());
            classifyGrid.add(classify, 0, 2);
        });
        classify.setOnAction(event -> {
            Button correct;
            Button incorrect;
            Class result = controller.classify(d[0]);
            classifyGrid.add(new Label("Classified as: \n\t" + result.toString()), 1, 0);
            classifyGrid.add(correct = new Button("Correct"), 1, 1);
            classifyGrid.add(incorrect = new Button("incorrect"), 1, 2);

            correct.setOnAction(event1 -> {
                controller.update(d[0], result);
                tabs(primaryStage);
            });

            incorrect.setOnAction(event2 -> {
                        classifyGrid.getChildren().removeAll(correct, incorrect);
                        int col = 2;
                        for (Class c : classWithDocs.keySet()) {
                            Button b1;
                            classifyGrid.add(b1 = new Button(c.toString()), 1, col);
                            b1.setOnAction(event3 -> {
                                controller.update(d[0], c);
                                tabs(primaryStage);
                            });
                            col++;

                        }
                    }
            );
        });
        return classifyGrid;
    }
}