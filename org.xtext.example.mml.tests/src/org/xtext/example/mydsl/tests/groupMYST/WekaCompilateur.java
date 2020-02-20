package org.xtext.example.mydsl.tests.groupMYST;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.Reader;
import java.util.List;
import java.util.Random;
import java.util.Scanner;

import weka.core.Instances;
import weka.core.converters.CSVLoader;
import weka.gui.beans.DataSource;
import weka.classifiers.Classifier;
import weka.classifiers.Evaluation;
import weka.classifiers.trees.J48;
import weka.classifiers.trees.RandomForest;
import org.xtext.example.mydsl.mml.CrossValidation;
import org.xtext.example.mydsl.mml.DT;
import org.xtext.example.mydsl.mml.DataInput;
import org.xtext.example.mydsl.mml.FormulaItem;
import org.xtext.example.mydsl.mml.LogisticRegression;
import org.xtext.example.mydsl.mml.MLAlgorithm;
import org.xtext.example.mydsl.mml.SVM;
import org.xtext.example.mydsl.mml.TrainingTest;
import org.xtext.example.mydsl.mml.Validation;
import org.xtext.example.mydsl.mml.ValidationMetric;
import org.xtext.example.mydsl.mml.XFormula;


public class WekaCompilateur implements Compilateur {
	private MLAlgorithm algo;
	private DataInput dataInput;
	private Validation validation;
	private String separator;
	private FormulaItem predictive;
	private XFormula predictore;
	private String fileResult;
	private List<ValidationMetric> metrics;
	
	
	public void execute() throws IOException{
	
	CSVLoader loader = new CSVLoader();
	loader.setSource(new File(dataInput.getFilelocation()));
	Instances data = loader.getDataSet();
	
	String predictivestr = "";
	if (predictive != null ) {
		predictivestr = ""+"\r\n";
	}else {
		//dernière colonne predictive.getColumn()
		predictivestr = ""+"\r\n";
	}
	//TODO
	String predictorstr ="";
	
	int trainSize = (int) Math.round(data.numInstances() * 0.8);
	int testSize = data.numInstances() - trainSize;
	Instances train = new Instances(data, trainSize);
	Instances test = new Instances(data, testSize);
	
	Classifier classifier = null;
	
	if(algo instanceof SVM) {
//	    Y a po !!
	}else if(algo instanceof DT) {
		classifier = new J48();
	}else if(algo instanceof org.xtext.example.mydsl.mml.RandomForest ) {
		classifier = new RandomForest();
	}else if(algo instanceof LogisticRegression) {
		classifier = new weka.classifiers.functions.Logistic();
	}
	
	Evaluation eval = null;
	try {
		eval = new Evaluation(train);
	} catch (Exception e1) {
		// TODO Auto-generated catch block
		e1.printStackTrace();
	}
	int num = validation.getStratification().getNumber();

	if (validation.getStratification() instanceof CrossValidation) {
		try {
			eval.crossValidateModel(classifier, data, num, new Random(1));
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}else if (validation.getStratification() instanceof TrainingTest) {
	   
	}
	
	for (ValidationMetric laMetric : metrics) {
	switch(laMetric.getLiteral()) {
	  case "balanced_accuracy":
		  
	    break;
	  case "recall":
		  eval.recall(0);
	    break;
	  case "precision":
		  eval.precision(0);
		    break;
	  case "F1":
		  	
		    break;
	  case "accuracy":
		  
		    break;
	  case "macro_recall":
		  
		    break;
	  case "macro_precision":
		  
		    break;
	  case "macro_F1":
		  
		    break;
	  case "macro_accuracy":
		  
		    break;
	}

	}
	
	try {
		classifier.buildClassifier(train);
	} catch (Exception e) {
		// TODO Auto-generated catch block
		e.printStackTrace();
	}
		
	try {
		eval.evaluateModel(classifier, test);
	} catch (Exception e) {
		// TODO Auto-generated catch block
		e.printStackTrace();
	}
	System.out.println(eval.toSummaryString("\nResults\n======\n", false));
		
//	Long date = new Date().getTime();
//	Files.write(pandasCode.getBytes(), new File("mml_Weka_"+date+".py"));

//	Process p = Runtime.getRuntime().exec("python mml"+date+".py");
//	BufferedReader in = new BufferedReader(new InputStreamReader(p.getInputStream()));
		
	if (writeInFile) {
		File myFile = new File("recall.csv");
		FileOutputStream oFile = new FileOutputStream(myFile, true);
		oFile.write((dataInput.getFilelocation() +";"+NameAlgo+";ScikitLearn;"+fin+";"+in.read()+"\n").getBytes());
		oFile.close();
		}
	}
	public void configure(MLAlgorithm algo, DataInput dataInput, Validation validation, String separator, FormulaItem predictive, XFormula predictore,  String fileResult) {
		this.algo = algo;
		this.dataInput = dataInput;
		this.validation = validation;
		this.separator = separator;
		this.predictive = predictive;
		this.predictore = predictore;
		this.fileResult = fileResult;
		this.metrics = validation.getMetric();
	}
	
}
