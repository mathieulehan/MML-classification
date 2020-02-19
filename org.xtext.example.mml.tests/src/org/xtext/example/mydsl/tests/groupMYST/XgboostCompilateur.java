package org.xtext.example.mydsl.tests.groupMYST;

import java.io.File;
import java.io.IOException;
import java.util.Date;
import java.util.List;

import org.xtext.example.mydsl.mml.CrossValidation;
import org.xtext.example.mydsl.mml.DT;
import org.xtext.example.mydsl.mml.DataInput;
import org.xtext.example.mydsl.mml.FormulaItem;
import org.xtext.example.mydsl.mml.LogisticRegression;
import org.xtext.example.mydsl.mml.MLAlgorithm;
import org.xtext.example.mydsl.mml.RandomForest;
import org.xtext.example.mydsl.mml.SVM;
import org.xtext.example.mydsl.mml.TrainingTest;
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
		String boostImport = "import xgboost as xgb"+"\r\n"; 
		boostImport += "import pandas as pd"+"\r\n"; 
		String csvReading = "mml_data = pd.read_csv(" + mkValueInSingleQuote(dataInput.getFilelocation()) + ", sep=" + 
				mkValueInSingleQuote(separator) + ")"+"\r\n";	
		
		String predictivestr = "";
		if (predictive != null ) {
			//TODO
			predictivestr = ""+"\r\n";
		}else {
			//dernière colonne predictive.getColumn()
			//TODO
			predictivestr = ""+"\r\n";
		}
		
		String predictorstr =""+"\r\n";
		
		
		String algostr ="";
		if(algo instanceof SVM) {
			  //TODO
			  SVM svm = (SVM)algo;
			  algostr = "algo = "+"\r\n";
		}else if(algo instanceof DT) {
			 DT dt = (DT)algo;
				int max_depth = dt.getMax_depth();
				 
				if(max_depth != 0) {
					 algostr = "algo = "+"\r\n";
				}else {
					 algostr = "algo = "+"\r\n";
				}
		}else if(algo instanceof RandomForest ) {
			  //TODO
			  algostr = "algo = "+"\r\n";
		}else if(algo instanceof LogisticRegression) {
			  //TODO
			  algostr = "algo = "+"\r\n";
		}
		
		int num;
		String val = "";
		if (validation.getStratification() instanceof CrossValidation) {
			  //TODO
			  num = validation.getStratification().getNumber();
			  val = ""+"\r\n";
		}else if (validation.getStratification() instanceof TrainingTest) {
			  //TODO
			  num = validation.getStratification().getNumber();
			  val = ""+"\r\n";
		
		}
		
		String affiche ="";
		String metric ="";
		for (ValidationMetric laMetric : metrics) {
		switch(laMetric.getLiteral()) {
		  case "balanced_accuracy":
			  //TODO
			   metric +=""+"\r\n";
			   affiche  +=""+"\r\n";
		    break;
		  case "recall":
			  //TODO
			  metric +=""+"\r\n";
			  affiche  +=""+"\r\n";
		    break;
		  case "precision":
			  //TODO
			  metric +=""+"\r\n";
			  affiche  +=""+"\r\n";
			    break;
		  case "F1":
			  //TODO
			  metric +=""+"\r\n";
			  affiche  +=""+"\r\n";
			    break;
		  case "accuracy":
			  //TODO
			  metric +=""+"\r\n";
			  affiche  +=""+"\r\n";
			    break;
		  case "macro_recall":
			  //TODO
			  metric +=""+"\r\n";
			  affiche  +=""+"\r\n";
			    break;
		  case "macro_precision":
			  //TODO
			  metric +=""+"\r\n";
			  affiche  +=""+"\r\n";
			    break;
		  case "macro_F1":
			  //TODO
			  metric +=""+"\r\n";
			  affiche  +=""+"\r\n";
			    break;
		  case "macro_accuracy":
			  //TODO
			  metric +=""+"\r\n";
			  affiche  +=""+"\r\n";
			    break;
		}

		}
		
		String xgBoostCode = boostImport + csvReading+ predictivestr +predictorstr  + algostr + val + metric +affiche;
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
