package com.predictiveprocess.webservice.rest;

import com.predictiveprocess.encoder.EncodingType;
import com.predictiveprocess.encoder.LoadFrequencyEncoder;
import com.predictiveprocess.log.Log;
import com.predictiveprocess.log.LogReader;
import com.predictiveprocess.log.LogRepository;
import com.predictiveprocess.log.XLogReader;
import com.predictiveprocess.webservice.service.EncodingService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import weka.core.Instances;

import java.util.Map;
import java.util.TreeMap;

import static org.springframework.web.bind.annotation.RequestMethod.GET;

/**
 * Created by Kerwin on 1/18/2017.
 */
@CrossOrigin
@RestController
@RequestMapping("/api/encode")
public class EncodingRestController {
    @Autowired
    EncodingService encodingService;
    @Autowired
    LogRepository repo;

    @RequestMapping(method = GET, path = "")
    public String encode(@RequestParam String filename, @RequestParam int prefixLength) throws Exception{
        System.out.println ("encode file simple");
        System.out.println("filename = "+ filename);
        Instances instances = encodingService.encodeLog(EncodingType.SIMPLE, filename, prefixLength);

        LogReader.writeToFile(instances, filename+".csv");
        System.out.println("done loading this page.");
        return instances.toString();
    }

    @RequestMapping(method = GET, path = "/simple")
    public String simpleIndexEncode() throws Exception{
        Instances instances = encodingService.encodeLog(EncodingType.SIMPLE_INDEX, "./activitylog_uci_detailed_labour.org.deckfour.xes", 0);
        System.out.println("in this path /api/encode/simple");

        System.out.println("done loading this page.");
        return "";
    }

    @RequestMapping(method = GET, path = "/dailyworkload/{id}")
    public Map<String, Integer> workload(@PathVariable Long id) throws Exception{
        Log log = repo.findById(id);

        System.out.println("done reading log");

        Map<String, Integer> map = new TreeMap<String, Integer>(LoadFrequencyEncoder.countActiveTraces(XLogReader.openLog(log.getPath())));

        return map;
    }

    @RequestMapping(method = GET, path = "/dailyresources/{id}")
    public Map<String, Integer> resourcesload(@PathVariable Long id) throws Exception{
        Log log = repo.findById(id);

        System.out.println("done reading log");

        Map<String, Integer> map = new TreeMap<String, Integer>(LoadFrequencyEncoder.countActiveResources(XLogReader.openLog(log.getPath())));

        return map;
    }

    @RequestMapping(method = GET, path = "/eventexecutions/{id}")
    public Map<String, Integer> eventExecutions(@PathVariable Long id) throws Exception{
        Log log = repo.findById(id);

        System.out.println("done reading log");

//        Map<String, Integer> map = new TreeMap<String, Integer>();

        return LoadFrequencyEncoder.getEventExecutions(XLogReader.openLog(log.getPath()));
    }
}
