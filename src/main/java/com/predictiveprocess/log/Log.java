package com.predictiveprocess.log;

import lombok.*;
import org.springframework.hateoas.ResourceSupport;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import java.io.Serializable;

/**
 * Created by kerwin on 3/11/17.
 */
@Entity
@EqualsAndHashCode(callSuper = false)
@ToString
@NoArgsConstructor(access = AccessLevel.PROTECTED, force = true)
@AllArgsConstructor(staticName = "of")
public class Log extends org.springframework.hateoas.ResourceSupport {
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
