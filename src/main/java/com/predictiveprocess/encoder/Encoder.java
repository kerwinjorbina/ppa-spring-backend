package com.predictiveprocess.encoder;

import org.deckfour.xes.model.XTrace;
import weka.core.Instances;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by Kerwin on 1/18/2017.
 */
public class Encoder {
    public Instances instances;
    EncodingType type;

    public Instances getInstances(){
        return instances;
    }
}
