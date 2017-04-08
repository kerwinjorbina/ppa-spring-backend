package com.predictiveprocess.webservice.rest;

import com.predictiveprocess.classification.enumeration.ClassificationType;
import com.predictiveprocess.webservice.service.ClassificationService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import weka.classifiers.trees.J48;
import weka.classifiers.trees.RandomForest;
import weka.core.Instance;
import weka.core.Instances;
import weka.core.converters.CSVSaver;
import weka.core.converters.ConverterUtils;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.InputStreamReader;

import static org.springframework.web.bind.annotation.RequestMethod.GET;

/**
 * Created by Kerwin on 1/18/2017.
 */
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

    @RequestMapping(method = GET, path = "/dt")
    public String dtClassify(@RequestParam int prefixLength, @RequestParam String trainFile, @RequestParam String testFile) throws Exception{
        //read csv file
        try{
            ConverterUtils.DataSource source = new ConverterUtils.DataSource(readFile(trainFile));
            Instances instances = source.getDataSet();
            instances.setClassIndex(instances.numAttributes() - 1);

            J48 dt = (J48) classificationService.train(ClassificationType.DECISION_TREE, instances);
            System.out.println("Decision Tree J48 Testing");

            ConverterUtils.DataSource testSource = new ConverterUtils.DataSource(readFile(testFile));
            Instances testInstances = source.getDataSet();
            testInstances.setClassIndex(testInstances.numAttributes() - 1);

            if(testInstances.size() == 0)
                return "empty test";

            int correctPredictions = 0;
            for(Instance instance : testInstances){
                Double val = instance.value(instance.numAttributes()-1);
                Double res = classificationService.predict(ClassificationType.DECISION_TREE, dt, instance);
                if(val.doubleValue() == res.doubleValue())
                    correctPredictions++;
            }
            String results = "number of correct predictions = "+ correctPredictions;
            results += "\nnumber of instances tested = "+ testInstances.size();
            results += "\naccuracy = "+ 100*correctPredictions/testInstances.size();
            System.out.print(results);
            return results;
        } catch(Exception e){
            e.printStackTrace();
            return null;
        }
    }

    @RequestMapping(method = GET, path = "/rf")
    public String rfClassify(@RequestParam int prefixLength, @RequestParam String trainFile, @RequestParam String testFile) throws Exception{

        try{
            System.out.println("prefix length = "+ prefixLength);
            ConverterUtils.DataSource source = new ConverterUtils.DataSource(readFile(trainFile));
            Instances instances = source.getDataSet();
            instances.setClassIndex(instances.numAttributes() - 1);

            RandomForest rf = (RandomForest) classificationService.train(ClassificationType.RANDOM_FOREST, instances);
            System.out.println("Random Forest Testing");

            ConverterUtils.DataSource testSource = new ConverterUtils.DataSource(readFile(testFile));
            Instances testInstances = source.getDataSet();
            testInstances.setClassIndex(testInstances.numAttributes() - 1);

            if(testInstances.size() == 0)
                return "empty test";

            int correctPredictions = 0;
            for(Instance instance : testInstances){
                Double val = instance.value(instance.numAttributes()-1);
                Double res = classificationService.predict(ClassificationType.RANDOM_FOREST, rf, instance);
//            System.out.println("actual = "+ val +" ----- result prediction = "+ res);
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

    public void writeToFile(Instances data, String filename){
        try{
            CSVSaver saver = new CSVSaver();
            saver.setInstances(data);
            saver.setFile(new File("./"+filename));
            saver.writeBatch();
        }catch(Exception e){
            System.out.println(e.toString());
        }
    }
}
