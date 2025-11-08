package druyaned.aston.intensive.userservice.console.command;

import druyaned.aston.intensive.userservice.dao.UserDao;
import druyaned.aston.intensive.userservice.model.UserEntity;

/**
 * Reads a user by the given ID from the database.
 *
 * @author druyaned
 */
public class Read implements Command {

    public static final String CODE = "r";

    public static final String DESCRIPTION
            = "'" + CODE + " [ID]' - read user by id";

    protected final UserDao userDao;

    public Read(UserDao userDao) {
        this.userDao = userDao;
    }

    @Override
    public String code() {
        return CODE;
    }

    @Override
    public String execute(String[] input) {
        String violationMessage = CommandUtils.verifyInput(input, 2);
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

        // Get user
        UserEntity user = userDao.find(id);
        return user == null
                ? "There is no user with id=" + id
                : "Found user: " + CommandUtils.outputUser(user);
    }
}
