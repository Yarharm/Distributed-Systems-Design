package exceptions;

public class ItemWasNeverPurchasedException extends Exception {
    public ItemWasNeverPurchasedException(String errorMessage) {
        super(errorMessage);
    }
}
