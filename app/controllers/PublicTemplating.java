package controllers;
import com.fasterxml.jackson.databind.node.ObjectNode;
import org.apache.commons.codec.binary.Base64;
import com.fasterxml.jackson.databind.JsonNode;
import models.Template;
import models.Person;
import it.innove.play.pdf.PdfGenerator;
import play.libs.Json;
import play.mvc.BodyParser;
import play.mvc.Controller;
import play.mvc.Result;

import play.Logger;
import views.html.documents.document;

import java.util.Iterator;


/**
 * Created by michal on 25.4.2015.
 */
public class PublicTemplating extends Controller {


    @BodyParser.Of(BodyParser.Json.class)
    public static Result documentsJson() throws Exception {

        JsonNode json = request().body().asJson();
        if (json == null) {

            ObjectNode result = Json.newObject();
            result.put("status", "ERROR");
            result.put("code", 400);
            result.put("message", "Not valid json");
            return ok(Json.toJson(result));
        }

        String token = json.findPath("token").textValue();
        String name_template = json.findPath("name").textValue();
        JsonNode parameters = json.findPath("parameters");

        if (token == null || name_template == null || parameters == null) {
            ObjectNode result = Json.newObject();
            result.put("status", "ERROR");
            result.put("code", 400);
            result.put("message", "Not enought parameters");
            return ok(Json.toJson(result));
        }

        Template modified_template = Template.findByName(name_template);

        if (modified_template == null) {
            ObjectNode result = Json.newObject();
            result.put("status", "ERROR");
            result.put("code", 400);
            result.put("message", "Template does not exists");
            return ok(Json.toJson(result));
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
            ObjectNode result = Json.newObject();
            result.put("status", "ERROR");
            result.put("code", 400);
            result.put("message", "Token is not valid");
            return ok(Json.toJson(result));
        }



        String output_html = modified_template.html;
        Logger.debug(output_html);


        Iterator<JsonNode> it = parameters.iterator();

        while (it.hasNext()) {
            JsonNode param = it.next();
            String name = param.findPath("name").textValue();
            String value = param.findPath("value").textValue();
            output_html = output_html.replaceAll("\\$\\$" + name + "\\$\\$" , value);

        }

        byte[] encodedBytes  = PdfGenerator.toBytes(new play.twirl.api.Html(output_html), "http://localhost:9000");
        byte[] encoded64Bytes = Base64.encodeBase64(encodedBytes);


        String pdf = new String(encoded64Bytes, "UTF-8");

        ObjectNode result = Json.newObject();
        result.put("status", "OK");
        result.put("code", 200);
        result.put("result", pdf);
        return ok(result);
    }



}
