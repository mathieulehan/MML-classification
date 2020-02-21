package org.xtext.example.mydsl.tests.groupMYST;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
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
		String boostImport = "import xgboost as xgb\r\n"; 
		boostImport += "import pandas as pd\r\n"; 
		String csvReading = "mml_data = pd.read_csv(" + mkValueInSingleQuote(dataInput.getFilelocation()) + ", sep=" + 
				mkValueInSingleQuote(separator) + ")\r\n";	
		
		String predictivestr = "";
		if (predictive != null) {
			predictivestr = "X = mml_data.drop(columns=[\""+predictive.getColName()+"\"])"+"\r\n";
		}else {
			//dernière colonne predictive.getColumn()
			predictivestr = "X = mml_data.drop(columns=mml_data.columns[len(mml_data.columns)-1])\r\n";
		}

		String predictorstr ="Y = mml_data[mml_data.columns[len(mml_data.columns)-1]]\r\n";
		
		String NameAlgo ="";		
		String algostr ="";
		if(algo instanceof SVM) {
			  //TODO
			NameAlgo = "SVM";
			  SVM svm = (SVM)algo;
			  algostr = "algo = "+"\r\n";
		}else if(algo instanceof DT) {
			 DT dt = (DT)algo;
				NameAlgo = "Decision Tree";
				int max_depth = dt.getMax_depth();
				 
				if(max_depth != 0) {
					 algostr = "algo = \r\n";
				}else {
					 algostr = "algo = \r\n";
				}
		}else if(algo instanceof RandomForest ) {
			  //TODO
			NameAlgo = "Random Forest";
			  algostr = "algo = "+"\r\n";
		}else if(algo instanceof LogisticRegression) {
			  //TODO
			NameAlgo = "Logistic Regression";
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
		
		boolean writeInFile = false;
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
			  writeInFile = true;
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
		
		long debut = System.currentTimeMillis();
		Process p = Runtime.getRuntime().exec("python mml_Xgboost_"+date+".py");
		long fin = System.currentTimeMillis()-debut;
		BufferedReader in = new BufferedReader(new InputStreamReader(p.getInputStream()));
		
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
	
	private String mkValueInSingleQuote(String val) {
		return "'" + val + "'";
	}
}
