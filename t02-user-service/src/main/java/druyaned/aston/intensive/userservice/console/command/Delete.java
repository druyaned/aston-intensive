package druyaned.aston.intensive.userservice.console.command;

import druyaned.aston.intensive.userservice.dao.UserDao;
import druyaned.aston.intensive.userservice.model.UserEntity;

/**
 * Deletes an existing user by the id from the database.
 *
 * @author druyaned
 */
public class Delete implements Command {

    public static final String CODE = "d";

    public static final String DESCRIPTION
            = "'" + CODE + " [ID]' - delete user by id";

    protected final UserDao userDao;

    public Delete(UserDao userDao) {
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

        // Delete user
        UserEntity user = userDao.find(id);
        if (user == null) {
            return "There is no user with id=" + id;
        }
        userDao.delete(user);

        return "Deleted by id=" + id;
    }
}
