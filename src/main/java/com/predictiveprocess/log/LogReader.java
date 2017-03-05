package com.predictiveprocess.log;

import org.deckfour.xes.model.XEvent;
import org.deckfour.xes.model.XLog;
import org.deckfour.xes.model.XTrace;

import java.util.ArrayList;

/**
 * Created by Kerwin on 1/18/2017.
 */
public class LogReader {
    public LogReader(){

    }

    public static XLog readFile(String inputLogFilePath){
        XLog log = null;
        try {
            log = XLogReader.openLog(inputLogFilePath);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return log;
    }

    public static ArrayList<String> getAllEventNames(XLog log){
        ArrayList<String> events = new ArrayList<>();

        for (XTrace trace : log) {
            for (XEvent event : trace) {
                String eventName = event.getAttributes().get("concept:name").toString();
                if(!events.contains(eventName))
                    events.add(eventName);
            }
        }
        return events;
    }
}
