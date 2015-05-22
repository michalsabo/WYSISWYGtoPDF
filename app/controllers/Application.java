package controllers;

import models.Person;
import models.utils.AppException;
import play.Logger;
import play.data.Form;
import play.data.validation.Constraints;
import play.i18n.Messages;
import play.mvc.Controller;
import play.mvc.Result;
import views.html.index;
import static play.data.Form.form;

/**
 * Login and Logout.
 * User: yesnault
 */
public class Application extends Controller {


    public static Result GO_HOME = redirect(
            routes.Application.index()
    );

    public static Result GO_DASHBOARD = redirect(
            routes.Templating.index()
    );


    /**
     * Display the login page or dashboard if connected
     *
     * @return login page or dashboard
     */
    public static Result index() {
        // Check that the email matches a confirmed user before we redirect
        String email = ctx().session().get("email");
        if (email != null) {
            Person person = Person.findByEmail(email);
            if (person != null && person.validated) {
                return GO_DASHBOARD;
            } else {
                Logger.debug("Clearing invalid session credentials");
                session().clear();
            }
        }

        return ok(index.render(form(Register.class), form(Login.class)));
    }


    /**
     * Login class used by Login Form.
     */
    public static class Login {

        @Constraints.Required
        public String email;
        @Constraints.Required
        public String password;

        /**
         * Validate the authentication.
         *
         * @return null if validation ok, string with details otherwise
         */
        public String validate() {

            Person person = null;
            try {
                person = Person.authenticate(email, password);
            } catch (AppException e) {
                return Messages.get("error.technical");
            }
            if (person == null) {
                return Messages.get("invalid.user.or.password");
            } else if (!person.validated) {
                return Messages.get("account.not.validated.check.mail");
            }
            return null;
        }

    }


    /**
     * Create a tempalte.
     */
    public static class CreateTemplate {

        @Constraints.Required
        public String name;
        @Constraints.Required
        public String template;
        @Constraints.Required
        public String parameters;


        public String validate() {
            return null;
        }

    }

    public static class Register {


        @Constraints.Required
        @Constraints.Email
        public String email;

        @Constraints.Required
        public String fullname;

        @Constraints.MinLength(6)
        @Constraints.Required
        public String inputPassword;

        /**
         * Validate the authentication.
         *
         * @return null if validation ok, string with details otherwise
         */
        public String validate() {
            if (isBlank(email)) {
                return "Email is required";
            }

            if (!isUniqueEmail()) {
                return "Email already exists";
            }

            if (isBlank(fullname)) {
                return "Full name is required";
            }

            if (isBlank(inputPassword)) {
                return "Password is required";
            }


            return null;
        }

        private boolean isBlank(String input) {
            return input == null || input.isEmpty() || input.trim().isEmpty();
        }

        private boolean isUniqueEmail() {
            return  !Person.checkEmailExists(email);

        }
    }

    /**
     * Handle login form submission.
     *
     * @return Dashboard if auth OK or login form if auth KO
     */
    public static Result authenticate() {
        Form<Login> loginForm = form(Login.class).bindFromRequest();

        Form<Register> registerForm = form(Register.class);

        if (loginForm.hasErrors()) {
            return badRequest(index.render(registerForm, loginForm));
        } else {
            session("email", loginForm.get().email);
            return GO_DASHBOARD;
        }
    }

    /**
     * Logout and clean the session.
     *
     * @return Index page
     */
    public static Result logout() {
        session().clear();
        flash("success", Messages.get("youve.been.logged.out"));
        return GO_HOME;
    }

}