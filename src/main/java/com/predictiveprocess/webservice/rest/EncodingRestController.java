package com.predictiveprocess.webservice.rest;

import com.predictiveprocess.encoder.EncodingType;
import com.predictiveprocess.log.LogReader;
import com.predictiveprocess.utility.InstanceSaver;
import com.predictiveprocess.webservice.service.EncodingService;
import org.deckfour.xes.model.XLog;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import weka.classifiers.functions.SMOreg;
import weka.classifiers.trees.RandomForest;
import weka.core.Instance;
import weka.core.Instances;
import weka.core.converters.CSVSaver;
import weka.filters.Filter;
import weka.filters.unsupervised.attribute.Remove;

import java.io.File;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Set;

import static org.springframework.web.bind.annotation.RequestMethod.GET;

/**
 * Created by Kerwin on 1/18/2017.
 */
@RestController
@RequestMapping("/api/encode")
public class EncodingRestController {
    @Autowired
    EncodingService encodingService;

    @RequestMapping(method = GET, path = "")
    public String encode(@RequestParam String filename, @RequestParam int prefixLength) throws Exception{
        System.out.println ("encode file simple");
        Instances instances = encodingService.encodeLog(EncodingType.SIMPLE, filename, prefixLength);

        InstanceSaver.saveInstancesToArff(instances, "arff/file.arff");
        System.out.println("done loading this page.");
        return instances.toString();
    }

    @RequestMapping(method = GET, path = "/simple")
    public String simpleIndexEncode() throws Exception{
        Instances instances = encodingService.encodeLog(EncodingType.SIMPLE_INDEX, "./logs/dailyactivities/activitylog_uci_detailed_labour.xes", 0);
        System.out.println("in this path /api/encode/simple");
//        InstanceSaver.saveInstancesToArff(instances, "arff/file.arff");
        System.out.println("done loading this page.");
        return "";
    }

}
