package com.predictiveprocess.utility;

import weka.core.Instances;
import weka.core.json.JSONNode;

import java.util.List;

/**
 * Created by kerwin on 5/14/17.
 */
public class Results {
    private double accuracy;
    private String method;
    private List<double[]> results;

    public Results(double accuracy, String method, List<double[]>  results) {
        this.accuracy = accuracy;
        this.method = method;
        this.results = results;
    }

    public double getAccuracy() {
        return accuracy;
    }

    public void setAccuracy(double accuracy) {
        this.accuracy = accuracy;
    }

    public String getMethod() {
        return method;
    }

    public void setMethod(String method) {
        this.method = method;
    }

    public List<double[]> getResults() {
        return results;
    }

    public void setResults(List<double[]> results) {
        this.results = results;
    }
}
