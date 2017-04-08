package com.predictiveprocess.utility;

import com.predictiveprocess.log.XLogReader;
import org.deckfour.xes.factory.XFactory;
import org.deckfour.xes.factory.XFactoryRegistry;
import org.deckfour.xes.model.XAttributeMap;
import org.deckfour.xes.model.XLog;
import org.deckfour.xes.model.XTrace;
import org.deckfour.xes.out.XesXmlSerializer;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;

public class TraceCutter {
	
	public static void main(String[] args) {
		String outputFilePath = "./input/BPI2011_80.org.deckfour.xes";
//		String inputFilePath = "./input/BPI2011_80.org.deckfour.xes";
		String inputFilePath = "./src/main/resources/logs/bpi2011/bpi2013_all.org.deckfour.xes";
		
		XLog log;
		try {
			log = XLogReader.openLog(inputFilePath);

			splitLog(log, 0.80, "bpi2013_80.org.deckfour.xes", "bpi2013_20.org.deckfour.xes");

		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	/**
	 * Part of the log file (percentage) is stored in log1FilePath, the remaining part (1-percentage) in the log2FilePath
	 * @param log
	 * @param percentage
	 * @param log1FilePath
	 * @param log2FilePath
	 */
	public static void splitLog(XLog log, double percentage, String log1FilePath, String log2FilePath){
		int set1Size = (int) (log.size()*percentage);

		XLog log1 = XFactoryRegistry.instance().currentDefault().createLog();
		XLog log2 = XFactoryRegistry.instance().currentDefault().createLog();

		XAttributeMap map = log.getAttributes();
		log1.setAttributes(map);
		log2.setAttributes(map);

		int i = 0;
		for (XTrace trace : log) {
			if (i<set1Size)
				log1.add(trace);
			else
				log2.add(trace);
			i++;
		}
		saveLog(log1, log1FilePath);
		saveLog(log2, log2FilePath);

	}
	
	public static void cutTrace(int maxTraceNumber, int maxEventNumber, XLog log, String outputLogPath){
		
		
		XFactory factory = XFactoryRegistry.instance().currentDefault();
		XLog newLog = factory.createLog();
		newLog.setAttributes(log.getAttributes());
		for (int i = 0; i < maxTraceNumber; i++) {
			XTrace trace = log.get(i);
			if (i==912){
				XTrace newTrace = factory.createTrace();
				newTrace.setAttributes(trace.getAttributes());
				for (int j = 0; j < maxEventNumber; j++) {
					newTrace.add(trace.get(j));
				}
				newLog.add(newTrace);
			} else
				newLog.add(trace);
		}
		saveLog(newLog, outputLogPath);
		
	}
	
	public static void saveLog(XLog log, String outputLogPath){
		File output = new File(outputLogPath);

		try {
			FileOutputStream fBOS = new FileOutputStream(output);
			XesXmlSerializer serializer = new XesXmlSerializer();
			serializer.serialize(log, fBOS);
			fBOS.flush();
			fBOS.close();
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

	}

}
