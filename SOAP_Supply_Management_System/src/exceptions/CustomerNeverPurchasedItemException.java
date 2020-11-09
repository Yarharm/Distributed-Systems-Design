package exceptions;

public class CustomerNeverPurchasedItemException extends Exception {
    public CustomerNeverPurchasedItemException(String errorMessage) {
        super(errorMessage);
    }
}
