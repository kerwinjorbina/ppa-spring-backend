package com.predictiveprocess.utility;

import weka.core.Instances;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by kerwin on 5/13/17.
 */
public class InstancesSplitter {

    public static Map<String, Instances> split(Instances instances){
        int trainSize = (int) Math.round(instances.numInstances() * 80
                / 100);
        int testSize = instances.numInstances() - trainSize;
        Instances train = new Instances(instances, 0, trainSize);
        Instances test = new Instances(instances, trainSize, testSize);

        Map<String, Instances> instancesMap = new HashMap<>();

        instancesMap.put("train", train);
        instancesMap.put("test", test);

        return instancesMap;
    }

}
