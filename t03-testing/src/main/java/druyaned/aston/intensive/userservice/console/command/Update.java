package druyaned.aston.intensive.userservice.console.command;

import druyaned.aston.intensive.userservice.dao.UserDao;
import druyaned.aston.intensive.userservice.model.UserEntity;
import jakarta.validation.ConstraintViolation;
import jakarta.validation.Validator;
import java.time.LocalDate;
import java.time.format.DateTimeParseException;
import java.util.Set;

/**
 * Updates an existing user by the given input and puts it into the database.
 *
 * @author druyaned
 */
public class Update extends CommandAbstract {

    public static final String CODE = "u";

    public static final String DESCRIPTION
            = "'" + CODE + " [ID] [NAME] [EMAIL] [BIRTHDATE/-]'"
            + " - update user by id, name, email and birthdate";

    public Update(UserDao userDao, Validator validator) {
        super(userDao, validator);
    }

    @Override
    public String code() {
        return CODE;
    }

    @Override
    public String execute(String[] input) {
        String violationMessage = CommandUtils.verifyInput(input, 5);
        if (violationMessage != null) {
            return violationMessage;
        }

        // Extract arguments
        String idStr = input[1];
        Long id;
        try {
            id = Long.valueOf(idStr);
        } catch (NumberFormatException exc) {
            return "Id '" + idStr + "' can't be parsed";
        }

        String name = input[2];
        String email = input[3];
        String birthdateStr = input[4];

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

        // Update user
        UserEntity newUser = new UserEntity();
        newUser.setName(name);
        newUser.setEmail(email);
        newUser.setBirthdate(birthdate);

        Set<ConstraintViolation<UserEntity>> violations
                = validator.validate(newUser);
        if (violations.isEmpty()) {
            UserEntity user = userDao.find(id);

            if (user == null) {
                return "There is no user with id=" + id;
            }
            if (!email.equals(user.getEmail())
                    && userDao.emailExists(email)) {

                return "Email '" + email + "' already exists";
            }

            user.setName(name);
            user.setEmail(email);
            user.setBirthdate(birthdate);
            userDao.update(user);

            return "Updated: " + CommandUtils.outputUser(user);

        } else {
            return CommandUtils.violationOutput(violations);
        }
    }
}
