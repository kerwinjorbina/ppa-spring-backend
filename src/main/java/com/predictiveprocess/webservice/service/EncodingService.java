package com.predictiveprocess.webservice.service;

import com.predictiveprocess.encoder.Encoder;
import com.predictiveprocess.encoder.EncodingType;
import com.predictiveprocess.encoder.LoadFrequencyEncoder;
import com.predictiveprocess.encoder.SimpleIndexBasedEncoder;
import com.predictiveprocess.log.LogReader;
import org.deckfour.xes.model.XLog;
import org.springframework.stereotype.Service;
import weka.core.Instances;

import java.net.URL;
import java.util.ArrayList;

/**
 * Created by Kerwin on 1/18/2017.
 */
@Service
public class EncodingService {
    public Encoder encoder;
    public int prefixLength;

    public Instances encodeLog(EncodingType type, String filename, int prefixLength) throws Exception{

        this.prefixLength = prefixLength;

        Instances instances = null;
//        URL url = getClass().getClassLoader().getResource(filename);
//        XLog log = LogReader.readFile(url.getPath());
        XLog log = LogReader.readFile(filename);

        switch (type){
//            case FREQUENCY:
//                LoadFrequencyEncoder loadFrequencyEncoder = new LoadFrequencyEncoder(log);
//                instances = loadFrequencyEncoder.getInstances();
//                break;
            case SIMPLE_INDEX:
                SimpleIndexBasedEncoder simpleIndexBasedEncoder = new SimpleIndexBasedEncoder(log);
                instances = simpleIndexBasedEncoder.encodeLog(log, this.prefixLength);
                ArrayList<String> exog = LoadFrequencyEncoder.getCasePerDayPerIndexEvent(log, prefixLength);
                String csv = String.join(",", exog);
                encoder = simpleIndexBasedEncoder;
                break;
            default:
                break;
        }

        return instances;
    }

    public Instances encodeTestLog(EncodingType type, String filename) throws Exception{

        Instances instances = null;
        URL url = getClass().getClassLoader().getResource(filename);
        XLog log = LogReader.readFile(url.getPath());
        switch (type){
//            case FREQUENCY:
//                LoadFrequencyEncoder loadFrequencyEncoder = new LoadFrequencyEncoder(log);
//                instances = loadFrequencyEncoder.getInstances();
//                break;
            case SIMPLE_INDEX:
                SimpleIndexBasedEncoder simpleIndexBasedEncoder = (SimpleIndexBasedEncoder)this.encoder;
                instances = simpleIndexBasedEncoder.encodeTestLog(log, this.prefixLength, simpleIndexBasedEncoder.getAttributes());
                break;
            default:
                break;
        }
        return instances;
    }
}
