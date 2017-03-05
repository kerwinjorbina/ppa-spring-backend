package com.predictiveprocess.encoder;

import com.predictiveprocess.log.LogReader;
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
public class DependentRemainingTimeEncoder extends Encoder {


    private static ArrayList<Attribute> attributes = null;
    ArrayList<String> events = null;

    public DependentRemainingTimeEncoder(){
    }

    public DependentRemainingTimeEncoder(XLog log){
        this.events = LogReader.getAllEventNames(log);
    }


    public ArrayList<Attribute> generateAttributes(int prefixLength, int numberOfTraces){

        ArrayList<Attribute> attributes = new ArrayList<Attribute>(prefixLength);

        for(int i = 0; i < numberOfTraces; i++){
            for(int j = 0; j < prefixLength; j++){
                String attributeName = "trace_"+i+"_event_"+j;
                Attribute attribute = new Attribute(attributeName);
                attributes.add(attribute);
            }
            String elapsedTimeAttributeName = "elapsedTime_trace"+i;
            Attribute elapsedTimeAttribute = new Attribute(elapsedTimeAttributeName);
            attributes.add(elapsedTimeAttribute);
        }

        Attribute attribute = new Attribute("remainingTime");
        attributes.add(attribute);

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

        int logLength = 0;
        for(XTrace trace : log) {
            if (trace.size() < prefixLength)
                continue;
            logLength++;
        }

        ArrayList<Attribute> attributes = generateAttributes(prefixLength, logLength);
        Instances instances = new Instances("DATA", attributes, logLength);

        for(int i = 0; i < log.size(); i++){
            XTrace trace = log.get(i);
            if(trace.size() < prefixLength)
                continue;

            int index = 0;

            HashMap<String, Long> instanceData = new HashMap<>();
            instanceData.putAll(encodeTrace(trace, prefixLength, index));
            index = instanceData.size();
            for(int j = 0; j < log.size(); j++){
                if(i == j) continue;

                XTrace trace2 = log.get(j);
                if(trace2.size() < prefixLength)
                    continue;
                instanceData.putAll(encodeTrace(trace2, prefixLength, index));
                index = instanceData.size();
            }
            long remainingTime = getTimeDifference(trace.get(prefixLength-1), trace.get(trace.size()-1));
            instanceData.put(attributes.get(attributes.size()-1).toString(), remainingTime);
            instances.add(createInstance(instanceData, attributes));
        }

        return instances;
    }

    private HashMap<String, Long> encodeTrace(XTrace trace, int prefixLength, int index) throws Exception{
        HashMap<String, Long> eventNamePrefix = new HashMap<>();

        int counter = 0;
        for (XEvent event : trace) {
            if(counter >= prefixLength) {
                long remainingTime = getTimeDifference(trace.get(0), event);
                eventNamePrefix.put(attributes.get(index).toString(), remainingTime);
                continue;
            }
            String eventName = event.getAttributes().get("concept:name").toString();
            long value = this.events.indexOf(eventName);
            eventNamePrefix.put(attributes.get(index).toString(), value);
            index++;
            counter++;
        }
        return eventNamePrefix;
    }

    public long getTimeDifference(XEvent currentEvent, XEvent endEvent){
        long remainingTime = 0;
        try{
            XAttributeTimestampImpl currTime=(XAttributeTimestampImpl)currentEvent.getAttributes().get(XTimeExtension.KEY_TIMESTAMP);
            XAttributeTimestampImpl lastTime=(XAttributeTimestampImpl)endEvent.getAttributes().get(XTimeExtension.KEY_TIMESTAMP);
            remainingTime = Math.abs(lastTime.getValueMillis() - currTime.getValueMillis())/(3600);
        }catch (Exception e){

        }
        return remainingTime;
    }

}
