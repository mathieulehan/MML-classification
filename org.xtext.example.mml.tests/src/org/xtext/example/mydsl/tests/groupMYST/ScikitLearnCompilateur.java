package org.xtext.example.mydsl.tests.groupMYST;

import java.io.File;
import java.io.IOException;
import java.util.List;

import org.xtext.example.mydsl.mml.DataInput;
import org.xtext.example.mydsl.mml.FormulaItem;
import org.xtext.example.mydsl.mml.MLAlgorithm;
import org.xtext.example.mydsl.mml.MLChoiceAlgorithm;
import org.xtext.example.mydsl.mml.Validation;
import org.xtext.example.mydsl.mml.ValidationMetric;
import org.xtext.example.mydsl.mml.XFormula;

import com.google.common.io.Files;

public class ScikitLearnCompilateur implements Compilateur {
	private MLAlgorithm algo;
	private DataInput dataInput;
	private Validation validation;
	private String separator;
	private FormulaItem predictive;
	private XFormula predictore;
	private String fileResult;
	private List<ValidationMetric> metrics;
	

	
	public void execute() throws IOException {
		String pythonImport = "import pandas as pd\n"; 
		String csvReading = "mml_data = pd.read_csv(" + mkValueInSingleQuote(dataInput.getFilelocation()) + ", sep=" + 
		mkValueInSingleQuote(separator) + ")";	
		
		//TODO
		String predictor = "";
		
		//TODO
		String predictive = "";
		
		String algo ="";
		switch(algo.toString()) {
		  case "SVM":
			  //TODO
			  algo = ""+"/n";
		    break;
		  case "DT":
			  //TODO
			  algo = ""+"/n";
		    break;
		  case "RandomForest":
			  //TODO
			  algo = ""+"/n";
			    break;
		  case "LogisticRegression":
			  //TODO
			  algo = ""+"/n";
			    break;
		}
	
		//TODO PREDICTIVE
		//TODO PREDICTORE
		String val = "";
		switch(validation.getStratification().toString()) {
		  case "CrossValidation":
			  //TODO
			  val = ""+"/n";
		    break;
		  case "TrainingTest":
			  //TODO
			  val = ""+"/n";
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
		
		String pandasCode = pythonImport + csvReading + algo + val + metric;
		pandasCode += "\nprint (mml_data)\n"; 
		Files.write(pandasCode.getBytes(), new File("mml_ScikitLearn_"+new java.util.Date().getTime()+".py"));
	}
	public void configure(MLAlgorithm algo, DataInput dataInput, Validation validation, String separator, 	FormulaItem predictive, XFormula predictore , String fileResult) {
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
