package com.predictiveprocess.webservice.service;

import com.predictiveprocess.classification.Predictor;
import com.predictiveprocess.classification.enumeration.ClassificationType;
import org.springframework.stereotype.Service;
import weka.classifiers.AbstractClassifier;
import weka.classifiers.functions.SMOreg;
import weka.classifiers.trees.J48;
import weka.classifiers.trees.RandomForest;
import weka.core.Instance;
import weka.core.Instances;

/**
 * Created by Kerwin on 1/18/2017.
 */
@Service
public class ClassificationService {

    public static AbstractClassifier train(ClassificationType type, Instances instances){

        AbstractClassifier classifier = null;
        switch (type){
            case DECISION_TREE:
                classifier = Predictor.trainDecisionTree(instances);
                break;
            case RANDOM_FOREST:
                classifier = Predictor.trainRandomForest(instances);
                break;
            case REGRESSION:
                classifier = Predictor.trainSmoRegression(instances,0.1, 1.0, 0.001);
                break;
            default:
                break;
        }
        return classifier;
    }

    public static Double predict(ClassificationType type, AbstractClassifier classifier, Instance instance) throws Exception{
        Double result = null;
        switch (type){
            case DECISION_TREE:
                result = Predictor.makePredictionDecisionTree((J48) classifier, instance);
                break;
            case RANDOM_FOREST:
                result = Predictor.makePredictionRandomForest((RandomForest) classifier, instance);
                break;
            case REGRESSION:
                result = Predictor.makePredictionSmoReg((SMOreg) classifier, instance);
                break;
            default:
                break;
        }

        return result;
    }
}
