package controllers;
import org.apache.commons.codec.binary.Base64;
import com.fasterxml.jackson.databind.JsonNode;
import models.Template;
import models.Person;
import it.innove.play.pdf.PdfGenerator;
import play.libs.Json;
import play.mvc.BodyParser;
import play.mvc.Controller;
import play.mvc.Result;
import play.mvc.Security;
import views.html.documents.document;
import views.html.templates.edit.edit;
import views.html.templates.show.show;
import views.html.templates.create.create;
import play.data.Form;
import play.Logger;

import java.util.Iterator;
import java.util.List;


import static play.data.Form.form;

/**
 * Created by michal on 25.4.2015.
 */
@Security.Authenticated(Secured.class)
public class Templating extends Controller {
    public static Result index() {
        return ok(create.render(Person.findByEmail(request().username()), form(Template.class)));
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
        return ok(show.render(Person.findByEmail(request().username()), ""));
    }

    public static Result editTemplate(Long token) {
        Template modified_tempalte = Template.findById(token);
        return ok(edit.render(Person.findByEmail(request().username()), form(Template.class).fill(modified_tempalte)));
    }

    public static Result document(Long token) {
        Template template = Template.findById(token);
        return PdfGenerator.ok(document.render(new play.twirl.api.Html(template.html)), "http://localhost:9000");
    }


    @BodyParser.Of(BodyParser.Json.class)
    public static Result documentsJson() {

        JsonNode json = request().body().asJson();

        String token = json.findPath("token").textValue();
        String name_template = json.findPath("name").textValue();
        JsonNode params = json.findPath("params");
        if (token == null || name_template == null || params == null) {
            return ok(Json.toJson(new String("Not enough parameters")));
        }


        Template modified_template = Template.findByName(name_template);

        if (modified_template == null) {
            Logger.debug("NUUUL");
            return ok(Json.toJson(new String("Template does not exists")));
        }

        String owner = modified_template.owner;
        String group = modified_template.groups;


        Boolean authorized = false;

        Person user = Person.findByEmail(owner);
        if (user.webserviceToken.equals(token)) {
            authorized = true;
        }

        if (!group.isEmpty()) {
            user = Person.findByEmail(group);
            if (user != null) {
                if (user.webserviceToken.equals(token)) {
                    authorized = true;
                }
            }
        }


        if (!authorized) {
            return ok(Json.toJson(new String("Token is not valid")));
        }



        String output_html = modified_template.html;

        Iterator<JsonNode> it = params.iterator();

        while (it.hasNext()) {
            JsonNode param = it.next();
            String name = param.findPath("name").textValue();
            String value = param.findPath("value").textValue();
            output_html = output_html.replaceAll("\\$\\$" + name + "\\$\\$" , value);

        }
        byte[] encodedBytes  = PdfGenerator.toBytes(new play.twirl.api.Html(output_html), "http://localhost:9000");

        byte[] encoded64Bytes = Base64.encodeBase64(encodedBytes);


        return ok(Json.toJson(new String(encoded64Bytes)));
        }


    public static Result documentString(String token) {
        return PdfGenerator.ok(document.render(new play.twirl.api.Html(token)), "http://localhost:9000");
    }

    public static Result storeEditTemplate(Long token) {
        Template templateForm = Form.form(Template.class).bindFromRequest().get();
        String action = Form.form().bindFromRequest().get("action");

        Template modified_template = Template.findById(token);
        if( "edit".equals(action)) {
            modified_template.groups = templateForm.groups;
            modified_template.html = templateForm.html;
            modified_template.name = templateForm.name;
            modified_template.parameters = templateForm.parameters;
            modified_template.save();
            return ok(show.render(Person.findByEmail(request().username()), ""));
        } else {
            modified_template.delete();
            return ok(show.render(Person.findByEmail(request().username()), "Template was deleted"));
        }
    }

    public static Result storeTemplate() {
        Template templateForm = Form.form(Template.class).bindFromRequest().get();
        templateForm.owner = request().username();
        templateForm.save();
        return ok(show.render(Person.findByEmail(request().username()), "New template was sucessfully created"));
    }


}
