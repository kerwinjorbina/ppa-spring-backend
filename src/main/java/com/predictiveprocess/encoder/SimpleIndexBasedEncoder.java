package com.predictiveprocess.encoder;

import com.predictiveprocess.log.LogReader;
import org.deckfour.xes.model.XEvent;
import org.deckfour.xes.model.XLog;
import org.deckfour.xes.model.XTrace;
import weka.core.Attribute;
import weka.core.DenseInstance;
import weka.core.Instance;
import weka.core.Instances;

import java.util.ArrayList;
import java.util.HashMap;

/**
 * Created by kerwin on 31.01.17.
 */
public class SimpleIndexBasedEncoder extends Encoder {


    private static ArrayList<Attribute> attributes = null;
    ArrayList<String> events = null;

    public static ArrayList<Attribute> getAttributes() {
        return attributes;
    }

    public SimpleIndexBasedEncoder(){
    }

    public SimpleIndexBasedEncoder(XLog log){
        this.events = LogReader.getAllEventNames(log);
    }

    public ArrayList<Attribute> generateAttributes(int prefixLength, ArrayList<String> values){

        ArrayList<Attribute> attributes = new ArrayList<Attribute>(prefixLength);
        ArrayList<Attribute> attributesNumeric = new ArrayList<Attribute>(prefixLength);

        for(int i = 0; i < prefixLength; i++){
            String attributeName = (i == prefixLength - 1)?"target":"step_"+i;
            Attribute attribute = new Attribute(attributeName, values);
            attributes.add(attribute);
        }

        this.attributes = attributes;

        return attributes;
    }

    public Instance createInstance(HashMap<String, Long> eventNamePrefix, ArrayList<Attribute> attributes){
        Instance instance = new DenseInstance(eventNamePrefix.size());
        for(Attribute attribute : attributes){
            try {
                instance.setValue(attribute, eventNamePrefix.get(attribute.toString()));
            }catch (Exception e){
                System.out.println("error happened");
            }
        }
        return instance;
    }

    public Instances encodeLog(XLog log, int prefixLength) throws Exception{
        ArrayList<String> attributeValues = new ArrayList<>();

        ArrayList<String> values = new ArrayList<>();
        for(int i = 0; i < events.size(); i++)
            values.add(Integer.toString(i));

        ArrayList<Attribute> attributes = generateAttributes(prefixLength, values);
        Instances instances = new Instances("DATA", attributes, log.size());

        for (XTrace trace : log) {
            //do not include shorter traces in making the instances
            if(trace.size() < prefixLength)
                continue;
            instances.add(createInstance(encodeTrace(trace, prefixLength), attributes));
        }

        return instances;
    }

    private HashMap<String, Long> encodeTrace(XTrace trace, int prefixLength) throws Exception{
        int index = 0;
        HashMap<String, Long> eventNamePrefix = new HashMap<>();

        for (XEvent event : trace) {
            if(index >= prefixLength)
                continue;
            String eventName = event.getAttributes().get("concept:name").toString();
            long value = this.events.indexOf(eventName);
            eventNamePrefix.put(attributes.get(index).toString(), value);
            index++;
        }
        return eventNamePrefix;
    }

    public Instances encodeTestLog(XLog log, int prefixLength, ArrayList<Attribute> attributes) throws Exception{

        Instances instances = new Instances("DATA", attributes, log.size());

        for (XTrace trace : log) {
            //do not include shorter traces in making the instances
            if(trace.size() < prefixLength)
                continue;
            instances.add(createInstance(encodeTrace(trace, prefixLength), attributes));
        }

        return instances;
    }


}
