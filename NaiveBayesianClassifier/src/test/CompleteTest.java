package test;

import java.io.File;
import java.util.*;

import model.*;
import model.Class;
import controller.Controller;
import javafx.scene.Scene;
import javafx.scene.chart.XYChart;
import javafx.scene.chart.NumberAxis;
import javafx.scene.chart.LineChart;
import javafx.stage.Stage;
import javafx.application.*;

public class CompleteTest extends Application {
	
	@Override 
	public void start(Stage stage) {
        stage.setTitle("Accuracy measurement");
        //defining the axes
        final NumberAxis xAxis = new NumberAxis();
        final NumberAxis yAxis = new NumberAxis(0.5, 1, 0.01);
        yAxis.setAutoRanging(false);
        xAxis.setLabel("% of data classified");
        //creating the chart
        final LineChart<Number,Number> lineChart = 
                new LineChart<Number,Number>(xAxis,yAxis);
                
        lineChart.setTitle("Accuracy over time :");
        lineChart.setCreateSymbols(false);
        XYChart.Series seriesTotal = new XYChart.Series();
        XYChart.Series seriesClass1 = new XYChart.Series();
        XYChart.Series seriesClass2 = new XYChart.Series();
        seriesTotal.setName("Overall accuracy");
        seriesClass1.setName("Class 1 accuracy");
        seriesClass2.setName("Class 2 accuracy");
        Controller controller = new Controller();
		File[] class1Train = new File("C:\\Users\\Vincent\\Downloads\\corpus-mails\\corpus-mails\\corpus\\hamtraintest").listFiles();
		File[] class2Train = new File("C:\\Users\\Vincent\\Downloads\\corpus-mails\\corpus-mails\\corpus\\spamtraintest").listFiles();
//		File[] class1Train = new File("C:\\Users\\Vincent\\Downloads\\blogs\\MaleTest").listFiles();
//		File[] class2Train = new File("C:\\Users\\Vincent\\Downloads\\blogs\\FemaleTest").listFiles();
//		File[] class1Train = new File("C:\\Users\\V\\Downloads\\news20.tar\\20_newsgroup\\alt.atheism train").listFiles();
//		File[] class2Train = new File("C:\\Users\\V\\Downloads\\news20.tar\\20_newsgroup\\comp.graphics train").listFiles();
//		File[] class3Train = new File("C:\\Users\\V\\Downloads\\news20.tar\\20_newsgroup\\comp.os.ms-windows.misc train").listFiles();
		
		Set<Document> documentsClass1 = new HashSet<Document>();
		for (File f : class1Train) { documentsClass1.add(new Document(f)); }; 
		Set<Document> documentsClass2 = new HashSet<Document>();
		for (File f : class2Train) { documentsClass2.add(new Document(f)); };
		Set<Document> documentsClass3 = new HashSet<Document>();
//		for (File f : class3Train) { documentsClass3.add(new Document(f)); };
		
		Class class1Class = new Class("Class1", controller.getBaysianClassifier());
		Class class2Class = new Class("Class2", controller.getBaysianClassifier());
		Class class3Class = new Class("Class3", controller.getBaysianClassifier());
		controller.addClassWithDocs(class1Class, documentsClass1);
		controller.addClassWithDocs(class2Class, documentsClass2);
//		controller.addClassWithDocs(class3Class, documentsClass3);
		controller.train();
		System.out.println("Document count: " + controller.getBaysianClassifier().documentCount);
		System.out.println("Vocabulary size: " + controller.getBaysianClassifier().getfeatureVocabularySize());
		
		File[] class1Test = new File("C:\\Users\\Vincent\\Downloads\\corpus-mails\\corpus-mails\\corpus\\hamtest").listFiles();
		File[] class2Test = new File("C:\\Users\\Vincent\\Downloads\\corpus-mails\\corpus-mails\\corpus\\spamtest").listFiles();
//		File[] class1Test = new File("C:\\Users\\Vincent\\Downloads\\blogs\\MaleTrain").listFiles();
//		File[] class2Test = new File("C:\\Users\\Vincent\\Downloads\\blogs\\FemaleTrain").listFiles();
//		File[] class1Test = new File("C:\\Users\\V\\Downloads\\news20.tar\\20_newsgroup\\alt.atheism test").listFiles();
//		File[] class2Test = new File("C:\\Users\\V\\Downloads\\news20.tar\\20_newsgroup\\comp.graphics test").listFiles();
//		File[] class3Test = new File("C:\\Users\\V\\Downloads\\news20.tar\\20_newsgroup\\comp.os.ms-windows.misc test").listFiles();
		List<Document> documentsClass1Test = new ArrayList<Document>();
		List<Document> documentsClass2Test = new ArrayList<Document>();
		List<Document> documentsClass3Test = new ArrayList<Document>();
		for (File f : class1Test) { documentsClass1Test.add(new Document(f)); }; 
		for (File f : class2Test) { documentsClass2Test.add(new Document(f)); };
//		for (File f : class3Test) { documentsClass3Test.add(new Document(f)); };
		
		int correctlyClassifiedClass1 = 0;
		int correctlyClassifiedClass2 = 0;
		int correctlyClassifiedClass3 = 0;
		int totalClassified1 = 0;
		int totalClassified2 = 0;
		System.out.println("Max. index : " + Math.max(documentsClass1Test.size(), Math.max(documentsClass2Test.size(), documentsClass3Test.size())));
		
		for(int i = 0; i < Math.max(documentsClass1Test.size(), Math.max(documentsClass2Test.size(), documentsClass3Test.size())); i++) {
			System.out.println("Iteration: " + i);
			if(i < documentsClass2Test.size()) {
				if(controller.getBaysianClassifier().classify(documentsClass2Test.get(i)).equals(class2Class)) {
					correctlyClassifiedClass2++;
					
				}
				totalClassified2++;
				controller.getBaysianClassifier().train(documentsClass2Test.get(i), class2Class);
			}
			
			if(i < documentsClass1Test.size()) {
				if(controller.getBaysianClassifier().classify(documentsClass1Test.get(i)).equals(class1Class)) {
					correctlyClassifiedClass1++;
					
				}
				totalClassified1++;
				controller.getBaysianClassifier().train(documentsClass1Test.get(i), class1Class);
			}
			
//			if(i < documentsClass3Test.size()) {
//				if(controller.getBaysianClassifier().classify(documentsClass3Test.get(i)).equals(class3Class)) {
//					correctlyClassifiedClass3++;
//					System.out.println("WAS HERE");
//				}
//				
//				controller.getBaysianClassifier().train(documentsClass3Test.get(i), class3Class);
//			}
			
			System.out.println("preliminary % correct : " + ((double)(correctlyClassifiedClass2 + correctlyClassifiedClass1) / (totalClassified1 + totalClassified2)));
			seriesTotal.getData().add(new XYChart.Data((totalClassified1 + totalClassified2), ((double)(correctlyClassifiedClass2 + correctlyClassifiedClass1) / (totalClassified1 + totalClassified2))));
			seriesClass1.getData().add(new XYChart.Data((totalClassified1 + totalClassified2), ((double)(correctlyClassifiedClass1) / totalClassified1)));
			seriesClass2.getData().add(new XYChart.Data((totalClassified1 + totalClassified2), ((double)(correctlyClassifiedClass2) / totalClassified2)));
			
			
		}
		
		
		
		
		
//		for(Document d: documentsClass1Test) {
//			if(controller.getBaysianClassifier().classify(d).equals(class1Class)) {
//				correctlyClassifiedClass1++;
//			}
//			controller.getBaysianClassifier().train(d, class1Class);
//		}
//		
//		for(Document d: documentsClass2Test) {
//			if(controller.getBaysianClassifier().classify(d).equals(class2Class)) {
//				correctlyClassifiedClass2++;
//			}
//			controller.getBaysianClassifier().train(d, class2Class);
//		}
		System.out.println("Result class1 : " + ((double)correctlyClassifiedClass1 / (double)documentsClass1Test.size()) );
		System.out.println("Result class2 : " + ((double)correctlyClassifiedClass2 / (double)documentsClass2Test.size()) );
		System.out.println("Result class3 : " + ((double)correctlyClassifiedClass3 / (double)documentsClass3Test.size()) );
//		System.out.println("Total result : " +((double)(correctlyClassifiedClass1 + correctlyClassifiedClass2))/ ((double)(documentsClass1Test.size() + documentsClass2Test.size())) );
		System.out.println("Total result : " +((double)(correctlyClassifiedClass1 + correctlyClassifiedClass2 + correctlyClassifiedClass3))/ ((double)(documentsClass1Test.size() + documentsClass2Test.size() + documentsClass3Test.size())) );
//		for(Class c: controller.getBaysianClassifier().getClasses().keySet()) {
//			c.ConProbsString();
//		}
		
		Scene scene  = new Scene(lineChart,800,600);
		lineChart.getData().add(seriesTotal);
		lineChart.getData().add(seriesClass2);
		lineChart.getData().add(seriesClass1);
	       
		stage.setScene(scene);
        stage.show();
    }
 
    public static void main(String[] args) {
        launch(args);
    }

}
