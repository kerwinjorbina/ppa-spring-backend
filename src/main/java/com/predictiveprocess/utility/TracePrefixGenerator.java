package com.predictiveprocess.utility;

import org.deckfour.xes.factory.XFactoryRegistry;
import org.deckfour.xes.model.XEvent;
import org.deckfour.xes.model.XLog;
import org.deckfour.xes.model.XTrace;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

public class TracePrefixGenerator {
	private Map<Integer, Integer> prefixToLog;
	private XLog prefixLog;
	
	public TracePrefixGenerator(XLog log, int minPrefixLength, int maxPrefixLength, int gap){
		XLog prefixTraceLog  = XFactoryRegistry.instance().currentDefault().createLog();
		prefixTraceLog.setAttributes(log.getAttributes());
		
		Map<Integer, Integer> prefixToLog = new HashMap<Integer, Integer>();
		int prefixIndex = 0;
		int logIndex = 0;
		
		for (XTrace trace : log) {
			// change to or pL should not reach the last event of the trace
			for(int pL = minPrefixLength; pL <= maxPrefixLength ; pL = pL + gap){
				// only add prefix traces that are less than the trace size
				if(pL < trace.size()){
					prefixTraceLog.add(getPrefixTrace(trace, pL));
					prefixToLog.put(prefixIndex++, logIndex);
				}
			}
			
			//do not add last event of trace
//			if (trace.size()<maxPrefixLength){
//				prefixTraceLog.add(trace);
//				prefixToLog.put(prefixIndex++, logIndex);
//			}
			logIndex++;
		}
		this.prefixToLog = prefixToLog;
		this.prefixLog = prefixTraceLog;
	}
	
	public XLog generatePrefixesFromLog(XLog log, int minPrefixLength, int maxPrefixLength, int gap){
		XLog prefixTraceLog  = XFactoryRegistry.instance().currentDefault().createLog();
		prefixTraceLog.setAttributes(log.getAttributes());
		
		Map<Integer, Integer> prefixToLog = new HashMap<Integer, Integer>();
		int prefixIndex = 0;
		int logIndex = 0;
		
		for (XTrace trace : log) {
			// change to or pL should not reach the last event of the trace
			for (int pL = minPrefixLength; pL <= maxPrefixLength || pL <trace.size(); pL = pL+gap) {
				prefixTraceLog.add(getPrefixTrace(trace, pL));
				prefixToLog.put(prefixIndex++, logIndex);
			}
			if (trace.size()<maxPrefixLength){
				prefixTraceLog.add(trace);
				prefixToLog.put(prefixIndex++, logIndex);
			}
			logIndex++;
		}
		this.prefixToLog = prefixToLog;
		
		return prefixTraceLog;
	}
	
	public Map<Integer, Integer> getPrefixToLog(){
		return prefixToLog;
	}
	
	public XLog getPrefixLog(){
		return prefixLog;
	}
	
	public static XLog computePrefixTraceLog(XLog log, int prefixLength){
		XLog prefixTraceLog  = XFactoryRegistry.instance().currentDefault().createLog();
		prefixTraceLog.setAttributes(log.getAttributes());
		for (XTrace trace : log) {
			prefixTraceLog.add(getPrefixTrace(trace, prefixLength));
		}
		return prefixTraceLog;
	}

	private static XTrace getPrefixTrace(XTrace trace, int prefixLength ){
		XTrace prefixTrace = XFactoryRegistry.instance().currentDefault().createTrace(trace.getAttributes());
		int i=0;
		for (Iterator iterator = trace.iterator(); iterator.hasNext() && i<prefixLength;) {
			XEvent event = (XEvent) iterator.next();
			prefixTrace.add(event);
			i++;
		}

		return prefixTrace;
	}

}
