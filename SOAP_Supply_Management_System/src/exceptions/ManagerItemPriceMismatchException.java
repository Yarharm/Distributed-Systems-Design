package exceptions;

public class ManagerItemPriceMismatchException extends Exception {
    public ManagerItemPriceMismatchException(String errorMessage) {
        super(errorMessage);
    }
}
