package org.xtext.example.mydsl.tests.groupMYST;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.Date;
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
		
		String predictivestr = "";
		if (predictive.getColName() != null && predictive.getColName()!= "") {
			predictivestr = "X = mml_data.drop(columns=[\""+predictive.getColName()+"\"])"+"/n";
		}else {
			//dernière colonne predictive.getColumn()
			predictivestr = "X = mml_data.drop(mml_data.columns[len(mml_data.columns)-1])"+"/n";
		}
		
		//TODO
		String predictorstr ="Y = mml_data[mml_data.columns[len(mml_data.columns)-1]] ";
		
		
		
		
		String algostr ="";
		switch(algo.toString()) {
		  case "SVM":
			  //TODO
			 
			  algostr = "algo = "+"/n";
		    break;
		  case "DT":
			   pythonImport = " from sklearn.tree import DecisionTreeClassifier";
			  algostr = "algo = DecisionTreeClassifier()"+"/n";
		    break;
		  case "RandomForest":
			  //TODO
			  algostr = "algo = "+"/n";
			    break;
		  case "LogisticRegression":
			  //TODO
			  algostr = "algo = "+"/n";
			    break;
		}

		String val = "";
		int num;
		switch(validation.getStratification().toString()) {
		  case "CrossValidation":
				 pythonImport += "from sklearn.model_selection import cross_validate";
				 num = validation.getStratification().getNumber();
			  val = "scores = cross_validate(algo, X, Y, cv="+num+")"+"/n";
		    break;
		  case "TrainingTest":
			  pythonImport += "from sklearn.model_selection import train_test_split";
			  num = validation.getStratification().getNumber();
			  val = "test_size =" +num+"\n" + 
			  		"X_train, X_test, Y_train, Y_test = train_test_split(X, Y, test_size=test_size)"+"/n";
			  val +="scores =  algo.fit(X_train, y_train)";
			  break;
		}
		
		String affiche ="";
		String metric ="";
		for (ValidationMetric laMetric : metrics) {
		switch(laMetric.getName()) {
		  case "balanced_accuracy":
			  //TODO
				pythonImport +="from sklearn.metrics import balanced_accuracy_score";
			   metric +="balanceA = balanced_accuracy_score(y_true, y_pred)"+"/n";
			   affiche  +="print(balanceA)"+"/n";
		    break;
		  case "precision":
			  //TODO
			  pythonImport +="from sklearn.metrics import precision_score";
			  metric +="precision = precision_score(y_true, y_pred, average='micro')"+"/n";
			   affiche  +="precision"+"/n";
		    break;
		  case "recall":
			  //TODO
			  pythonImport +="from sklearn.metrics import recall_score";
			  metric +="recall = recall_score(y_true, y_pred,average='micro' )"+"/n";
			  affiche  +="print(recall)"+"/n";
			    break;
		  case "F1":
			  //TODO
			  pythonImport +="from sklearn.metrics import f1_score";
			  metric +="f1 = f1_score(y_true, y_pred, average='micro')"+"/n";
			   affiche  +="print(f1)"+"/n";
			    break;
		  case "accuracy":
			  //TODO
			pythonImport +="from sklearn.metrics import accuracy_score";
			  metric +="accuracy = accuracy_score(y_true, y_pred)"+"/n";
			   affiche  +="print(accuracy)"+"/n";
			    break;
		  case "macro_recall":
			  //TODO
			  pythonImport +="from sklearn.metrics import recall_score";
			  metric +="recall = recall_score(y_true, y_pred,average='macro' )"+"/n";
			  affiche  +="print(recall)"+"/n";
			    break;
		  case "macro_precision":
			  //TODO
			  pythonImport +="from sklearn.metrics import precision_score";
			  metric +="precision = precision_score(y_true, y_pred, average='macro')"+"/n";
			   affiche  +="precision"+"/n";
			    break;
		  case "macro_F1":
			  //TODO
			  pythonImport +="from sklearn.metrics import f1_score";
			  metric +="f1 = f1_score(y_true, y_pred, average='macro')"+"/n";
			   affiche  +="print(f1)"+"/n";
			    break;
		  case "macro_accuracy":
			  //TODO
			  metric +=""+"/n";
			  affiche  +=""+"/n";
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
