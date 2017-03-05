package com.predictiveprocess.utility;

import org.deckfour.xes.model.XAttributeMap;

import java.util.Set;

public interface ReplayerListener {
	void openTrace(XAttributeMap attribs, String traceId, Set<String> candidateActivations);
	void closeTrace(XAttributeMap attribs, String traceId);
	void processEvent(XAttributeMap attribs, int index);
}
