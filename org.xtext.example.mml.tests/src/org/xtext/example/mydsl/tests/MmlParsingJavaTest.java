package org.xtext.example.mydsl.tests;

import static org.junit.Assert.assertTrue;
import static org.junit.jupiter.api.Assertions.assertEquals;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;
import java.util.ListIterator;

import org.eclipse.emf.common.util.EList;
import org.eclipse.emf.ecore.resource.Resource;
import org.eclipse.xtext.testing.InjectWith;
import org.eclipse.xtext.testing.extensions.InjectionExtension;
import org.eclipse.xtext.testing.util.ParseHelper;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.xtext.example.mydsl.mml.CSVParsingConfiguration;
import org.xtext.example.mydsl.mml.DataInput;
import org.xtext.example.mydsl.mml.MMLModel;

import com.google.common.io.Files;
import com.google.inject.Inject;

import junit.framework.Assert;

@ExtendWith(InjectionExtension.class)
@InjectWith(MmlInjectorProvider.class)
public class MmlParsingJavaTest {

	@Inject
	ParseHelper<MMLModel> parseHelper;
	
	@Test
	public void loadModel() throws Exception {
		List<MMLModel> listResult = new ArrayList();
		MMLModel result = parseHelper.parse("datainput \"foo.csv\" mlframework scikit-learn\n"
				+ "algorithm DT\n"
				+ "CrossValidation { numRepetitionCross 70 }\n"
				+ "precision\n"
				+ "");
		listResult.add(result);
		MMLModel result1 = parseHelper.parse("datainput \"foo.csv\"\n"
				+ "mlframework scikit-learn\n"
				+ "algorithm DT\n"
				+ "TrainingTest { percentageTraining 10 }\n"
				+ "recall\n"
				+ "");
		listResult.add(result1);
		MMLModel result2 = parseHelper.parse("datainput \"foo.csv\"\n"
				+ "mlframework scikit-learn\n"
				+ "algorithm RF\n"
				+ "TrainingTest { percentageTraining 50 }\n"
				+ "recall\n"
				+ "");
		listResult.add(result2);
		MMLModel result3 = parseHelper.parse("datainput \"foo.csv\"\n"
				+ "mlframework scikit-learn\n"
				+ "algorithm DT\n"
				+ "TrainingTest { percentageTraining 70 }\n"
				+ "accuracy\n"
				+ "");
		listResult.add(result3);
		MMLModel result4 = parseHelper.parse("datainput \"foo.csv\"\n"
				+ "mlframework scikit-learn\n"
				+ "algorithm DT\n"
				+ "CrossValidation { percentageTraining 70 }\n"
				+ "accuracy\n"
				+ "");
		listResult.add(result4);
		MMLModel result5 = parseHelper.parse("datainput \"foo.csv\"\n"
				+ "mlframework scikit-learn\n"
				+ "algorithm DT\n"
				+ "CrossValidation { percentageTraining 70 }\n"
				+ "F1\n"
				+ "");
		listResult.add(result5);
		MMLModel result6 = parseHelper.parse("datainput \"foo.csv\"\n"
				+ "mlframework scikit-learn\n"
				+ "algorithm DT\n"
				+ "TrainingTest { percentageTraining 70 }\n"
				+ "F1\n"
				+ "");
		listResult.add(result6);
		MMLModel result7 = parseHelper.parse("datainput \"foo.csv\"\n"
				+ "mlframework scikit-learn\n"
				+ "algorithm DT\n"
				+ "TrainingTest { percentageTraining 10 }\n"
				+ "F1\n"
				+ "");
		listResult.add(result7);
		MMLModel result8 = parseHelper.parse("datainput \"foo.csv\"\n"
				+ "mlframework scikit-learn\n"
				+ "algorithm RF\n"
				+ "TrainingTest { percentageTraining 70 }\n"
				+ "F1\n"
				+ "");
		listResult.add(result8);
		MMLModel result9 = parseHelper.parse("datainput \"foo.csv\"\n"
				+ "mlframework scikit-learn\n"
				+ "algorithm RF\n"
				+ "CrossValidation { percentageTraining 10 }\n"
				+ "accuracy\n"
				+ "");
		listResult.add(result9);
		ListIterator<MMLModel> it = listResult.listIterator();
	    while(it.hasNext()){
	    	System.out.println(it.next());
	    	Assertions.assertNotNull(it.next());
	    	EList<Resource.Diagnostic> errors = it.next().eResource().getErrors();
			Assertions.assertTrue(errors.isEmpty(), "Unexpected errors");			
			Assertions.assertEquals("foo.csv", it.next().getInput().getFilelocation());
	    }
		
	}		
	
	@Test
	public void compileDataInput() throws Exception {
		List<MMLModel> models = new ArrayList<>();
		MMLModel result1 = parseHelper.parse("datainput \"foo2.csv\" separator ,\n"
				+ "mlframework scikit-learn\n"
				+ "algorithm DT\n"
				+ "CrossValidation { numRepetitionCross 70 }\n"
				+ "precision\n"
				+ "");
		MMLModel result2 = parseHelper.parse("datainput \"foo2.csv\" separator ,\n"
				+ "mlframework scikit-learn\n"
				+ "algorithm DT\n"
				+ "CrossValidation { numRepetitionCross 70 }\n"
				+ "precision\n"
				+ "");
		models.add(result1);
		models.add(result2);
		int indexModel = 1;
		
		for (MMLModel result : models) {
			DataInput dataInput = result.getInput();
			String fileLocation = dataInput.getFilelocation();
		
			
			String pythonImport = "import pandas as pd\n"; 
			String DEFAULT_COLUMN_SEPARATOR = ","; // by default
			String csv_separator = DEFAULT_COLUMN_SEPARATOR;
			CSVParsingConfiguration parsingInstruction = dataInput.getParsingInstruction();
			if (parsingInstruction != null) {			
				System.err.println("parsing instruction..." + parsingInstruction);
				csv_separator = parsingInstruction.getSep().toString();
			}
			String csvReading = "mml_data = pd.read_csv(" + mkValueInSingleQuote(fileLocation) + ", sep=" + mkValueInSingleQuote(csv_separator) + ")";						
			String pandasCode = pythonImport + csvReading;
			
			pandasCode += "\nprint (mml_data)\n"; 
			
			Files.write(pandasCode.getBytes(), new File("mml"+indexModel+".py"));
			// end of Python generation
			
			
			/*
			 * Calling generated Python script (basic solution through systems call)
			 * we assume that "python" is in the path
			 */
			try	{
				Process p = Runtime.getRuntime().exec("python mml"+indexModel+".py");
				BufferedReader in = new BufferedReader(new InputStreamReader(p.getInputStream()));
				String line; 
				int index = 0;
				while ((line = in.readLine()) != null) {
					System.out.println(line);
					assertEquals(false, line.isEmpty());
					switch (index) {
					case 0:
						assertEquals("    colonne 1     colonne 2", line);
						break;
					case 1:
						assertEquals("0  cellule 1 1  cellule 1 2", line);
						break;
					case 2:
						assertEquals("1  cellule 2 1  cellule 2 2", line);
						break;
					default:
						Assert.fail("Error while reading mml"+indexModel+" results");
						break;
					}
					index++;
			    }
			}
			catch (IOException e) {
				Assert.fail("Error during mml"+indexModel+" execution");
			}
			indexModel++;
		}
	}
	
	private String mkValueInSingleQuote(String val) {
		return "'" + val + "'";
	}


}