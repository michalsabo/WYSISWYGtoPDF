package controllers;

import models.MyGroup;
import models.Template;
import models.User;
import play.data.Form;
import play.db.ebean.Model;
import play.libs.Json;
import play.mvc.Controller;
import play.mvc.Result;
import play.mvc.Security;
import views.html.groups.create.create;
import views.html.groups.edit.edit;
import views.html.groups.show.show;
import java.util.List;
import java.util.logging.Logger;

import static play.data.Form.form;

/**
 * Created by michal on 26.4.2015.
 */
@Security.Authenticated(Secured.class)
public class Grouping extends Controller {
    public static Result index() {
        return ok(create.render(User.findByEmail(request().username()), form(MyGroup.class)));
    }

    public static Result showGroups() {
        return ok(show.render(User.findByEmail(request().username()), ""));

    }

    public static Result editGroup(Long token) {
        MyGroup modified_group = MyGroup.findById(token);
        return ok(edit.render(User.findByEmail(request().username()), form(MyGroup.class).fill(modified_group)));
    }

    public static Result storeEditGroup(Long token) {
        MyGroup groupForm = Form.form(MyGroup.class).bindFromRequest().get();

        String action = Form.form().bindFromRequest().get("action");
        MyGroup modified_group = MyGroup.findById(token);
        if ("edit".equals(action)) {
            modified_group.name = groupForm.name;
            modified_group.members = groupForm.members;
            modified_group.save();
            return ok(show.render(User.findByEmail(request().username()), ""));
        } else {
            modified_group.delete();
            return ok(show.render(User.findByEmail(request().username()), "Group was deleted"));

        }
    }


    public static Result getGroups() {
        List<MyGroup> groups =  MyGroup.find.where().like("owner", request().username()).findList();
        return ok(Json.toJson(groups));
    }

    public static Result getAddedGroups() {
        List<MyGroup> groups =  MyGroup.find.where().ilike("members", "%" + request().username() + "%").findList();
        return ok(Json.toJson(groups));
    }

    public static Result storeGroup() {
        MyGroup groupForm = Form.form(MyGroup.class).bindFromRequest().get();
        groupForm.owner = request().username();
        groupForm.save();
        return ok(show.render(User.findByEmail(request().username()), "New group was sucesfully created"));
    }
}
