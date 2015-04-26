package controllers;

import models.Template;
import models.User;
import it.innove.play.pdf.PdfGenerator;
import play.libs.Json;
import play.mvc.Controller;
import play.mvc.Result;
import play.mvc.Security;
import views.html.account.signup.created;
import views.html.documents.document;
import views.html.templates.edit.edit;
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
        List<Template> templates = Template.find.where().like("owner", request().username()).findList();
        return ok(Json.toJson(templates));
    }

    public static Result getAddedTemplates() {
        List<Template> templates = Template.find.where().ilike("groups", "%" + request().username() + "%") .findList();
        return ok(Json.toJson(templates));
    }

    public static Result showTemplates() {
        return ok(show.render(User.findByEmail(request().username())));
    }

    public static Result editTemplate(Long token) {
        Template modified_tempalte = Template.findById(token);
        return ok(edit.render(User.findByEmail(request().username()), form(Template.class).fill(modified_tempalte)));
    }

    public static Result document(Long token) {
        Template template = Template.findById(token);
        return PdfGenerator.ok(document.render(new play.twirl.api.Html(template.html)), "http://localhost:9000");
    }

    public static Result documentString(String token) {
        return PdfGenerator.ok(document.render(new play.twirl.api.Html(token)), "http://localhost:9000");
    }

    public static Result storeEditTemplate(Long token) {
        Template templateForm = Form.form(Template.class).bindFromRequest().get();
        Template modified_template = Template.findById(token);
        modified_template.groups = templateForm.groups;
        modified_template.html = templateForm.html;
        modified_template.name = templateForm.name;
        modified_template.parameters = templateForm.parameters;
        modified_template.save();
        return ok(show.render(User.findByEmail(request().username())));
    }

    public static Result storeTemplate() {
        Template templateForm = Form.form(Template.class).bindFromRequest().get();
        templateForm.owner = request().username();
        templateForm.save();
        return ok(created.render());
    }


}
