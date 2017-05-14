package com.predictiveprocess.webservice.service;

import com.predictiveprocess.classification.Predictor;
import com.predictiveprocess.classification.enumeration.ClassificationType;
import org.springframework.stereotype.Service;
import weka.classifiers.AbstractClassifier;
import weka.classifiers.functions.SMO;
import weka.classifiers.functions.SMOreg;
import weka.classifiers.lazy.IBk;
import weka.classifiers.trees.J48;
import weka.classifiers.trees.REPTree;
import weka.classifiers.trees.RandomForest;
import weka.classifiers.trees.RandomTree;
import weka.core.Instance;
import weka.core.Instances;

/**
 * Created by Kerwin on 1/18/2017.
 */
@Service
public class ClassificationService {

    public static AbstractClassifier train(ClassificationType type, Instances instances){

        AbstractClassifier classifier = null;
        Predictor predictor = new Predictor();
        switch (type){
            case DECISION_TREE:
                classifier = predictor.trainDecisionTree(instances);
                break;
            case KNN:
                classifier = predictor.trainKnn(instances);
                break;
            case RANDOM_FOREST:
                classifier = predictor.trainRandomForest(instances);
                break;
            case REGRESSION:
                classifier = predictor.trainSmoRegression(instances);
                break;
            default:
                break;
        }
        return classifier;
    }

    public static Double predict(ClassificationType type, AbstractClassifier classifier, Instance instance) throws Exception{
        Double result = null;
        Predictor predictor = new Predictor();
        switch (type){
            case DECISION_TREE:
                result = predictor.makePredictionDecisionTree((REPTree) classifier, instance);
                break;
            case KNN:
                result = predictor.makePredictionKnn((IBk) classifier, instance);
                break;
            case RANDOM_FOREST:
                result = predictor.makePredictionRandomForest((RandomForest) classifier, instance);
                break;
            case REGRESSION:
                result = predictor.makePredictionSmoReg((SMO) classifier, instance);
                break;
            default:
                break;
        }

        return result;
    }
}
