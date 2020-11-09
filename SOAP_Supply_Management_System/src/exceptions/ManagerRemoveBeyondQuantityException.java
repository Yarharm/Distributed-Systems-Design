package exceptions;

public class ManagerRemoveBeyondQuantityException extends Exception{
    public ManagerRemoveBeyondQuantityException(String errorMessage) {
        super(errorMessage);
    }
}
