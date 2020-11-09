package exceptions;

public class IncorrectUserRoleException extends Exception {
    public IncorrectUserRoleException(String errorMessage) {
        super(errorMessage);
    }
}