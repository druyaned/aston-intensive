package druyaned.aston.intensive.userservice.console.command;

import druyaned.aston.intensive.userservice.dao.UserDao;
import druyaned.aston.intensive.userservice.model.UserEntity;
import java.util.List;

/**
 * Reads all users from the database.
 *
 * @author druyaned
 */
public class ReadAll implements Command {

    public static final String CODE = "a";

    public static final String DESCRIPTION
            = "'" + CODE + "' - read all users";

    protected final UserDao userDao;

    public ReadAll(UserDao userDao) {
        this.userDao = userDao;
    }

    @Override
    public String code() {
        return CODE;
    }

    @Override
    public String execute(String[] input) {
        String violationMessage = CommandUtils.verifyInput(input, 1);
        if (violationMessage != null) {
            return violationMessage;
        }

        List<UserEntity> users = userDao.findAll();
        if (users.isEmpty()) {
            return "There are no users yet";
        }

        StringBuilder sb = new StringBuilder();
        sb.append("All users:");
        for (int i = 0; i < users.size(); i++) {
            sb
                    .append("\n  ")
                    .append(Integer.toString(i + 1))
                    .append(") ")
                    .append(CommandUtils.outputUser(users.get(i)));
        }
        return sb.toString();
    }
}
