package controllers.account.settings;

import controllers.Secured;
import models.Token;
import models.Person;
import play.Logger;
import play.i18n.Messages;
import play.mvc.Controller;
import play.mvc.Result;
import play.mvc.Security;
import views.html.account.settings.password;

import java.net.MalformedURLException;

/**
 * User: yesnault
 * Date: 15/05/12
 */
@Security.Authenticated(Secured.class)
public class Password extends Controller {

    /**
     * Password Page. Ask the user to change his password.
     *
     * @return index settings
     */
    public static Result index() {
        return ok(password.render(Person.findByEmail(request().username())));
    }

    /**
     * Send a mail with the reset link.
     *
     * @return password page with flash error or success
     */
    public static Result runPassword() {
        Person person = Person.findByEmail(request().username());
        try {
            Token.sendMailResetPassword(person);
            flash("success", Messages.get("resetpassword.mailsent"));
            return ok(password.render(person));
        } catch (MalformedURLException e) {
            Logger.error("Cannot validate URL", e);
            flash("error", Messages.get("error.technical"));
        }
        return badRequest(password.render(person));
    }
}
