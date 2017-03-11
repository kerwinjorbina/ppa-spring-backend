package com.predictiveprocess.log;

import lombok.Data;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;

/**
 * Created by kerwin on 3/11/17.
 */
@Entity
@Data
public class Log {
    @Id
    @GeneratedValue
    Long id;
    public String name;
    public String path;

    public String description;

    public Log(long id, String name, String path) {
        this.id = id;
        this.name = name;
        this.path = path;
    }

}
