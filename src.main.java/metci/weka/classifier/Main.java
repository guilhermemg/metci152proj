package metci.weka.classifier;

import java.io.File;
import java.io.FileWriter;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import metci.weka.classifier.mail.preprocessing.ARFFCreator;
import metci.weka.classifier.mail.processing.Classifier;
import metci.weka.classifier.mail.processing.ModelCreator;

public class Main {
	
	public static void main(String[] args) {
		
		String reportFilePath = "/tmp/report.csv";
		
		String classificationAlgo = "";
		String trainDir = "";
		String testDir = "";
		String verbose = "";
		String trainPercent = "";
		String emailSize = "";
		
		if(args.length >= 5) {
			classificationAlgo = args[0];
			trainDir = args[1];
			testDir = args[2];
			trainPercent = args[3];
			emailSize = args[4];
			
			if(args.length == 6) {
				verbose = args[0];
				classificationAlgo = args[1];
				trainDir = args[2];
				testDir = args[3];
				trainPercent = args[4];
				emailSize = args[5];
			}
		}
		else {
			throw new IllegalArgumentException();
		}
		
		System.out.println("====================================================");
		System.out.println("INIT EXEC");
		List<String> argsList = new ArrayList<String>();
		argsList.add(args[0]);
		argsList.add(args[1]);
		argsList.add(args[2]);
		argsList.add(args[3]);
		argsList.add(args[4]);
		System.out.println("ARGS: " + argsList);
		
//		String hamsDir = "main/resources/data/treino/ham/"; 
//		String spamsDir = "main/resources/data/treino/spam/";
		String hamsDir = trainDir + "/ham/"; 
		String spamsDir = testDir + "/spam/";
		
		try {
			
			// preprocessing -------------------------------------------------
			
			ARFFCreator arffCreator = new ARFFCreator();
			arffCreator.writeARFFFileFromFolder(hamsDir, spamsDir, Integer.parseInt(trainPercent), Integer.parseInt(emailSize));
			
			
			// training -----------------------------------------------------
			
			ModelCreator modelCreator = new ModelCreator(classificationAlgo);
			String dataset = ARFFCreator.OUTPUT_DATASET_PATH;
			String modelCreatedPath = ModelCreator.MODEL_CREATED_PATH;
			
			modelCreator.loadDataset(dataset);
			List<Double> result = modelCreator.evaluate();
			System.out.println("Result1: " + result.toString());
			modelCreator.learn();
			modelCreator.saveModel(modelCreatedPath);
			
			
			// classification ----------------------------------------------------
			
			String dataModelPath = modelCreatedPath;

			Classifier classifier = new Classifier(dataModelPath, verbose);
			
//			String testsDir1 = "main/resources/data/teste/ham";
//			String testsDir2 = "main/resources/data/teste/spam";
			String hamsTestDir = testDir + "/ham";
			String spamsTestDir = testDir + "/spam";
			
			Date date = new Date();
			long before = date.getTime();
			
			// result: {precision, recall, fmeasure, time}
			List<Double> result2 = classifier.classifyEmails(hamsTestDir, spamsTestDir);
			System.out.println("Result2: " + result2.toString());
			
			Date date2 = new Date();
			long after = date2.getTime();
			
			long processingTimeInMiliseconds = after - before;
			System.out.println("processing time: " + processingTimeInMiliseconds + " ms");
			
			// write results as one string line to report.csv
			boolean flag = false;
			File reportFile = new File(reportFilePath);
			if (!reportFile.exists()) {
				reportFile.createNewFile();
				flag = true;
			}
			
			FileWriter reportFileWriter = new FileWriter(reportFilePath, true);
			if(flag) {
				reportFileWriter.write("Algoritmo,TME,PD,precision,recall,fmeasure,time\n");
			}

			String resultingString = classificationAlgo + "," + emailSize + "," + trainPercent + "," + result.get(0) + "," + result.get(1) + "," + result.get(2) + "," + processingTimeInMiliseconds + "\n";
			reportFileWriter.write(resultingString);
			reportFileWriter.close();
		}
		catch(Exception e) {
			e.printStackTrace();
		}
		
		System.out.println("END EXEC");
		System.out.println("====================================================");
		
	}
}
