package org.xtext.example.mydsl.tests.groupMYST;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileOutputStream;
import java.io.FileReader;
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
		String columnToPredictName = "";
		rImport += "install.packages(\"caret\")\r\n";
		rImport += "library(rpart)\r\n";
		rImport += "library(caret)\r\n";
		String csvReading = "data <- read.csv(\""+dataInput.getFilelocation()+"\", header = TRUE, sep = \""+separator+"\")\r\nattach(data)\r\n";

        BufferedReader br = new BufferedReader(new FileReader(dataInput.getFilelocation()));
        String header = br.readLine();
        if (header != null) {
            String[] columns = header.split(separator);
            columnToPredictName = columns[columns.length-1].trim();
        }
		
		String predictivestr = "";
		if (predictive!= null ) {
			predictivestr = "X <- data$"+predictive.getColName()+"\r\n";
		}else {
			//dernière colonne predictive.getColumn()
			predictivestr = "X <- subset(data, select=-" + columnToPredictName + ")\r\n";
			predictivestr = predictivestr.replace("\"", "");
		}
		
		String predictorstr ="Y <- " + columnToPredictName + "\r\n";
		predictorstr = predictorstr.replace("\"", "");

		String NameAlgo ="";
		String algostr ="";
		String trainingModel = "";
		if(algo instanceof SVM) {
			SVM svm = (SVM)algo;
			NameAlgo = "SVM";
			String kernel = svm.getKernel().getName();
			String cost = svm.getC();
			String gamma = svm.getGamma();

			if (svm.isClassificationSpecified()) {
				algostr += "install.packages(\"e1071\")\r\n";
				algostr += "library(e1071)\r\n";
				SVMClassification classification = svm.getSvmclassification();
				switch (classification.getLiteral()) {
				case "C-classification":
					algostr += "svm_model <- svm(Y ~ ., data = X, kernel = \""+ kernel +"\", cost = "+ cost +", gamma = "+ gamma +", scale = FALSE, type =\"C-classification\")\r\n";
					algostr += "predictions <- predict(svm_model, X)\r\n";
					algostr += "table(predictions, Y)\r\n";
					break;
				case "nu-classification":
					algostr += "svm_model <- svm(Y ~ ., data = X, kernel = \""+ kernel +"\", cost = "+ cost +", gamma = "+ gamma +", scale = FALSE, type =\"nu-classification\")\r\n";
					algostr += "predictions <- predict(svm_model, X)\r\n";
					algostr += "table(predictions, Y)\r\n";
					break;	
				case "one-classification":
					//TODO : impossible d'afficher des stats avec confusionMatrix pour ce svm
					algostr += "svm_model <- svm(Y ~ ., data = X, kernel = \""+ kernel +"\", cost = "+ cost +", gamma = "+ gamma +", scale = FALSE, type =\"one-classification\")\r\n";
					algostr += "predictions <- predict(svm_model, X)\r\n";
					algostr += "table(predictions, Y)\r\n";
					break;
				}
			}
		}else if(algo instanceof DT) {
			trainingModel += "rpart";
			NameAlgo = "Decision Tree";
		}else if(algo instanceof RandomForest ) {
			algostr += "install.packages(\"randomForest\")\r\n";
			algostr += "library(randomForest)\r\n";
			trainingModel += "rf";
			NameAlgo = "Random Forest";
		}else if(algo instanceof LogisticRegression) {
			algostr += "install.packages(\"caTools\")\r\n";
			algostr += "library(caTools)\r\n";
			trainingModel += "LogitBoost";
			NameAlgo = "Logistic Regression";
		}


		int num;
		String val = "";
		if (validation.getStratification() instanceof CrossValidation) {
			algostr += "set.seed(123)\r\n";
			algostr += "sample <- createDataPartition(data$variety, p = .8, list = FALSE)\r\n";
			algostr += "train <- data[sample, ]\r\n";
			algostr += "test  <- data[-sample, ]\r\n";
			num = validation.getStratification().getNumber();
			algostr += "train_control <- trainControl(method=\"cv\", number="+num+")\r\n";
			algostr += "model <- train(" + columnToPredictName + "~ ., data=train, trControl=train_control, method=\""+trainingModel+"\")\r\n";
			algostr += "X <- test[,1:ncol(test)-1]\r\n";
			algostr += "Y <-test[,ncol(test)]\r\n";
			algostr += "predictions <- predict(model, X)\r\n";
		}else if(validation.getStratification() instanceof TrainingTest) {
			/*num = validation.getStratification().getNumber();
			algostr += "set.seed(123)\r\n";
			algostr += "sample <- createDataPartition(data$variety, p = .8, list = FALSE)\r\n";
			algostr += "train <- data[sample, ]\r\n";
			algostr += "test  <- data[-sample, ]\r\n";
			algostr += "training <- train(unlist(data$"+ columnToPredictName +") ~ ., data = train,na.action = na.omit)\r\n";*/
		}

		String metric ="";
		String affiche  ="";
		boolean writeInFile = false;
		for (ValidationMetric laMetric : metrics) {
			algostr += "cm <- confusionMatrix(predictions, Y, mode=\"prec_recall\")\r\n";
			algostr += "byClass <- cm$byClass\r\n";
			switch(laMetric.getLiteral()) {
			case "balanced_accuracy":
				//TODO
				metric +="balancedA <- byClass[TRUE,c(\"Balanced Accuracy\")]"+"\r\n";
				affiche  +="print(balanceA)"+"\r\n";
				writeInFile = true;
				break;
			case "recall":
				//TODO
				metric +="recall <- byClass[TRUE,c(\"Recall\")]\r\n";
				affiche  +="print(recall)"+"\r\n";
				writeInFile = true;
				break;
			case "precision":
				//TODO
				metric +="precision <- byClass[TRUE,c(\"Precision\")]"+"\r\n";
				affiche  +="print(precision)"+"\r\n";
				writeInFile = true;
				break;
			case "F1":
				//TODO
				metric +="F1 <- byClass[TRUE,c(\"F1\")]\r\n";
				affiche  +="print(f1)"+"\r\n";
				writeInFile = true;
				break;
			case "accuracy":
				metric +="accuracy <- cm$overall[['Accuracy']]"+"\r\n";
				affiche  +="print(accuracy)"+"\r\n";
				writeInFile = true;
				break;
			case "macro_recall":
				//TODO
				metric +="macroRecall <- mean(byClass[TRUE,c(\"Recall\")])"+"\r\n";
				affiche  +="print(macroRecall)"+"\r\n";
				break;
			case "macro_precision":
				//TODO
				metric +="macroPrecision <- mean(byClass[TRUE,c(\"Precision\")])"+"\r\n";
				affiche  +="print(macroPrecision)"+"\r\n";
				break;
			case "macro_F1":
				//TODO
				metric +="macroF1 <- mean(byClass[TRUE,c(\"F1\")])"+"\r\n";
				affiche  +="print(macroF1)"+"\r\n";
				break;
			case "macro_accuracy":
				//TODO
				metric +="macroAccuracy <- mean(byClass[TRUE,c(\"Balanced Accuracy\")])"+"\r\n";
				affiche  +="print(macroAccuracy)"+"\r\n";
				break;
			}

		}

		String rCode = rImport + csvReading + predictivestr +predictorstr  + algostr + val + metric +affiche;
		Long date = new Date().getTime();
		File rFile = new File("mml_R"+date+".R");
		Files.write(rCode.getBytes(), rFile);		
		String rFilePath = rFile.getAbsolutePath();
		System.out.println(rFilePath);
		long debut = System.currentTimeMillis();
		Process p = Runtime.getRuntime().exec("Rscript.exe --no-save "+rFilePath+"");
		System.out.println();
		long fin = System.currentTimeMillis()-debut;
		BufferedReader in = new BufferedReader(new InputStreamReader(p.getInputStream()));
		
		String line; 
		String recall ="";
		while ((line = in.readLine()) != null) {
			recall = line;
			System.out.println(recall);
		}
		BufferedReader inError = new BufferedReader(new InputStreamReader(p.getErrorStream()));
		
		
	
		while ((line = inError.readLine()) != null) {
			System.out.println(line);
		}
			
		if (writeInFile) {
			File myFile = new File("recall.csv");
			FileOutputStream oFile = new FileOutputStream(myFile, true);
			oFile.write((dataInput.getFilelocation() +";"+NameAlgo+";R;"+fin+";"+in.read()+"\n").getBytes());
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
