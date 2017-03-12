package com.predictiveprocess.webservice.rest;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.ObjectWriter;
import com.google.gson.Gson;
import com.predictiveprocess.log.Log;
import com.predictiveprocess.log.LogRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.multipart.MultipartHttpServletRequest;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.file.Paths;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

import static org.springframework.web.bind.annotation.RequestMethod.GET;

/**
 * Created by kerwin on 3/11/17.
 */
@CrossOrigin
@RestController
@RequestMapping("/api/logs")
public class LogController {
    @Autowired
    LogRepository repo;

    @RequestMapping(value = "/uploadFile", method = RequestMethod.POST)
    @ResponseBody
    public ResponseEntity<?> uploadFile(HttpServletRequest req, HttpServletResponse res) throws ServletException, IOException {

        try {
//             Get the filename and build the local file path
            String filepath = "";
            MultipartHttpServletRequest multipartRequest = (MultipartHttpServletRequest) req;

            Set set = multipartRequest.getFileMap().entrySet();
            String name = req.getParameter("name");
            String description = req.getParameter("description");
            String directory = ".";

            Iterator i = set.iterator();
            while(i.hasNext()) {
                Map.Entry me = (Map.Entry)i.next();
                String filename = (String)me.getKey();
                MultipartFile multipartFile = (MultipartFile)me.getValue();
                System.out.println("Original fileName - " + multipartFile.getOriginalFilename());
                System.out.println("fileName - " + filename);
                filepath = Paths.get(directory, multipartFile.getOriginalFilename()).toString();
                // Save the file locally
                BufferedOutputStream stream =
                        new BufferedOutputStream(new FileOutputStream(new File(filepath)));
                stream.write(multipartFile.getBytes());
                stream.close();
            }

            Log log = new Log(name, filepath, description);
            repo.save(log);
        }
        catch (Exception e) {
            System.out.println(e.getMessage());
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
        }

        return new ResponseEntity<>(HttpStatus.OK);
    }

    @RequestMapping(method = GET, path = "")
    public @ResponseBody List<Log> getAll() throws Exception{
        System.out.println("getting all logs");

        try{
            return repo.findAll();
        } catch(Exception e){
            e.printStackTrace();
        }

        return null;
    }

    @RequestMapping(method = GET, path = "/{id}")
    public Log getOne(@PathVariable Long id) throws Exception{
        Log log = null;
        try{
            System.out.println("getting specific log");
            log = repo.getOne(id);
        } catch(Exception e){
            e.printStackTrace();
        }

        return log;
    }
}
