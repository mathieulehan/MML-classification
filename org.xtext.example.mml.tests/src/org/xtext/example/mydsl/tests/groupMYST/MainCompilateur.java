package org.xtext.example.mydsl.tests.groupMYST;

import java.io.File;
import java.io.IOException;
import java.util.List;

import org.xtext.example.mydsl.mml.CSVParsingConfiguration;
import org.xtext.example.mydsl.mml.DataInput;
import org.xtext.example.mydsl.mml.FormulaItem;
import org.xtext.example.mydsl.mml.MLChoiceAlgorithm;
import org.xtext.example.mydsl.mml.MMLModel;
import org.xtext.example.mydsl.mml.Validation;
import org.xtext.example.mydsl.mml.XFormula;

public class MainCompilateur {
	
	private final String DEFAULT_COLUMN_SEPARATOR = ","; // by default
	private final String DEFAULT_COLUMN_PREDICTOR = "" ;
	private final String DEFAULT_COLUMN_PREDICTIVE = "" ;
	
	private MMLModel mmlModel;
	private DataInput dataInput;
	private CSVParsingConfiguration parsingInstruction ;
	private List<MLChoiceAlgorithm> AlgosList ;
	private Validation validation;
	private FormulaItem predictive;
	private XFormula predictor;
	private Compilateur compilateur;
	private File fileResultat;
	private String pathFile; 
	
	public MainCompilateur(MMLModel mmlModel, String PathFile) {
		this.mmlModel = mmlModel;
		
		//Récupère les informations globales
		if (mmlModel != null) {
		this.dataInput = mmlModel.getInput();
		this.parsingInstruction = dataInput.getParsingInstruction();
		this.AlgosList = mmlModel.getAlgorithms();
		this.validation = mmlModel.getValidation();
		this.predictive = mmlModel.getFormula().getPredictive();
		this.predictor = mmlModel.getFormula().getPredictors();
		this.pathFile = pathFile;
		}
		//Crée le fichier CSV de résultat
		//TODO
	}
	
	public void run() {
		for (MLChoiceAlgorithm alogs : AlgosList) 
		{ 
		
		switch(alogs.getFramework().getName()) {
		  case "R":
		    compilateur = new RCompilateur();
		    break;
		  case "Weca":
		     compilateur = new WekaCompilateur();
		    break;
		  case "xgboost":
			  compilateur = new XgboostCompilateur();		 
			    break;
		  case "scikit-learn":
			  compilateur = new ScikitLearnCompilateur();		 
			    break;
		}
		compilateur.configure(alogs.getAlgorithm(),dataInput,validation, getCSVSeparator(),  predictive, predictor,pathFile );
		try {
			compilateur.execute();
		} catch (IOException e) {
			e.printStackTrace();
		}
		}
	}
	
	
	private void writeIntoFile(String dataName, String algo, String frameWork, String execTime,String metricName, String result) {
		//TODO
	}
	
	public File getFile() {
		return this.fileResultat;
	}
	
	private String getNameData() {
		//TODO : must give the name of the dataSet
		return "";
	}

	private String getCSVSeparator() {
		if(parsingInstruction.getSep() == null) {
			return parsingInstruction.getSep().getName();
		}
		return DEFAULT_COLUMN_SEPARATOR;
	}
	

	
}
