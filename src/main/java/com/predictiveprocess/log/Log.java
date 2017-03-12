package com.predictiveprocess.log;

import lombok.Data;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;

/**
 * Created by kerwin on 3/11/17.
 */
@Entity
@Data
public class Log {
    @Id
    @GeneratedValue(strategy= GenerationType.AUTO)
    Long id;
    public String name;
    public String path;
    public String description;

    public Log(String name, String path, String description) {
        this.name = name;
        this.path = path;
        this.description = description;
    }

}
