package com.predictiveprocess.webservice.rest;

import com.predictiveprocess.log.LogRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import static org.springframework.web.bind.annotation.RequestMethod.GET;

/**
 * Created by kerwin on 3/11/17.
 */
@RestController
@RequestMapping("/api/logs")
public class LogController {
    @Autowired
    LogRepository repo;

    @RequestMapping(method = GET, path = "")
    public String list(){
        return "logs";
    }
}
