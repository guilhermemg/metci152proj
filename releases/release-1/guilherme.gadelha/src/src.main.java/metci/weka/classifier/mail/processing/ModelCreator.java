package metci.weka.classifier.mail.processing;


import java.io.BufferedReader;
import java.io.File;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.IOException;
import java.io.ObjectOutputStream;
import java.util.Random;

import weka.classifiers.Evaluation;
import weka.classifiers.bayes.NaiveBayes;
import weka.classifiers.meta.FilteredClassifier;
import weka.classifiers.trees.RandomTree;

/**
 * A Java class that implements a simple text learner, based on WEKA.
 * To be used with MyFilteredClassifier.java.
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

import weka.core.Instances;
import weka.core.converters.ArffLoader.ArffReader;
import weka.filters.unsupervised.attribute.StringToWordVector;

/**
 * This class implements a simple text learner in Java using WEKA.
 * It loads a text dataset written in ARFF format, evaluates a classifier on it,
 * and saves the learnt model for further use.
 * 
 * @author Jose Maria Gomez Hidalgo - http://www.esp.uem.es/jmgomez
 * @author Guilherme Gadelha
 * 
 * @see MyFilteredClassifier
 */
public class ModelCreator {
	
	public final static String MODEL_CREATED_PATH = "/tmp/model_created.dat";
	
	/**
	 * Object that stores training data.
	 */
	Instances trainData;
	/**
	 * Object that stores the filter
	 */
	StringToWordVector filter;
	/**
	 * Object that stores the classifier
	 */
	FilteredClassifier classifier;

	private String classificationAlgo;
		
	public ModelCreator(String classificationAlgo) {
		this.classificationAlgo = classificationAlgo;
	}

	/**
	 * This method loads a dataset in ARFF format. If the file does not exist, or
	 * it has a wrong format, the attribute trainData is null.
	 * @param fileName The name of the file that stores the dataset.
	 */
	public void loadDataset(String fileName) {
		try {
			BufferedReader reader = new BufferedReader(new FileReader(fileName));
			ArffReader arff = new ArffReader(reader);
			trainData = arff.getData();
//			System.out.println("===== Loaded dataset: " + fileName + " =====");
			reader.close();
		}
		catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	/**
	 * This method evaluates the classifier. As recommended by WEKA documentation,
	 * the classifier is defined but not trained yet. Evaluation of previously
	 * trained classifiers can lead to unexpected results.
	 */
	public void evaluate() {
		try {
			trainData.setClassIndex(0);
			filter = new StringToWordVector();
			filter.setAttributeIndices("last");
			classifier = new FilteredClassifier();
			classifier.setFilter(filter);

			if(this.classificationAlgo.equals("RandomTree")) {
				classifier.setClassifier(new RandomTree());
			}
			if(this.classificationAlgo.equals("NaiveBayes")) {
				classifier.setClassifier(new NaiveBayes());
			}
			
			Evaluation eval = new Evaluation(trainData);
			eval.crossValidateModel(classifier, trainData, 4, new Random(1));
			
			System.out.println("-------------------------------------------");
			
			System.out.println("Precision (Hams): " + eval.precision(0));
			System.out.println("Precision (Spams): " + eval.precision(1));
			
			System.out.println("Recall (Hams): " + eval.recall(0));
			System.out.println("Recall (Spams): " + eval.recall(1));
			
			System.out.println("F-Measure (Hams): " + eval.fMeasure(0)); // 0 ==> ham, 1 ==> spam
			System.out.println("F-Measure (Spams): " + eval.fMeasure(1)); // 0 ==> ham, 1 ==> spam
			
			System.out.println("-------------------------------------------");
//			System.out.println(eval.toSummaryString());
//			System.out.println(eval.toClassDetailsString());
			
//			System.out.println("===== Evaluating on filtered (training) dataset done =====");
		}
		catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	/**
	 * This method trains the classifier on the loaded dataset.
	 */
	public void learn() {
		try {
			trainData.setClassIndex(0);
			filter = new StringToWordVector();
			filter.setAttributeIndices("last");
			classifier = new FilteredClassifier();
			classifier.setFilter(filter);
			classifier.setClassifier(new NaiveBayes());
			classifier.buildClassifier(trainData);
			
			// Uncomment to see the classifier
			// System.out.println(classifier);
//			System.out.println("===== Training on filtered (training) dataset done =====");
		}
		catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	/**
	 * This method saves the trained model into a file. This is done by
	 * simple serialization of the classifier object.
	 * @param fileName The name of the file that will store the trained model.
	 */
	public void saveModel(String fileName) {
		try {
			File file = new File(fileName);
			if (!file.exists()) {
				file.createNewFile();
			}
			else {
				file.delete();
				file.createNewFile();
			}
			
            ObjectOutputStream out = new ObjectOutputStream(new FileOutputStream(fileName));
            out.writeObject(classifier);
            out.close();
// 			System.out.println("===== Saved model: " + fileName + " =====");
        } 
		catch (IOException e) {
			e.printStackTrace();
		}
	}
	
//	/**
//	 * Main method. It is an example of the usage of this class.
//	 * @param args Command-line arguments: fileData and fileModel.
//	 */
//	public static void main (String[] args) {
//	
//		ModelCreator learner;
////		if (args.length < 2)
////			System.out.println("Usage: java MyLearner <fileData> <fileModel>");
////		else {
//			learner = new ModelCreator();
////			String dataset = "main/resources/model/smsspam.small.arff";
//			String dataset = "main/resources/model/testmodel.arff";
//			String classifierDat = "main/resources/model/myClassifier.dat";
//			
//			learner.loadDataset(dataset);
//			// Evaluation mus be done before training
//			// More info in: http://weka.wikispaces.com/Use+WEKA+in+your+Java+code
//			
//			learner.evaluate();
//			
//			learner.learn();
//			
//			learner.saveModel(classifierDat);
////		}
//	}
}	