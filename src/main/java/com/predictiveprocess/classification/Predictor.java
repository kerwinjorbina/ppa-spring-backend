package com.predictiveprocess.classification;

import weka.classifiers.functions.LinearRegression;
import weka.classifiers.functions.SMOreg;
import weka.classifiers.functions.supportVector.PolyKernel;
import weka.classifiers.functions.supportVector.RBFKernel;
import weka.classifiers.functions.supportVector.RegSMOImproved;
import weka.classifiers.trees.J48;
import weka.classifiers.trees.RandomForest;
import weka.core.Instance;
import weka.core.Instances;
import weka.core.SelectedTag;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;


public class Predictor {

	/**
	 *
	 *
	 * DECISION TREE
	 *
	 *
	 *
	 */

	public static J48 trainDecisionTree(Instances inst){
		J48 tree = new J48();
		 try {

				Instances train = inst;

				train.setClassIndex(train.numAttributes() - 1);

//				String[] options = new String[3];
//				options[0] = "-p";
//				options[1] = "-C";
//				options[2] = "0.25";
//
//				tree.setOptions(options);
				tree.buildClassifier(train);   // build classifier

			} catch (FileNotFoundException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (Exception e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}

		 return tree;

	}

	public static Double makePredictionDecisionTree(J48 tree, Instance instance) throws Exception {
		Map<String, String> results = new HashMap<String, String>();
		Double result = null;
		if(tree!=null){
			result = tree.classifyInstance(instance);
		}

		return result;
	}

	public static RandomForest trainRandomForest(Instances inst){
		RandomForest rf = new RandomForest();
		try {

			Instances train = inst;

			train.setClassIndex(train.numAttributes() - 1);

//				String[] options = new String[3];
//				options[0] = "-p";
//				options[1] = "-C";
//				options[2] = "0.25";
//
//				tree.setOptions(options);
			rf.buildClassifier(train);   // build classifier

		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		return rf;

	}

	public static Double makePredictionRandomForest(RandomForest rf, Instance instance) throws Exception {
		Map<String, String> results = new HashMap<String, String>();
		Double result = null;
		if(rf!=null){
			result = rf.classifyInstance(instance);
		}

		return result;
	}

	public static LinearRegression trainRegression(Instances inst){
		LinearRegression linearRegression = new LinearRegression();
		try {

			Instances train = inst;

			train.setClassIndex(train.numAttributes() - 1);
			linearRegression.buildClassifier(train);   // build classifier

		} catch (Exception e) {
			e.printStackTrace();
		}

		return linearRegression;

	}

	public static Double makePredictionRegression(LinearRegression linearRegression, Instance instance) throws Exception {
		Map<String, String> results = new HashMap<String, String>();
		Double result = null;
		if(linearRegression!=null){
			result = linearRegression.classifyInstance(instance);
		}

		return result;
	}

	public static SMOreg trainSmoRegression(Instances instances, double kernelMainParam, double cParam, double epsilon) {

		SMOreg regressor = new SMOreg();

		// DEFAULT
		RegSMOImproved optimizer = new RegSMOImproved();
		optimizer.setTolerance(0.001);
		optimizer.setEpsilon(1.0e-12);

		regressor.setRegOptimizer(optimizer);
		SelectedTag filterTag = new SelectedTag(SMOreg.FILTER_NORMALIZE, SMOreg.TAGS_FILTER); // Default value FILTER_NORMALIZE
		regressor.setFilterType(filterTag);

		optimizer.setEpsilonParameter(epsilon);

		int kernelParam = 1; // RBF
		try {
			regressor.setC(cParam);

			if (kernelParam == 0) {
				PolyKernel kernel = new PolyKernel();
				kernel.setCacheSize(-1);
				kernel.setExponent(kernelMainParam);

				regressor.setKernel(kernel);
			} else {
				RBFKernel kernel = new RBFKernel();
				kernel.setCacheSize(-1);
				kernel.setGamma(kernelMainParam);

				regressor.setKernel(kernel);
			}

		} catch (Exception e1) {
			e1.printStackTrace();
		}

		instances.setClassIndex(instances.numAttributes() - 1);
		try {
			regressor.buildClassifier(instances);
		} catch (Exception e){
			e.printStackTrace();
			System.out.println("Error in building the classifier");
		}

		return regressor;
	}

	public static Double makePredictionSmoReg(SMOreg smoReg, Instance instance) throws Exception {
		Map<String, String> results = new HashMap<String, String>();
		Double result = null;
		if(smoReg!=null){
			result = smoReg.classifyInstance(instance);
		}
		return result;
	}

}

