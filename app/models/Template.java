package models;

import play.db.ebean.Model;

import javax.persistence.Entity;
import javax.persistence.Id;

/**
 * Created by michal on 25.4.2015.
 */
@Entity
public class Template extends Model {

    @Id
    public Long id;

    public String name;

    public String html;

    public String parameters;



}
