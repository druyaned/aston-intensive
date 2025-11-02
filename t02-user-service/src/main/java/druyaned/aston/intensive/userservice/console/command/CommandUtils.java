package druyaned.aston.intensive.userservice.console.command;

import druyaned.aston.intensive.userservice.model.User;
import jakarta.validation.ConstraintViolation;
import java.util.Set;

/**
 * Provides some static utilities for commands, exempli gratia common
 * {@link violationOutput violation output}.
 * 
 * @author druyaned
 */
public class CommandUtils {
    
    /**
     * Verifies the given input, which must be not null and not empty,
     * as well as each its element, and the length of the input must be
     * equal to the given length.
     * 
     * @param input to be verified
     * @param length expected length of the input, more than 0
     * @return null in case of accepted input, otherwise a violation message
     *   to be returned by the command
     */
    public static String verifyInput(String[] input, int length) {
        if (input == null || input.length == 0 || input.length != length) {
            return "Bad input";
        }
        for (String part : input) {
            if (part == null || part.isEmpty()) {
                return "Bad input";
            }
        }
        return null;
    }
    
    /**
     * Returns a violation report as a string, including all violations;
     * the set must not be empty.
     * 
     * @param set to produce the violation output; must not be empty
     * @return violation output of all violations
     */
    public static String violationOutput(Set<ConstraintViolation<User>> set) {
        StringBuilder sb = new StringBuilder();
        sb.append("Constraint violations:");
        for (ConstraintViolation<User> violation : set) {
            sb.append("\nProperty_: ").append(violation.getPropertyPath());
            sb.append("\n  Value__: ").append(violation.getInvalidValue());
            sb.append("\n  Message: ").append(violation.getMessage());
        }
        return sb.toString();
    }
    
    /**
     * Returns fields of the user through spaces to construct the output for
     * a command.
     * 
     * @param user to construct the output from its fields
     * @return fields of the user through spaces
     */
    public static String outputUser(User user) {
        StringBuilder sb = new StringBuilder();
        sb
                .append("id=").append(user.getId())
                .append(" name=").append(user.getName())
                .append(" email=").append(user.getEmail());
        if (user.getBirthdate() != null) {
            sb.append(" birthdate=").append(user.getBirthdate());
        }
        sb.append(" createdAt=").append(user.getCreatedAt());
        return sb.toString();
    }
}
