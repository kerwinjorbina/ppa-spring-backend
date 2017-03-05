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

/**
 * Created by kerwin on 31.01.17.
 */
public class SimpleEncoder extends Encoder {


    private static ArrayList<Attribute> attributes = null;
    ArrayList<String> events = null;
    ArrayList<String> resources = null;
    ArrayList<String> traces = null;
    int eventCounter;

    public SimpleEncoder(){
    }

    public SimpleEncoder(XLog log){
        this.events = LogReader.getAllEventNames(log);
        setResources(log);

    }

    public void setResources(XLog log){
        ArrayList<String> resources = new ArrayList<>();
        ArrayList<String> traces = new ArrayList<>();
        this.eventCounter = 0;
        for (XTrace trace : log) {
            String traceName = trace.getAttributes().get("concept:name").toString();
            if(!traces.contains(traceName))
                traces.add(traceName);
            for (XEvent event : trace) {
                this.eventCounter++;
                String resourceName = event.getAttributes().get("org:resource").toString();
                if(!resources.contains(resourceName))
                    resources.add(resourceName);
            }
        }
        this.traces = traces;
        this.resources = resources;
    }

    public ArrayList<Attribute> generateAttributes(){

        ArrayList<Attribute> attributes = new ArrayList<Attribute>(4);

        Attribute attribute = new Attribute("id");
        attributes.add(attribute);
        attribute = new Attribute("startTime");
        attributes.add(attribute);
        attribute = new Attribute("event");
        attributes.add(attribute);
        attribute = new Attribute("resource");
        attributes.add(attribute);

        this.attributes = attributes;

        return attributes;
    }

    public Instances encodeLog(XLog log) throws Exception{


        ArrayList<Attribute> attributes = generateAttributes();
        Instances instances = new Instances("DATA", attributes, this.eventCounter);

        for(XTrace trace : log){
            String caseId = trace.getAttributes().get("concept:name").toString();

            for(XEvent event : trace){
                Instance instance = new DenseInstance(attributes.size());

                instance.setValue(attributes.get(0), this.traces.indexOf(caseId));
                XAttributeTimestampImpl eventTime=(XAttributeTimestampImpl)event.getAttributes().get(XTimeExtension.KEY_TIMESTAMP);
                //seconds
                instance.setValue(attributes.get(1), eventTime.getValueMillis()/1000);
                String eventName = event.getAttributes().get("concept:name").toString();
                instance.setValue(attributes.get(2), this.events.indexOf(eventName));
                String resourceName = event.getAttributes().get("org:resource").toString();
                instance.setValue(attributes.get(3), this.resources.indexOf(resourceName));
                instances.add(instance);
            }
        }

        return instances;
    }

}
