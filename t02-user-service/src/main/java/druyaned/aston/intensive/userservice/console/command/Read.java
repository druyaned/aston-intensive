package druyaned.aston.intensive.userservice.console.command;

import druyaned.aston.intensive.userservice.dao.UserDao;
import druyaned.aston.intensive.userservice.model.User;
import jakarta.validation.Validator;

/**
 * Reads a user by the given ID from the database.
 * 
 * @author druyaned
 */
public class Read extends CommandAbstract {
    
    public static final String CODE = "r";
    
    public static final String DESCRIPTION
            = "'" + CODE + " [ID]' - read user by id";
    
    public Read(UserDao userDao, Validator validator) {
        super(userDao, validator);
    }
    
    @Override
    public String code() {
        return CODE;
    }
    
    @Override
    public String execute(String[] input) {
        // Deal with input
        String violationMessage = CommandUtils.verifyInput(input, 2);
        if (violationMessage != null) {
            return violationMessage;
        }
        String idStr = input[1];
        Long id;
        try {
            id = Long.valueOf(idStr);
        } catch (NumberFormatException exc) {
            return "Id '" + idStr + "' can't be parsed";
        }
        // Deal with user and DAO
        User user = userDao.find(id);
        if (user == null) {
            return "There is no user with id=" + id;
        } else {
            return "Found user: " + CommandUtils.outputUser(user);
        }
    }
}
