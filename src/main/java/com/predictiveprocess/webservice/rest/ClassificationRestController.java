package com.predictiveprocess.webservice.rest;

import com.predictiveprocess.classification.enumeration.ClassificationType;
import com.predictiveprocess.utility.InstancesSplitter;
import com.predictiveprocess.utility.Results;
import com.predictiveprocess.webservice.service.ClassificationService;
import org.apache.commons.io.FileUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import weka.classifiers.AbstractClassifier;
import weka.classifiers.lazy.IBk;
import weka.classifiers.trees.REPTree;
import weka.classifiers.trees.RandomForest;
import weka.core.Attribute;
import weka.core.Instance;
import weka.core.Instances;
import weka.core.converters.CSVSaver;
import weka.core.converters.ConverterUtils;
import weka.filters.Filter;
import weka.filters.unsupervised.attribute.NumericToNominal;

import java.io.*;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import static org.springframework.http.HttpHeaders.USER_AGENT;
import static org.springframework.web.bind.annotation.RequestMethod.GET;

/**
 * Created by Kerwin on 1/18/2017.
 */
@CrossOrigin
@RestController
@RequestMapping("/api/classify")
public class ClassificationRestController {
    @Autowired
    ClassificationService classificationService;

    public static Instances getData(String name) {
        Instances data = null;
        try {
            data = new Instances(new BufferedReader(new InputStreamReader(
                    new FileInputStream(name))));
        } catch (Exception ex) {
            ex.printStackTrace();
        }
        return data;
    }

    public String readFile(String file){
        String filename = System.getProperty("user.dir")+file;
        File f = new File(filename);
        if(!(f.exists() && !f.isDirectory())) {
            System.out.println("file does not exist");
            return null;
        }
        return filename;
    }

    private void saveResults(String encodedFile, int prefixLength, Instances predictionResults, String method) throws IOException {
        CSVSaver saver = new CSVSaver();
        saver.setInstances(predictionResults);
        String filename = "./results/"+encodedFile+prefixLength+method+".csv";
        saver.setFile(new File(filename));
        saver.writeBatch();
    }

    private String sendGet(String file, int index) throws Exception {

        String url = "http://localhost:8000/encoding/read?log="+file+"&index="+index;

        URL obj = new URL(url);
        HttpURLConnection con = (HttpURLConnection) obj.openConnection();

        con.setRequestMethod("GET");
        con.setRequestProperty("User-Agent", USER_AGENT);

        BufferedReader in = new BufferedReader(
                new InputStreamReader(con.getInputStream()));
        String inputLine;
        StringBuffer response = new StringBuffer();

        while ((inputLine = in.readLine()) != null) {
            response.append(inputLine+"\n");
        }
        in.close();
        String filename = "encodedfiles/"+file+index+".csv";
        FileUtils.writeStringToFile(new File(filename), response.toString());
        return filename;

    }

    private Instances convertToNominal(Instances instances) throws Exception {
        NumericToNominal convert= new NumericToNominal();

        String[] options= new String[2];
        options[0]="-R";
        options[1]="1-2";  //range of variables to make numeric

        convert.setOptions(options);
        convert.setInputFormat(instances);

        Instances newData= Filter.useFilter(instances, convert);
        return newData;
    }

    private Results evaluateResults(Instances testInstances, AbstractClassifier classifier, int prefixLength, String encodedFile, String method, ClassificationType type){
        try{
            testInstances.setClassIndex(testInstances.numAttributes() - 1);

            if(testInstances.size() == 0)
                return null;

            int correctPredictions = 0;
            Instances predictionResults = new Instances(testInstances);
            Attribute attribute = new Attribute("results");
            predictionResults.insertAttributeAt(attribute, predictionResults.numAttributes());
            int i = 0;

            List<double[]> resultValues = new LinkedList<>();
            for(Instance instance : testInstances){
                Double val = instance.value(instance.numAttributes()-1);
                Double res = classificationService.predict(type, classifier, instance);
                System.out.println("actual = "+val+ " prediction = "+res);
                if(val.doubleValue() == res.doubleValue())
                    correctPredictions++;

                predictionResults.instance(i).setValue(predictionResults.numAttributes()-1, res);
                resultValues.add(predictionResults.instance(i).toDoubleArray());
            }
            saveResults(encodedFile, prefixLength, predictionResults, method);
            String results = "number of correct predictions = "+ correctPredictions;
            results += "\nnumber of instances tested = "+ testInstances.size();
            results += "\naccuracy = "+ 100*correctPredictions/testInstances.size();
            System.out.print(results);
            return new Results(100*correctPredictions/testInstances.size(), method, resultValues);
        } catch (Exception e){
            return null;
        }

    }

    @RequestMapping(method = GET, path = "/dt")
    public Results dtClassify(@RequestParam int prefixLength, @RequestParam String encodedFile) throws Exception{
        ;
        //read csv file
        try{
            //split the log
            ConverterUtils.DataSource source = new ConverterUtils.DataSource(sendGet(encodedFile ,prefixLength));
            Instances instances = source.getDataSet();
            //split dataset to train and test
            instances = convertToNominal(instances);
            Map<String, Instances> instancesMap = InstancesSplitter.split(instances);

            Instances train = instancesMap.get("train");
            train.setClassIndex(instances.numAttributes() - 1);

            REPTree dt = (REPTree) classificationService.train(ClassificationType.DECISION_TREE, train);
            System.out.println("Decision Tree RepTree Testing");

            Instances testInstances = instancesMap.get("test");
            return evaluateResults(testInstances, dt, prefixLength, encodedFile, "DT", ClassificationType.DECISION_TREE);

        } catch(Exception e){
            e.printStackTrace();
            return null;
        }
    }

    @RequestMapping(method = GET, path = "/knn")
    public Results knnClassify(@RequestParam int prefixLength, @RequestParam String encodedFile) throws Exception{

        try{
            ConverterUtils.DataSource source = new ConverterUtils.DataSource(sendGet(encodedFile ,prefixLength));
            Instances instances = source.getDataSet();
            //split dataset to train and test
            instances = convertToNominal(instances);
            Map<String, Instances> instancesMap = InstancesSplitter.split(instances);

            Instances train = instancesMap.get("train");
            train.setClassIndex(instances.numAttributes() - 1);

            IBk ibk = (IBk) classificationService.train(ClassificationType.KNN, instances);

            Instances testInstances = instancesMap.get("test");
            return evaluateResults(testInstances, ibk, prefixLength, encodedFile, "KNN", ClassificationType.KNN);
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }

    }

    @RequestMapping(method = GET, path = "/rf")
    public Results rfClassify(@RequestParam int prefixLength, @RequestParam String encodedFile) throws Exception{

        try{
            ConverterUtils.DataSource source = new ConverterUtils.DataSource(sendGet(encodedFile ,prefixLength));
            Instances instances = source.getDataSet();
            //split dataset to train and test
            instances = convertToNominal(instances);
            Map<String, Instances> instancesMap = InstancesSplitter.split(instances);

            Instances train = instancesMap.get("train");
            train.setClassIndex(instances.numAttributes() - 1);

            RandomForest rf = (RandomForest) classificationService.train(ClassificationType.RANDOM_FOREST, instances);
            System.out.println("Random Forest Testing");

            Instances testInstances = instancesMap.get("test");
            return evaluateResults(testInstances, rf, prefixLength, encodedFile, "RF", ClassificationType.RANDOM_FOREST);
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }

    }

}
