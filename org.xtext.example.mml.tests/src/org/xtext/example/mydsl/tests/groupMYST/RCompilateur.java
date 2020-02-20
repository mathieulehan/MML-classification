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
import org.xtext.example.mydsl.mml.SVMClassification;
import org.xtext.example.mydsl.mml.TrainingTest;
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
		String rImport = "install.packages(\"rpart\")\r\n";
		rImport += "install.packages(\"caret\")\r\n";
		rImport += "library(rpart)\r\n";
		rImport += "library(caret)\r\n";
		String csvReading = "data <- read.csv(\""+dataInput.getFilelocation()+"\", header = TRUE, sep = \""+separator+"\")\r\n";

		String predictivestr = "";
		if (predictive!= null ) {
			predictivestr = "X <- data$"+predictive.getColName()+" <- NULL"+"\r\n";
		}else {
			//dernière colonne predictive.getColumn()
			predictivestr = "X <- data[ncol(data)-1] <- NULL"+"\r\n";
		}

		String predictorstr ="Y <- data[ncol(data)-1]\r\n";

		String NameAlgo ="";
		String algostr ="";
		if(algo instanceof SVM) {
			SVM svm = (SVM)algo;
			NameAlgo = "SVM";
			if (svm.isClassificationSpecified()) {
				algostr += "install.packages(\"e1071\")\r\n";
				algostr += "library(e1071)\r\n";
				SVMClassification classification = svm.getSvmclassification();
				switch (classification.getName()) {
				case "C-classification":
					algostr += "algo <- svm(Y ~ X, data = data, kernel = \"linear\", cost = 10, scale = FALSE, type =\"C-classification\")";
					break;
				case "nu-classification":
					algostr += "algo2 <- svm(Y ~ X, data = data, kernel = \"linear\", cost = 10, scale = FALSE, type =\"nu-classification\")";
					break;
				case "one-classification":
					algostr += "algo3 <- svm(Y ~ X, data = data, kernel = \"linear\", cost = 10, scale = FALSE, type =\"one-classification\")";
					break;
				}
			}
		}else if(algo instanceof DT) {
			algostr += "n <- nrow(data)\r\n";
			algostr += "n_train <- round(0.8 * n)\r\n";
			algostr += "set.seed(123)\r\n";
			algostr += "train_indices <- sample(1:n, n_train)\r\n";
			algostr += "train <- data[train_indices, ]\r\n";
			algostr += "algo <- rpart(formula = Y ~ X, data = data, method = \"class\")\r\n";
			 NameAlgo = "Decision Tree";
		}else if(algo instanceof RandomForest ) {
			algostr += "install.packages(\"randomForest\")\r\n";
			algostr += "install.packages(\"dplyr\")\r\n";
			algostr += "library(dplyr)\r\n";
			algostr += "data4 <- select(data3, Survived, Name, Sex, Age, Ticket, Cabin, Embarked)\r\n";
			algostr += "library(randomForest)\r\n";
			algostr += "set.seed(123)\r\n";
			algostr += "algo <- randomForest(Y ~ X, data = data)";
			 NameAlgo = "Random Forest";
		}else if(algo instanceof LogisticRegression) {
			algostr += "glm.fit <- gml(Y ~ X, data = data, family = binomial)";
			 NameAlgo = "Logistic Regression";
		}


		int num;
		String val = "";
		if (validation.getStratification() instanceof CrossValidation) {
			num = validation.getStratification().getNumber();
			val += "train_control <- trainControl(method=\"cv\", number="+num+")\r\n";
			val += "scores <- train(data=data, trControl=train_control, method=\"nb\"\r\n)";
		}else if(validation.getStratification() instanceof TrainingTest) {
			num = validation.getStratification().getNumber();
			val += "sample <- sample.int(n = nrow(data), size = floor(.80*nrow(data)), replace = F)\r\n";
			val += "train <- data[sample, ]\r\n";
			val += "test  <- data[-sample, ]\r\n";
		}

		String metric ="";
		String affiche  ="";
		boolean writeInFile = false;
		for (ValidationMetric laMetric : metrics) {
			algostr += "cm <- confusionMatrix(data, train(data))\r\n";
			switch(laMetric.getLiteral()) {
			case "balanced_accuracy":
				//TODO
				metric +="balanceA <- cm$overall[['Balanced Accuracy']]"+"\r\n";
				affiche  +="print(balanceA)"+"\r\n";
				break;
			case "recall":
				//TODO
				metric +="recall <- cm$overall[['Recall']]"+"\r\n";
				affiche  +="print(recall)"+"\r\n";
				writeInFile = true;
				break;
			case "precision":
				//TODO
				metric +="precision <- cm$overall[['Precision']]"+"\r\n";
				affiche  +="print(precision)"+"\r\n";
				break;
			case "F1":
				//TODO
				metric +="f1 <- cm$overall[['F1']]"+"\r\n";
				affiche  +="print(f1)"+"\r\n";
				break;
			case "accuracy":
				//TODO
				metric +="accuracy <- cm$overall[['Accuracy']]"+"\r\n";
				affiche  +="print(accuracy)"+"\r\n";
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

		String rCode = rImport + csvReading + predictivestr +predictorstr  + algostr + val + metric +affiche;
		Long date = new Date().getTime();
		Files.write(rCode.getBytes(), new File("mml_R"+date+".R"));		
		
		long debut = System.currentTimeMillis();
		Process p = Runtime.getRuntime().exec("R mml_R"+date+".R");
		long fin = System.currentTimeMillis()-debut;
		BufferedReader in = new BufferedReader(new InputStreamReader(p.getInputStream()));
		
		if (writeInFile) {
			File myFile = new File("recall.csv");
			FileOutputStream oFile = new FileOutputStream(myFile, true);
			oFile.write((dataInput.getFilelocation() +";"+NameAlgo+";ScikitLearn;"+fin+";"+in.read()+"\n").getBytes());
			oFile.close();
			}

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
