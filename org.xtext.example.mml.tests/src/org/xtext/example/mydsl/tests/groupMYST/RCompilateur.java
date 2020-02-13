package org.xtext.example.mydsl.tests.groupMYST;

import java.io.File;
import java.io.IOException;
import java.util.List;

import org.xtext.example.mydsl.mml.DataInput;
import org.xtext.example.mydsl.mml.FormulaItem;
import org.xtext.example.mydsl.mml.MLAlgorithm;
import org.xtext.example.mydsl.mml.Validation;
import org.xtext.example.mydsl.mml.ValidationMetric;
import org.xtext.example.mydsl.mml.XFormula;

import com.google.common.io.Files;

public class RCompilateur implements Compilateur{
	private MLAlgorithm algo;
	private DataInput dataInput;
	private Validation validation;
	private String separator;
	private FormulaItem predictive;
	private XFormula predictore;
	private String fileResult;
	

	private List<ValidationMetric> metrics;
	
	public void execute() throws IOException {
		String rImport = "library(rpart)";
		String csvReading = "data <- read.csv(\""+dataInput.getFilelocation()+"\", header = FALSE, sep = \""+separator+"\")";
		
		String predictivestr = "";
		if (predictive.getColName() != null && predictive.getColName()!= "") {
			//TODO
			predictivestr = ""+"/n";
		}else {
			//dernière colonne predictive.getColumn()
			//TODO
			predictivestr = ""+"/n";
		}
		
		String predictorstr ="";
		
		String algo ="";
		switch(algo.toString()) {
		  case "SVM":
			  //TODO
			  algo = "";
		    break;
		  case "DT":
			  //TODO
			  algo = "";
		    break;
		  case "RandomForest":
			  //TODO
			  algo = "";
			    break;
		  case "LogisticRegression":
			  //TODO
			  algo = "";
			    break;
		}
	

		String val = "";
		switch(validation.getStratification().toString()) {
		  case "CrossValidation":
			  //TODO
			  val = "";
		    break;
		  case "TrainingTest":
			  //TODO
			  val = "";
		    break;
		}
		
		String metric ="";
		for (ValidationMetric laMetric : metrics) {
		switch(laMetric.getName()) {
		  case "balanced_accuracy":
			  //TODO
			   metric +=""+"/n";
		    break;
		  case "recall":
			  //TODO
			  metric +=""+"/n";
		    break;
		  case "precision":
			  //TODO
			  metric +=""+"/n";
			    break;
		  case "F1":
			  //TODO
			  metric +=""+"/n";
			    break;
		  case "accuracy":
			  //TODO
			  metric +=""+"/n";
			    break;
		  case "macro_recall":
			  //TODO
			  metric +=""+"/n";
			    break;
		  case "macro_precision":
			  //TODO
			  metric +=""+"/n";
			    break;
		  case "macro_F1":
			  //TODO
			  metric +=""+"/n";
			    break;
		  case "macro_accuracy":
			  //TODO
			  metric +=""+"/n";
			    break;
		}

		}
		

		
		String rCode = rImport + csvReading + predictivestr +predictorstr  + algo + val + metric;
		//TODO
		rCode += ""; //trouver a afficher en R
		//TODO
		Files.write(rCode.getBytes(), new File("mml_R"+new java.util.Date().getTime()+".???????"));		
	}
	public void configure(MLAlgorithm algo, DataInput dataInput, Validation validation, String separator,  FormulaItem predictive, XFormula predictore, String fileResult) {
		this.algo = algo;
		this.dataInput = dataInput;
		this.validation = validation;
		this.separator = separator;
		this.predictive = predictive;
		this.predictore = predictore;
		this.fileResult = fileResult;
		this.metrics = validation.getMetric();
	}
	
	private String mkValueInSingleQuote(String val) {
		return "'" + val + "'";
	}
}
