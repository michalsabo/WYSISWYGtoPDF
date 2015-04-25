package controllers;

import akka.event.Logging;
import models.Template;
import models.User;
import play.data.validation.Constraints;
import play.db.ebean.Model;
import play.libs.Json;
import play.mvc.Controller;
import play.mvc.Result;
import play.mvc.Security;
import views.html.account.signup.created;
import views.html.templates.show.show;
import views.html.templates.create.create;
import play.data.Form;


import java.util.List;

import static play.data.Form.form;


/**
 * Created by michal on 25.4.2015.
 */
@Security.Authenticated(Secured.class)
public class Templating extends Controller {
    public static Result index() {
        return ok(create.render(User.findByEmail(request().username()), form(Template.class)));
    }

    public static Result getTemplates() {
        List<Template> templates = new Model.Finder(String.class, Template.class).all();
        return ok(Json.toJson(templates));
    }

    public static Result showTemplates() {
        return ok(show.render());
    }


    public static Result storeTemplate() {
        Template templateForm = Form.form(Template.class).bindFromRequest().get();

        templateForm.save();


        return ok(created.render());
    }


}
