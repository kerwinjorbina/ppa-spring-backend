package com.predictiveprocess.utility;

import weka.core.Instances;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;

/**
 * Created by Kerwin on 1/19/2017.
 */
public class InstanceSaver {

    public static void saveInstancesToArff(Instances instances, String filepath){

        try {
            BufferedWriter writer = new BufferedWriter(new FileWriter(filepath));
            writer.write(instances.toString());
            writer.flush();
            writer.close();
        } catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
    }
}
