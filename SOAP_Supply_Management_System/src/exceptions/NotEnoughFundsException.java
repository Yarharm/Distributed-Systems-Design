package exceptions;

public class NotEnoughFundsException extends Exception {
    public NotEnoughFundsException(String errorMessage) {
        super(errorMessage);
    }
}