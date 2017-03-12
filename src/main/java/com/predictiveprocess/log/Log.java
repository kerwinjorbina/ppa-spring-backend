package com.predictiveprocess.log;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.*;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import java.io.Serializable;

/**
 * Created by kerwin on 3/11/17.
 */
@Entity
@Data
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED, force = true)
@AllArgsConstructor(staticName = "of")
@JsonIgnoreProperties({"hibernateLazyInitializer", "handler"})
public class Log implements Serializable{
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

    public Long getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public String getPath() {
        return path;
    }

    public String getDescription() {
        return description;
    }


}
