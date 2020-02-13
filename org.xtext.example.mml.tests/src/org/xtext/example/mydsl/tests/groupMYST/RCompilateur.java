package org.xtext.example.mydsl.tests.groupMYST;

import java.io.File;
import java.io.IOException;
import java.util.Date;
import java.util.List;

import org.xtext.example.mydsl.mml.DT;
import org.xtext.example.mydsl.mml.DataInput;
import org.xtext.example.mydsl.mml.FormulaItem;
import org.xtext.example.mydsl.mml.MLAlgorithm;
import org.xtext.example.mydsl.mml.SVM;
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
		
		String algostr ="";
		switch(algo.toString()) {
		  case "SVM":
			  SVM svm = (SVM)algo;
			  //TODO
			  algostr = "";
		    break;
		  case "DT":
			  //TODO
			  DT dt = (DT)algo;
			int max_depth = dt.getMax_depth();
			  algostr = "";
		    break;
		  case "RandomForest":
			  //TODO
			  algostr = "";
			    break;
		  case "LogisticRegression":
			  //TODO
			  algostr = "";
			    break;
		}
	
		int num;
		String val = "";
		switch(validation.getStratification().toString()) {
		  case "CrossValidation":
			  //TODO
			  val = "";
			  num = validation.getStratification().getNumber();
		    break;
		  case "TrainingTest":
			  //TODO
			  val = "";
			  num = validation.getStratification().getNumber();
		    break;
		}
		
		String metric ="";
		 String affiche  ="";
		for (ValidationMetric laMetric : metrics) {
		switch(laMetric.getName()) {
		  case "balanced_accuracy":
			  //TODO
			   metric +=""+"/n";
			   affiche  +=""+"/n";
		    break;
		  case "recall":
			  //TODO
			  metric +=""+"/n";
			  affiche  +=""+"/n";
		    break;
		  case "precision":
			  //TODO
			  metric +=""+"/n";
			  affiche  +=""+"/n";
			    break;
		  case "F1":
			  //TODO
			  metric +=""+"/n";
			  affiche  +=""+"/n";
			    break;
		  case "accuracy":
			  //TODO
			  metric +=""+"/n";
			  affiche  +=""+"/n";
			    break;
		  case "macro_recall":
			  //TODO
			  metric +=""+"/n";
			  affiche  +=""+"/n";
			    break;
		  case "macro_precision":
			  //TODO
			  metric +=""+"/n";
			  affiche  +=""+"/n";
			    break;
		  case "macro_F1":
			  //TODO
			  metric +=""+"/n";
			  affiche  +=""+"/n";
			    break;
		  case "macro_accuracy":
			  //TODO
			  metric +=""+"/n";
			  affiche  +=""+"/n";
			    break;
		}

		}
		

		
		String rCode = rImport + csvReading + predictivestr +predictorstr  + algostr + val + metric +affiche;
		//TODO
		rCode += ""; //trouver a afficher en R
		//TODO
		Long date = new Date().getTime();
		Files.write(rCode.getBytes(), new File("mml_R"+date+".???????"));		
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
