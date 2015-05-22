package models;

import models.utils.AppException;
import models.utils.Hash;
import play.data.format.Formats;
import play.data.validation.Constraints;
import play.db.ebean.Model;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import java.util.Date;
import java.util.UUID;

/**
 * User: yesnault
 * Date: 20/01/12
 */
@Entity
public class Person extends Model {

    @Id
    public Long id;

    @Constraints.Required
    @Formats.NonEmpty
    @Column(unique = true)
    public String email;

    @Constraints.Required
    @Formats.NonEmpty
    public String fullname;

    public String webserviceToken;
    public String confirmationToken;

    @Constraints.Required
    @Formats.NonEmpty
    public String passwordHash;

    @Formats.DateTime(pattern = "yyyy-MM-dd HH:mm:ss")
    public Date dateCreation;

    @Formats.NonEmpty
    public Boolean validated = false;

    // -- Queries (long id, user.class)
    public static Model.Finder<Long, Person> find = new Model.Finder<Long, Person>(Long.class, Person.class);

    /**
     * Retrieve a user from an email.
     *
     * @param email email to search
     * @return a user
     */
    public static Person findByEmail(String email) {
        return find.where().eq("email", email).findUnique();
    }

    /**
     * Retrieve a user from a fullname.
     *
     * @param fullname Full name
     * @return a user
     */
    public static Person findByFullname(String fullname) {
        return find.where().eq("fullname", fullname).findUnique();
    }

    /**
     * Retrieves a user from a confirmation token.
     *
     * @param token the confirmation token to use.
     * @return a user if the confirmation token is found, null otherwise.
     */
    public static Person findByConfirmationToken(String token) {
        return find.where().eq("confirmationToken", token).findUnique();
    }

    /**
     * Authenticate a User, from a email and clear password.
     *
     * @param email         email
     * @param clearPassword clear password
     * @return User if authenticated, null otherwise
     * @throws AppException App Exception
     */
    public static Person authenticate(String email, String clearPassword) throws AppException {

        // get the user with email only to keep the salt password
        Person person = find.where().eq("email", email).findUnique();
        if (person != null) {
            // get the hash password from the salt + clear password
            if (Hash.checkPassword(clearPassword, person.passwordHash)) {
                return person;
            }
        }
        return null;
    }

    public static boolean checkEmailExists(String email) {
        return (find.where().eq("email", email).findRowCount() > 0);
    }

    public void changeWebserviceToken() throws AppException {
        this.webserviceToken = UUID.randomUUID().toString();
        this.save();
    }

    public void changePassword(String password) throws AppException {
        this.passwordHash = Hash.createPassword(password);
        this.save();
    }

    /**
     * Confirms an account.
     *
     * @return true if confirmed, false otherwise.
     * @throws AppException App Exception
     */
    public static boolean confirm(Person person) throws AppException {
        if (person == null) {
            return false;
        }

        person.webserviceToken = UUID.randomUUID().toString();
        person.confirmationToken = null;
        person.validated = true;
        person.save();
        return true;
    }

}
