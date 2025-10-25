package druyaned.aston.intensive.userservice.console.command;

/**
 * Prints the help menu in the console.
 * 
 * @author druyaned
 */
public class HelpMenu implements Command {
    
    public static final String CODE = "h";
    
    public static final String DESCRIPTION = "'" + CODE + "' - help menu";
    
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
        StringBuilder sb = new StringBuilder();
        sb.append("Command menu:");
        sb.append("\n  " + Quit.DESCRIPTION);
        sb.append("\n  " + HelpMenu.DESCRIPTION);
        sb.append("\n  " + Create.DESCRIPTION);
        sb.append("\n  " + ReadAll.DESCRIPTION);
        sb.append("\n  " + Read.DESCRIPTION);
        sb.append("\n  " + Update.DESCRIPTION);
        sb.append("\n  " + Delete.DESCRIPTION);
        return sb.toString();
    }
}
