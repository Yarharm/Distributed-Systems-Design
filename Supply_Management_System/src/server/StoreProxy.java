package server;

import communicate.ICommunicate;

import java.util.Date;
import java.util.List;

/*
    StoreProxy is a wrapper on top of Store which performs any necessary validation prior
    executing a concrete operation.
    StoreProxy uses Proxy protection pattern.
    StoreProxy contains a concrete Store through decomposition and is the only API which allows
    communication with a store.
 */
public class StoreProxy implements ICommunicate {
    private enum UserRole {
        M('M'), U('U');
        char role;
        UserRole(char role) {
            this.role = role;
        }
        public char getRole() {
            return this.role;
        }
    }

    private Store store;
    private String location;
    public StoreProxy(String location) {
        super();
        this.store = new Store(location);
        this.location = location;
    }

    @Override
    public boolean addItem(String managerID, String itemID, String itemName, int quantity, int price) {
        return true;
    }

    @Override
    public void removeItem(String managerID, String itemID, int quantity, boolean removeCompletely) {
        return;
    }

    @Override
    public List<String> listItemAvailability(String managerID) {
        return this.store.listItemAvailability(managerID);
    }

    @Override
    public boolean purchaseItem(String customerID, String itemID, Date dateOfPurchase) {
        return true;
    }

    @Override
    public List<String> findItem(String customerID, String itemName) {
        return null;
    }

    @Override
    public boolean returnItem(String customerID, String itemID, Date dateOfReturn) {
        return true;
    }

    public void initializeStore() {
        Runnable startStore = () -> {
            this.store.listen();
        };
        Thread storeThread = new Thread(startStore);
        storeThread.start();
    }

    private void validateUser(String userID, UserRole expectedRole) throws IncorrectUserRoleException {
        char currentRole = userID.charAt(2);
        if(currentRole != expectedRole.getRole()) {
            throw new IncorrectUserRoleException("Invalid User ID");
        }
    }

    private class IncorrectUserRoleException extends Exception {
        public IncorrectUserRoleException(String errorMessage) {
            super(errorMessage);
        }
    }
}
