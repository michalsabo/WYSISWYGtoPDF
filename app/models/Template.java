package models;

import play.db.ebean.Model;

import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Lob;
/**
 * Created by michal on 25.4.2015.
 */
@Entity
public class Template extends Model {

    @Id
    public Long id;

    public String name;

    @Lob
    public String html;

    public String parameters;

    public String owner;

    public String groups;

    public static Finder<Long, Template> find = new Finder<Long, Template>(Long.class, Template.class);


    public static Template findById(Long id) {
        return find.where().eq("id", id).findUnique();
    }

    public static Template findByName(String name) {
        return find.where().eq("name", name).findUnique();
    }

}
