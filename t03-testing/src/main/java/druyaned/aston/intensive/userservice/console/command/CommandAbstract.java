package druyaned.aston.intensive.userservice.console.command;

import druyaned.aston.intensive.userservice.dao.UserDao;
import jakarta.validation.Validator;

/**
 * Some commands should interact with {@link UserDao} and {@link Validator},
 * that's why this class appeared.
 *
 * @author druyaned
 */
public abstract class CommandAbstract implements Command {

    protected final UserDao userDao;
    protected final Validator validator;

    public CommandAbstract(UserDao userDao, Validator validator) {
        this.userDao = userDao;
        this.validator = validator;
    }
}
