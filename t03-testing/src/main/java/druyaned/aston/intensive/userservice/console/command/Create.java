package druyaned.aston.intensive.userservice.console.command;

import druyaned.aston.intensive.userservice.dao.UserDao;
import druyaned.aston.intensive.userservice.model.UserEntity;
import jakarta.validation.ConstraintViolation;
import jakarta.validation.Validator;
import java.time.LocalDate;
import java.time.OffsetDateTime;
import java.time.format.DateTimeParseException;
import java.util.Set;

/**
 * Creates a new user by the given input and stores it in the database.
 *
 * @author druyaned
 */
public class Create extends CommandAbstract {

    public static final String CODE = "c";

    public static final String DESCRIPTION
            = "'" + CODE + " [NAME] [EMAIL] [BIRTHDATE/-]'"
            + " - create user by name, email and birthdate"
            + " ('YYYY-MM-DD' or '-')";

    public Create(UserDao userDao, Validator validator) {
        super(userDao, validator);
    }

    @Override
    public String code() {
        return CODE;
    }

    @Override
    public String execute(String[] input) {
        String violationMessage = CommandUtils.verifyInput(input, 4);
        if (violationMessage != null) {
            return violationMessage;
        }

        // Extract arguments
        String name = input[1];
        String email = input[2];
        if (userDao.emailExists(email)) {
            return "Email '" + email + "' already exists";
        }

        String birthdateStr = input[3];
        LocalDate birthdate;
        if (birthdateStr.equals("-")) {
            birthdate = null;
        } else {
            try {
                birthdate = LocalDate.parse(birthdateStr);

            } catch (DateTimeParseException exc) {
                return "Birthdate '" + birthdateStr + "' can't be parsed";
            }
        }

        // Create user
        UserEntity newUser = new UserEntity();
        newUser.setName(name);
        newUser.setEmail(email);
        newUser.setBirthdate(birthdate);
        newUser.setCreatedAt(OffsetDateTime.now());

        Set<ConstraintViolation<UserEntity>> violations
                = validator.validate(newUser);
        if (violations.isEmpty()) {
            userDao.save(newUser);
            return "Created: " + CommandUtils.outputUser(newUser);
        } else {
            return CommandUtils.violationOutput(violations);
        }
    }
}
