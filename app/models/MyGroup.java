package models;

import play.db.ebean.Model;

import javax.persistence.Entity;
import javax.persistence.Id;

/**
 * Created by michal on 26.4.2015.
 */
@Entity
public class MyGroup extends Model{
    @Id
    public Long id;

    public String name;

    public String owner;

    public String members;

    public static Model.Finder<Long, MyGroup> find = new Model.Finder<Long, MyGroup>(Long.class, MyGroup.class);


    public static MyGroup findById(Long id) {
        return find.where().eq("id", id).findUnique();
    }
}
