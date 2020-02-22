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
		String csvReading = "data <- read.csv(\""+dataInput.getFilelocation()+"\", header = TRUE, sep = \""+separator+"\")\r\n";

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
			predictivestr = "X <- subset(data, select=-" + columnToPredictName + ""+"\r\n";
		}
		
		String predictorstr ="Y <- " + columnToPredictName + "\r\n";

		String NameAlgo ="";
		String algostr ="";
		String trainingModel = "";
		if(algo instanceof SVM) {
			SVM svm = (SVM)algo;
			NameAlgo = "SVM";
			String kernel = svm.getKernel().getName();
			String cost = svm.getC();
			svm.getGamma();
			
			if (svm.isClassificationSpecified()) {
				algostr += "install.packages(\"e1071\")\r\n";
				algostr += "library(e1071)\r\n";
				SVMClassification classification = svm.getSvmclassification();
				algostr += "x_test <- subset(data, select=-"+columnToPredictName+")";
				algostr += "y_test <- "+columnToPredictName+"";
				switch (classification.getLiteral()) {
				case "C-classification":
					algostr += "svm_model <- svm(y_test ~ ., data = x_test, kernel = \""+ kernel +"\", cost = "+ cost +", scale = FALSE, type =\"C-classification\")\r\n";
					algostr += "predictions <- predict(svm_model, x_test)\r\n";
					algostr += "table(predictions, y_test)\r\n";
					break;
				case "nu-classification":
					algostr += "svm_model <- svm(y_test ~ ., data = x_test, kernel = \""+ kernel +"\", cost = "+ cost +", scale = FALSE, type =\"nu-classification\")\r\n";
					algostr += "predictions <- predict(svm_model, x_test)\r\n";
					algostr += "table(predictions, y_test)\r\n";
					break;	
				case "one-classification":
					//TODO : impossible d'afficher des stats avec confusionMatrix pour ce svm
					algostr += "svm_model <- svm(y_test ~ ., data = x_test, kernel = \""+ kernel +"\", cost = "+ cost +", scale = FALSE, type =\"one-classification\")\r\n";
					algostr += "predictions <- predict(svm_model, x_test)\r\n";
					algostr += "table(predictions, y_test)\r\n";
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
			algostr += "x_test <- test[,1:ncol(test)-1]\r\n";
			algostr += "y_test <-test[,ncol(test)]\r\n";
			algostr += "predictions <- predict(model, x_test)\r\n";
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
			algostr += "cm <- confusionMatrix(predcictions, y_test, mode=\"prec_recall\")\r\n";
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
