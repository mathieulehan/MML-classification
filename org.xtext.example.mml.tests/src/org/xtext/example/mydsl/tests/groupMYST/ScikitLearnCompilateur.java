package org.xtext.example.mydsl.tests.groupMYST;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.Date;
import java.util.List;

import org.xtext.example.mydsl.mml.DT;
import org.xtext.example.mydsl.mml.DataInput;
import org.xtext.example.mydsl.mml.FormulaItem;
import org.xtext.example.mydsl.mml.LogisticRegression;
import org.xtext.example.mydsl.mml.MLAlgorithm;
import org.xtext.example.mydsl.mml.MLChoiceAlgorithm;
import org.xtext.example.mydsl.mml.RandomForest;
import org.xtext.example.mydsl.mml.SVM;
import org.xtext.example.mydsl.mml.SVMClassification;
import org.xtext.example.mydsl.mml.SVMKernel;
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
		String pythonImport = "import pandas as pd\r\n"; 
		String csvReading = "mml_data = pd.read_csv(" + mkValueInSingleQuote(dataInput.getFilelocation()) + ", sep=" + 
		mkValueInSingleQuote(separator) + ")\r\n";	
		
		String predictivestr = "";
		if (predictive.getColName() != null && predictive.getColName()!= "") {
			predictivestr = "X = mml_data.drop(columns=[\""+predictive.getColName()+"\"])"+"\r\n";
		}else {
			//dernière colonne predictive.getColumn()
			predictivestr = "X = mml_data.drop(mml_data.columns[len(mml_data.columns)-1])"+"\r\n";
		}
		
		//TODO
		String predictorstr ="Y = mml_data[mml_data.columns[len(mml_data.columns)-1]] ";
		
		String algostr ="";
		if(algo instanceof SVM) {
			  //TODO
			SVM svm = (SVM)algo;
			String c = svm.getC();
			String gamma = svm.getGamma();
			
			if(svm.isKernelSpecified()) {
				SVMKernel kernel = svm.getKernel();
			}
			
			if(svm.isClassificationSpecified()) {
				SVMClassification classification = svm.getSvmclassification();
				switch(classification.getName()) {
					case "C-classification":
						pythonImport +="from sklearn.svm import SVC";
						algostr =	"algo = SVC(gamma="+gamma+").fit(X, Y)";
					case "nu-classification":
						pythonImport +="from sklearn.svm import NuSVC";
						algostr = "algo = NuSVC().fit(X, Y)";
					case "one-classification":
						pythonImport +="from sklearn.svm import OneClassSVM";
						algostr = "algo = OneClassSVM(gamma="+gamma+").fit(X)";
				}
			}
			
	
		}else if(algo instanceof DT) {
			 DT dt = (DT)algo;
				int max_depth = dt.getMax_depth();
			pythonImport += " from sklearn.tree import DecisionTreeClassifier"+"\r\n";
			if(max_depth != 0) {
				algostr = "algo = DecisionTreeClassifier(max_depth="+max_depth+")"+"\r\n";
			}else {
				algostr = "algo = DecisionTreeClassifier()"+"\r\n";
			}
		}else if(algo instanceof RandomForest ) {
			pythonImport += "from sklearn.ensemble import RandomForestClassifier";
			algostr = "algo = RandomForestClassifier().fit(X, Y)\r\n" ;
		}else if(algo instanceof LogisticRegression) {
			pythonImport += "from sklearn.linear_model import LogisticRegression";
			algostr = "algo = LogisticRegression().fit(X, Y) "+"\r\n";
		}
		
		String val = "";
		int num;
		switch(validation.getStratification().toString()) {
		  case "CrossValidation":
				 pythonImport += "from sklearn.model_selection import cross_validate"+"\r\n";
				 num = validation.getStratification().getNumber();
			  val = "scores = cross_validate(algo, X, Y, cv="+num+")"+"\r\n";
		    break;
		  case "TrainingTest":
			  pythonImport += "from sklearn.model_selection import train_test_split"+"\r\n";
			  num = validation.getStratification().getNumber();
			  val = "test_size =" +num+"\r\n" + 
			  		"X_train, X_test, Y_train, Y_test = train_test_split(X, Y, test_size=test_size)"+"\r\n";
			  val +="scores =  algo.fit(X_train, y_train)"+"\r\n";
			  break;
		}
		
		String affiche ="";
		String metric ="";
		for (ValidationMetric laMetric : metrics) {
		switch(laMetric.getName()) {
		  case "balanced_accuracy":
			  //TODO
				pythonImport +="from sklearn.metrics import balanced_accuracy_score"+"\r\n";
			   metric +="balanceA = balanced_accuracy_score(y_true, y_pred)"+"\r\n";
			   affiche  +="print(balanceA)";
		    break;
		  case "precision":
			  //TODO
			  pythonImport +="from sklearn.metrics import precision_score"+"\r\n";
			  metric +="precision = precision_score(y_true, y_pred, average='micro')"+"\r\n";
			   affiche  +="print(precision)";
		    break;
		  case "recall":
			  //TODO
			  pythonImport +="from sklearn.metrics import recall_score"+"\r\n";
			  metric +="recall = recall_score(y_true, y_pred,average='micro' )"+"\r\n";
			  affiche  +="print(recall)"+"\r\n";
			    break;
		  case "F1":
			  //TODO
			  pythonImport +="from sklearn.metrics import f1_score"+"\r\n";
			  metric +="f1 = f1_score(y_true, y_pred, average='micro')"+"\r\n";
			   affiche  +="print(f1)";
			    break;
		  case "accuracy":
			  //TODO
			pythonImport +="from sklearn.metrics import accuracy_score"+"\r\n";
			  metric +="accuracy = accuracy_score(y_true, y_pred)"+"\r\n";
			   affiche  +="print(accuracy)";
			    break;
		  case "macro_recall":
			  //TODO
			  pythonImport +="from sklearn.metrics import recall_score"+"\r\n";
			  metric +="recall = recall_score(y_true, y_pred,average='macro' )"+"\r\n";
			  affiche  +="print(recall)";
			    break;
		  case "macro_precision":
			  //TODO
			  pythonImport +="from sklearn.metrics import precision_score"+"\r\n";
			  metric +="precision = precision_score(y_true, y_pred, average='macro')"+"\r\n";
			   affiche  +="print(precision)";
			    break;
		  case "macro_F1":
			  //TODO
			  pythonImport +="from sklearn.metrics import f1_score"+"\r\n";
			  metric +="f1 = f1_score(y_true, y_pred, average='macro')"+"\r\n";
			   affiche  +="print(f1)";
			    break;
		  case "macro_accuracy":
			  //TODO
			  metric +=""+"\r\n";
			  affiche  +=""+"\r\n";
			    break;
		}

		}
		
		String pandasCode = pythonImport + csvReading + predictorstr + predictivestr + algostr + val + metric + affiche;
		Long date = new Date().getTime();
		Files.write(pandasCode.getBytes(), new File("mml_ScikitLearn_"+date+".py"));
	
		Process p = Runtime.getRuntime().exec("python mml"+date+".py");
		BufferedReader in = new BufferedReader(new InputStreamReader(p.getInputStream()));
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
