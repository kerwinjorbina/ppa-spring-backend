package com.predictiveprocess.encoder;

import org.deckfour.xes.extension.std.XTimeExtension;
import org.deckfour.xes.model.XEvent;
import org.deckfour.xes.model.XLog;
import org.deckfour.xes.model.XTrace;
import org.deckfour.xes.model.impl.XAttributeTimestampImpl;
import weka.core.Attribute;
import weka.core.DenseInstance;
import weka.core.Instance;
import weka.core.Instances;

import java.util.ArrayList;
import java.util.HashMap;

/**
 * Created by kerwin on 31.01.17.
 */
public class RemainingTimeEncoder extends Encoder {


    private static ArrayList<Attribute> attributes = null;

    public RemainingTimeEncoder(){
    }


    public ArrayList<Attribute> generateAttributes(int prefixLength){

        ArrayList<Attribute> attributes = new ArrayList<Attribute>(prefixLength);

        for(int i = 0; i < prefixLength; i++){
            String attributeName = (i == prefixLength - 1)?"target":"step_"+i;
            Attribute attribute = new Attribute(attributeName);
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

        ArrayList<Attribute> attributes = generateAttributes(prefixLength);
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
            long remainingTime = getRemainingTime(event, trace.get(trace.size() - 1));
            eventNamePrefix.put(attributes.get(index).toString(), remainingTime);
            index++;
        }
        return eventNamePrefix;
    }

    public long getRemainingTime(XEvent currentEvent, XEvent endEvent){
        long remainingTime = 0;
        try{
            XAttributeTimestampImpl currTime=(XAttributeTimestampImpl)currentEvent.getAttributes().get(XTimeExtension.KEY_TIMESTAMP);
            XAttributeTimestampImpl lastTime=(XAttributeTimestampImpl)endEvent.getAttributes().get(XTimeExtension.KEY_TIMESTAMP);
            remainingTime = Math.abs(lastTime.getValueMillis() - currTime.getValueMillis())/(3600);
        }catch (Exception e){

        }
        return remainingTime;
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
