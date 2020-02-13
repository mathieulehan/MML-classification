package org.xtext.example.mydsl.tests.groupMYST;

import java.io.File;
import java.io.IOException;
import java.util.Date;
import java.util.List;

import org.xtext.example.mydsl.mml.DataInput;
import org.xtext.example.mydsl.mml.FormulaItem;
import org.xtext.example.mydsl.mml.MLAlgorithm;
import org.xtext.example.mydsl.mml.Validation;
import org.xtext.example.mydsl.mml.ValidationMetric;
import org.xtext.example.mydsl.mml.XFormula;

import com.google.common.io.Files;

public class XgboostCompilateur implements Compilateur{
	private MLAlgorithm algo;
	private DataInput dataInput;
	private Validation validation;
	private String separator;
	private FormulaItem predictive;
	private XFormula predictore;
	private String fileResult;
	private List<ValidationMetric> metrics;
	
	public void execute()throws IOException {
		//TODO
		String boostImport = ""; 
		//TODO
		String csvReading = "";	
		
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
		
		int num;
		String val = "";
		switch(validation.getStratification().toString()) {
		  case "CrossValidation":
			  //TODO
			  num = validation.getStratification().getNumber();
			  val = "";
		    break;
		  case "TrainingTest":
			  //TODO
			  num = validation.getStratification().getNumber();
			  val = "";
		    break;
		}
		
		String affiche ="";
		String metric ="";
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
		
		String xgBoostCode = boostImport + csvReading+ predictivestr +predictorstr  + algo + val + metric +affiche;
		Long date = new Date().getTime();
		Files.write(xgBoostCode.getBytes(), new File("mml_Xgboost_"+date+".py"));

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
	
	private String mkValueInSingleQuote(String val) {
		return "'" + val + "'";
	}
}
