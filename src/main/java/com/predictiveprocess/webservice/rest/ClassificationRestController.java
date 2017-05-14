package com.predictiveprocess.webservice.rest;

import com.predictiveprocess.classification.enumeration.ClassificationType;
import com.predictiveprocess.utility.InstancesSplitter;
import com.predictiveprocess.webservice.service.ClassificationService;
import org.apache.commons.io.FileUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import weka.classifiers.lazy.IBk;
import weka.classifiers.trees.J48;
import weka.classifiers.trees.REPTree;
import weka.classifiers.trees.RandomForest;
import weka.classifiers.trees.RandomTree;
import weka.core.Instance;
import weka.core.Instances;
import weka.core.converters.ConverterUtils;
import weka.filters.Filter;
import weka.filters.unsupervised.attribute.NumericToNominal;

import java.io.*;
import java.net.HttpURLConnection;
import java.net.URL;
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


//    String log="./logs/dailyactivities/activitylog_uci_detailed_labour.org.deckfour.xes";
//    String testLog="./logs/dailyactivities/activitylog_uci_detailed_labour.org.deckfour.xes";

//    String log="./logs/hospital/2004visitdetails.org.deckfour.xes";
//    String testLog="./logs/hospital/2004visitdetails.org.deckfour.xes";

    String log="./logs/hospital/2004visitdetails.org.deckfour.xes";
    String testLog="./logs/hospital/2004visitdetails.org.deckfour.xes";

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

        System.out.println("Before");
        for(int i=0; i<2; i=i+1)
        {
            System.out.println("Nominal? "+instances.attribute(i).isNominal());
        }

        System.out.println("After");
        for(int i=0; i<2; i=i+1)
        {
            System.out.println("Nominal? "+newData.attribute(i).isNominal());
        }
        return newData;
    }

    @RequestMapping(method = GET, path = "/dt")
    public String dtClassify(@RequestParam int prefixLength, @RequestParam String encodedFile) throws Exception{
        ;
        //read csv file
        try{
            //split the log
            ConverterUtils.DataSource source = new ConverterUtils.DataSource(sendGet(encodedFile ,prefixLength));
//            ConverterUtils.DataSource source = new ConverterUtils.DataSource(readFile(encodedFile));
            Instances instances = source.getDataSet();
            //split dataset to train and test
            instances = convertToNominal(instances);
            Map<String, Instances> instancesMap = InstancesSplitter.split(instances);

            Instances train = instancesMap.get("train");
            train.setClassIndex(instances.numAttributes() - 1);

            REPTree dt = (REPTree) classificationService.train(ClassificationType.DECISION_TREE, train);
            System.out.println("Decision Tree RepTree Testing");

            Instances test = instancesMap.get("test");
            test.setClassIndex(instances.numAttributes() - 1);

            if(test.size() == 0)
                return "empty test";

            int correctPredictions = 0;
            for(Instance instance : test){
                Double val = instance.value(instance.numAttributes()-1);
                Double res = classificationService.predict(ClassificationType.DECISION_TREE, dt, instance);
                System.out.println("actual = "+val+ " prediction = "+res);
                if(val.doubleValue() == res.doubleValue())
                    correctPredictions++;
            }
            String results = "number of correct predictions = "+ correctPredictions;
            results += "\nnumber of instances tested = "+ test.size();
            results += "\naccuracy = "+ 100*correctPredictions/test.size();
            System.out.print(results);
            return results;
        } catch(Exception e){
            e.printStackTrace();
            return null;
        }
    }

    @RequestMapping(method = GET, path = "/knn")
    public String knnClassify(@RequestParam int prefixLength, @RequestParam String encodedFile) throws Exception{

        try{
            ConverterUtils.DataSource source = new ConverterUtils.DataSource(sendGet(encodedFile ,prefixLength));
//            ConverterUtils.DataSource source = new ConverterUtils.DataSource(readFile(encodedFile));
            Instances instances = source.getDataSet();
            //split dataset to train and test
            instances = convertToNominal(instances);
            Map<String, Instances> instancesMap = InstancesSplitter.split(instances);

            Instances train = instancesMap.get("train");
            train.setClassIndex(instances.numAttributes() - 1);

            IBk ibk = (IBk) classificationService.train(ClassificationType.KNN, instances);
            System.out.println("Random Forest Testing");

            Instances testInstances = instancesMap.get("test");
            testInstances.setClassIndex(testInstances.numAttributes() - 1);

            if(testInstances.size() == 0)
                return "empty test";

            int correctPredictions = 0;
            for(Instance instance : testInstances){
                Double val = instance.value(instance.numAttributes()-1);
                Double res = classificationService.predict(ClassificationType.KNN, ibk, instance);
                System.out.println("actual = "+ val +" ----- result prediction = "+ res);
                if(val.doubleValue() == res.doubleValue())
                    correctPredictions++;
            }
            String results = "number of correct predictions = "+ correctPredictions;
            results += "\nnumber of instances tested = "+ testInstances.size();
            results += "\naccuracy = "+ 100*correctPredictions/testInstances.size();
            System.out.print(results);
            return results;
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }

    }

    @RequestMapping(method = GET, path = "/rf")
    public String rfClassify(@RequestParam int prefixLength, @RequestParam String encodedFile) throws Exception{

        try{
            ConverterUtils.DataSource source = new ConverterUtils.DataSource(sendGet(encodedFile ,prefixLength));
//            ConverterUtils.DataSource source = new ConverterUtils.DataSource(readFile(encodedFile));
            Instances instances = source.getDataSet();
            //split dataset to train and test
            instances = convertToNominal(instances);
            Map<String, Instances> instancesMap = InstancesSplitter.split(instances);

            Instances train = instancesMap.get("train");
            train.setClassIndex(instances.numAttributes() - 1);

            RandomForest rf = (RandomForest) classificationService.train(ClassificationType.RANDOM_FOREST, instances);
            System.out.println("Random Forest Testing");

            Instances testInstances = instancesMap.get("test");
            testInstances.setClassIndex(testInstances.numAttributes() - 1);

            if(testInstances.size() == 0)
                return "empty test";

            int correctPredictions = 0;
            for(Instance instance : testInstances){
                Double val = instance.value(instance.numAttributes()-1);
                Double res = classificationService.predict(ClassificationType.RANDOM_FOREST, rf, instance);
                System.out.println("actual = "+ val +" ----- result prediction = "+ res);
                if(val.doubleValue() == res.doubleValue())
                    correctPredictions++;
            }
            String results = "number of correct predictions = "+ correctPredictions;
            results += "\nnumber of instances tested = "+ testInstances.size();
            results += "\naccuracy = "+ 100*correctPredictions/testInstances.size();
            System.out.print(results);
            return results;
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }

    }

}
