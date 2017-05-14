package com.predictiveprocess.classification;

import weka.classifiers.Classifier;
import weka.classifiers.functions.LinearRegression;
import weka.classifiers.functions.SMO;
import weka.classifiers.functions.SMOreg;
import weka.classifiers.functions.supportVector.PolyKernel;
import weka.classifiers.functions.supportVector.RBFKernel;
import weka.classifiers.functions.supportVector.RegSMOImproved;
import weka.classifiers.lazy.IBk;
import weka.classifiers.trees.J48;
import weka.classifiers.trees.REPTree;
import weka.classifiers.trees.RandomForest;
import weka.classifiers.trees.RandomTree;
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

	public REPTree trainDecisionTree(Instances inst){
		REPTree tree = new REPTree();
		 try {
				Instances train = inst;
				train.setClassIndex(train.numAttributes() - 1);
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

	public Double makePredictionDecisionTree(REPTree tree, Instance instance) throws Exception {
		Map<String, String> results = new HashMap<String, String>();
		Double result = null;
		if(tree!=null){
			result = tree.classifyInstance(instance);
		}
		return result;
	}

	public IBk trainKnn(Instances inst){
		IBk ibk = new IBk();
		try {
			Instances train = inst;
			train.setClassIndex(train.numAttributes() - 1);
			ibk.buildClassifier(train);   // build classifier
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
		return ibk;
	}

	public Double makePredictionKnn(IBk ibk, Instance instance) throws Exception {
		Map<String, String> results = new HashMap<String, String>();
		Double result = null;
		if(ibk!=null){
			result = ibk.classifyInstance(instance);
		}
		return result;
	}

	public RandomForest trainRandomForest(Instances inst){
		RandomForest rf = new RandomForest();
		try {
			Instances train = inst;
			train.setClassIndex(train.numAttributes() - 1);
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

	public Double makePredictionRandomForest(RandomForest rf, Instance instance) throws Exception {
		Map<String, String> results = new HashMap<String, String>();
		Double result = null;
		if(rf!=null){
			result = rf.classifyInstance(instance);
		}

		return result;
	}

	public LinearRegression trainRegression(Instances inst){
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

	public Double makePredictionRegression(LinearRegression linearRegression, Instance instance) throws Exception {
		Map<String, String> results = new HashMap<String, String>();
		Double result = null;
		if(linearRegression!=null){
			result = linearRegression.classifyInstance(instance);
		}

		return result;
	}

	public SMO trainSmoRegression(Instances instances) {

		SMO smo = new SMO();

		instances.setClassIndex(instances.numAttributes() - 1);
		try {
			smo.buildClassifier(instances);
		} catch (Exception e){
			e.printStackTrace();
			System.out.println("Error in building the classifier");
		}

		return smo;
	}

	public Double makePredictionSmoReg(SMO smo, Instance instance) throws Exception {
		Map<String, String> results = new HashMap<String, String>();
		Double result = null;
		if(smo!=null){
			result = smo.classifyInstance(instance);
		}
		return result;
	}

}

