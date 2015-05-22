package controllers.account.settings;

import controllers.Secured;
import models.Token;
import models.Person;
import play.Logger;
import play.data.Form;
import play.data.validation.Constraints;
import play.i18n.Messages;
import play.mvc.Controller;
import play.mvc.Result;
import play.mvc.Security;
import views.html.account.settings.email;
import views.html.account.settings.emailValidate;

import java.net.MalformedURLException;

import static play.data.Form.form;

/**
 * Settings -> Email page.
 * <p/>
 * User: yesnault
 * Date: 23/06/12
 */
@Security.Authenticated(Secured.class)
public class Email extends Controller {

    public static class AskForm {
        @Constraints.Required
        public String email;
        public AskForm() {}
        AskForm(String email) {
            this.email = email;
        }
    }

    /**
     * Password Page. Ask the user to change his password.
     *
     * @return index settings
     */
    public static Result index() {
        Person person = Person.findByEmail(request().username());
        Form<AskForm> askForm = form(AskForm.class);
        askForm = askForm.fill(new AskForm(person.email));
        return ok(email.render(Person.findByEmail(request().username()), askForm));
    }

    /**
     * Send a mail to confirm.
     *
     * @return email page with flash error or success
     */
    public static Result runEmail() {
        Form<AskForm> askForm = form(AskForm.class).bindFromRequest();
        Person person = Person.findByEmail(request().username());

        if (askForm.hasErrors()) {
            flash("error", Messages.get("signup.valid.email"));
            return badRequest(email.render(person, askForm));
        }

        try {
            String mail = askForm.get().email;
            Token.sendMailChangeMail(person, mail);
            flash("success", Messages.get("changemail.mailsent"));
            return ok(email.render(person, askForm));
        } catch (MalformedURLException e) {
            Logger.error("Cannot validate URL", e);
            flash("error", Messages.get("error.technical"));
        }
        return badRequest(email.render(person, askForm));
    }

    /**
     * Validate a email.
     *
     * @return email page with flash error or success
     */
    public static Result validateEmail(String token) {
        Person person = Person.findByEmail(request().username());

        if (token == null) {
            flash("error", Messages.get("error.technical"));
            return badRequest(emailValidate.render(person));
        }

        Token resetToken = Token.findByTokenAndType(token, Token.TypeToken.email);
        if (resetToken == null) {
            flash("error", Messages.get("error.technical"));
            return badRequest(emailValidate.render(person));
        }

        if (resetToken.isExpired()) {
            resetToken.delete();
            flash("error", Messages.get("error.expiredmaillink"));
            return badRequest(emailValidate.render(person));
        }

        person.email = resetToken.email;
        person.save();

        session("email", resetToken.email);

        flash("success", Messages.get("account.settings.email.successful", person.email));

        return ok(emailValidate.render(person));
    }
}
