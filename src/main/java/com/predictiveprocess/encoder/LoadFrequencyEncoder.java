package com.predictiveprocess.encoder;

import org.deckfour.xes.extension.std.XTimeExtension;
import org.deckfour.xes.model.XEvent;
import org.deckfour.xes.model.XLog;
import org.deckfour.xes.model.XTrace;
import weka.core.Attribute;
import weka.core.DenseInstance;
import weka.core.Instances;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;

/**
 * Created by Kerwin on 1/18/2017.
 */
public class LoadFrequencyEncoder extends Encoder{

    public LoadFrequencyEncoder(XLog log){

        this.instances = countActiveTraces(log);
    }

    public static ArrayList<Attribute> generateAttributes(){

        ArrayList<Attribute> attributes = new ArrayList<Attribute>(2);

        Attribute frequency = new Attribute("Frequency");
        Attribute date = new Attribute("Date", "yyyy-MM-dd");

        attributes.add(frequency);
        attributes.add(date);

        return attributes;
    }

    public static Instances getDailyWorkLoad(HashMap<String, Integer> workLoad) {
        Instances data = null;

        ArrayList<Attribute> attributes = new ArrayList<Attribute>(2);

        try {
            data = new Instances("DATA", generateAttributes(), 0);

            //sort the keys to prepare for timeseries
            List<String> sortedKeys = new ArrayList(workLoad.keySet());
            Collections.sort(sortedKeys);

            for (String key :  sortedKeys) {
                double[] instanceValue = new double[2];

                instanceValue[0] = workLoad.get(key);
                instanceValue[1] = data.attribute(1).parseDate(key);
                data.add(new DenseInstance(1.0, instanceValue));
            }
            System.out.println("done filling workload instances data");
        } catch (Exception ex) {
            ex.printStackTrace();
        }
        return data;
    }

    public static Instances countActiveTraces(XLog log){
        Instances instances = null;
        try {
            HashMap<String, Integer> workLoad = new HashMap<String, Integer>();
            for (XTrace trace : log) {
                ArrayList<String> activeDates = new ArrayList<String>();

                for (XEvent event : trace) {
                    String date = new SimpleDateFormat("yyyy-MM-dd").format(XTimeExtension.instance().extractTimestamp(event));

                    if(activeDates.contains(date)) break;

                    if(workLoad.containsKey(date)){
                        workLoad.put(date, workLoad.get(date)+1);
                    }
                    else{
                        workLoad.put(date, 1);
                    }
                }
            }
            instances = getDailyWorkLoad(workLoad);

        } catch (Exception e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }

        return instances;
    }

    //to be used as exogenous variable
    public static ArrayList<String> getCasePerDayPerIndexEvent(XLog log, int prefixLength){
        ArrayList<String> exog = new ArrayList<>();
        try {
            int cases = 0; // this is a bug
            for(int index = 0; index < prefixLength; index++){
                ArrayList<String> activeDates = new ArrayList<String>();

                for (XTrace trace : log) {
                    if(trace.size() < prefixLength) continue;
                    cases++;
                    XEvent event = trace.get(index);
                    String date = new SimpleDateFormat("yyyy-MM-dd").format(XTimeExtension.instance().extractTimestamp(event));
                    if(activeDates.contains(date)) break;
                    activeDates.add(date);

                }
                exog.add(Double.toString(log.size()/(double)activeDates.size()));
            }

        } catch (Exception e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        return exog;
    }

}
