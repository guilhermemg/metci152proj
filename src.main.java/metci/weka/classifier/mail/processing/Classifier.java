package metci.weka.classifier.mail.processing;

/**
 * A Java class that implements a simple text classifier, based on WEKA.
 * To be used with MyFilteredLearner.java.
 * WEKA is available at: http://www.cs.waikato.ac.nz/ml/weka/
 * Copyright (C) 2013 Jose Maria Gomez Hidalgo - http://www.esp.uem.es/jmgomez
 *
 * This program is free software: you can redistribute it and/or modify
 * it for any purpose.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.
 */

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileReader;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import weka.classifiers.meta.FilteredClassifier;
import weka.core.Attribute;
import weka.core.FastVector;
import weka.core.Instance;
import weka.core.Instances;

/**
 * This class implements a simple text classifier in Java using WEKA. It loads a
 * file with the text to classify, and the model that has been learnt with
 * MyFilteredLearner.java.
 * 
 * @author Jose Maria Gomez Hidalgo - http://www.esp.uem.es/jmgomez
 * @author Guilherme Gadelha
 * 
 * @see Learner
 */
public class Classifier {

	/**
	 * String that stores the text to classify
	 */
	String text;
	/**
	 * Object that stores the instance.
	 */
	Instances instances;
	/**
	 * Object that stores the classifier.
	 */
	FilteredClassifier classifier;
	private String verbose;
	
	public Classifier(String dataModelPath, String verbose) {
		loadModel(dataModelPath);
		this.verbose = verbose;
	}

	/**
	 * This method loads the text to be classified.
	 * 
	 * @param fileName
	 *            The name of the file that stores the text.
	 */
	public void load(String fileName) {
		try {
			BufferedReader reader = new BufferedReader(new FileReader(fileName));
			String line;
			text = "";
			while ((line = reader.readLine()) != null) {
				text = text + " " + line;
			}
//			System.out.println("===== Loaded text data: " + fileName + " =====");
			reader.close();
//			System.out.println(text);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	/**
	 * This method loads the model to be used as classifier.
	 * 
	 * @param fileName
	 *            The name of the file that stores the text.
	 */
	public void loadModel(String fileName) {
		try {
			ObjectInputStream in = new ObjectInputStream(new FileInputStream(fileName));
			Object tmp = in.readObject();
			classifier = (FilteredClassifier) tmp;
			in.close();
//			System.out.println("===== Loaded model: " + fileName + " =====");
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	/**
	 * This method creates the instance to be classified, from the text that has
	 * been read.
	 */
	public void makeInstance() {
		// Create the attributes, class and text
		FastVector fvNominalVal = new FastVector(2);
		fvNominalVal.addElement("spam");
		fvNominalVal.addElement("ham");
		Attribute attribute1 = new Attribute("class", fvNominalVal);
		Attribute attribute2 = new Attribute("text", (FastVector) null);
		// Create list of instances with one element
		FastVector fvWekaAttributes = new FastVector(2);
		fvWekaAttributes.addElement(attribute1);
		fvWekaAttributes.addElement(attribute2);
		instances = new Instances("Test relation", fvWekaAttributes, 1);
		// Set class index
		instances.setClassIndex(0);
		// Create and add the instance

		Instance instance = new Instance(2);

		// DenseInstance instance = new DenseInstance(2);

		instance.setValue(attribute2, text);
		// Another way to do it:
		// instance.setValue((Attribute)fvWekaAttributes.elementAt(1), text);
		instances.add(instance);
//		System.out.println("===== Instance created with reference dataset =====");
//		System.out.println(instances);
	}

	/**
	 * This method performs the classification of the instance. Output is done
	 * at the command-line.
	 * @return 
	 */
	public String classify() {
		try {
			double pred = classifier.classifyInstance(instances.instance(0));
//			System.out.println("===== Classified instance =====");
			return instances.classAttribute().value((int) pred);
		} catch (Exception e) {
			e.printStackTrace();
		}
		return "";
	}
	
	/**
	 * Classify emails as spams or not.
	 * 
	 * @param hamsTestDir
	 * @param spamsTestDir
	 * @return list with precision, recall, fmeasure and timeOfClassification
	 */
	public List<Double> classifyEmails(String hamsTestDir, String spamsTestDir) {
		double TP = 0;
		double FP = 0;
		double TN = 0;
		double FN = 0;
		
		try {
			List<File> filesInFolder = Files.walk(Paths.get(hamsTestDir)).filter(Files::isRegularFile).map(Path::toFile)
					.collect(Collectors.toList());
			
			int numHams = filesInFolder.size();
			
			for (File f : filesInFolder) {
				String fileName = f.getPath();
				load(fileName);
				makeInstance();
				String predValue = classify();
				if(verbose.equals("-v")) {
					System.out.println(fileName + ": " + predValue);
				}
				if(predValue.equals("ham")) {
					TN++;
				}
				if(predValue.equals("spam")) {
					FP++;
				}
			}
			
			List<File> filesInFolder2 = Files.walk(Paths.get(spamsTestDir)).filter(Files::isRegularFile).map(Path::toFile)
					.collect(Collectors.toList());
			int numSpams = filesInFolder2.size();
			
			for (File f : filesInFolder2) {
				String fileName = f.getPath();
				load(fileName);
				makeInstance();
				String predValue = classify();
				if(verbose.equals("-v")) {
					System.out.println(fileName + ": " + predValue);
				}
				if(predValue.equals("spam")) {
					TP++;
				}
				if(predValue.equals("ham")) {
					FN++;
				}
			}
			
			assert TP+FP+TN+FN == numHams+numSpams;

		} catch (Exception e) {
			e.printStackTrace();
		}
		
		double precision = TP/(TP+FP);
		double recall = TP/(TP+FN);
		double fmeasure = 2*precision*recall/(precision+recall);
		
		List<Double> list = new ArrayList<Double>();
		list.add(precision);
		list.add(recall);
		list.add(fmeasure);
		
		return list;
	}
	
//	/**
//	 * Main method. It is an example of the usage of this class.
//	 * 
//	 * @param args
//	 *            Command-line arguments: fileData and fileModel.
//	 */
//	public static void main(String[] args) {
//
//		Classifier classifier;
//
////		String testFile = "main/resources/data/teste/ham/5098.2001-12-05.farmer.ham.txt";
//		String testFile = "main/resources/data/teste/ham/1.";
////		String dataModel = "main/resources/model/smsspam.small.arff";
//		String dataModel = "main/resources/model/myClassifier.dat";
//		classifier = new Classifier();
//
//		classifier.loadModel(dataModel);
//		classifier.load(testFile);
//		classifier.makeInstance();
//		classifier.classify();
//		
//	}

}
