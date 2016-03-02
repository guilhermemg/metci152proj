package metci.weka.classifier;

import metci.weka.classifier.mail.preprocessing.ARFFCreator;
import metci.weka.classifier.mail.processing.Classifier;
import metci.weka.classifier.mail.processing.ModelCreator;

public class Main {
	
	public static void main(String[] args) {
		
		String classificationAlgo = "";
		String trainDir = "";
		String testDir = "";
		String verbose = "";
		
		if(args.length >= 3) {
			classificationAlgo = args[0];
			trainDir = args[1];
			testDir = args[2];
			if(args.length == 4) {
				verbose = args[0];
				classificationAlgo = args[1];
				trainDir = args[2];
				testDir = args[3];
			}
		}
		else {
			throw new IllegalArgumentException();
		}
		
//		String hamsDir = "main/resources/data/treino/ham/"; 
//		String spamsDir = "main/resources/data/treino/spam/";
		String hamsDir = trainDir + "/ham/"; 
		String spamsDir = testDir + "/spam/";
		
		try {
			
			// preprocessing -------------------------------------------------
			
			ARFFCreator arffCreator = new ARFFCreator();
			arffCreator.writeARFFFileFromFolder(hamsDir, spamsDir);
			
			
			// training -----------------------------------------------------
			
			ModelCreator modelCreator = new ModelCreator(classificationAlgo);
			String dataset = ARFFCreator.OUTPUT_DATASET_PATH;
			String modelCreatedPath = ModelCreator.MODEL_CREATED_PATH;
			
			modelCreator.loadDataset(dataset);
			modelCreator.evaluate();
			modelCreator.learn();
			modelCreator.saveModel(modelCreatedPath);
			
			
			// classification ----------------------------------------------------
			
			String dataModelPath = modelCreatedPath;

			Classifier classifier = new Classifier(dataModelPath, verbose);
			
//			String testsDir1 = "main/resources/data/teste/ham";
//			String testsDir2 = "main/resources/data/teste/spam";
			String testsDir1 = testDir + "/ham";
			String testsDir2 = testDir + "/spam";
			
			classifier.classifyAll(testsDir1);
			classifier.classifyAll(testsDir2);
			
		}
		catch(Exception e) {
			e.printStackTrace();
		}
		
	}
}
