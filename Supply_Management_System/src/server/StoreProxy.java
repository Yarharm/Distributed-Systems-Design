package server;

import communicate.ICommunicate;
import communicate.Item;
import exceptions.IncorrectUserRoleException;

import java.io.IOException;
import java.util.Date;
import java.util.List;
import java.util.logging.FileHandler;
import java.util.logging.Handler;
import java.util.logging.Logger;

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
    private String locationName;
    private Logger logger;
    public StoreProxy(String locationName) {
        super();
        this.logger = Logger.getLogger(locationName);
        this.store = new Store(locationName, this.logger);
        this.locationName = locationName;

    }

    @Override
    public Item addItem(String managerID, String itemID, String itemName, int quantity, int price) throws IncorrectUserRoleException {
        try {
            this.validateUser(managerID, UserRole.M);
        } catch(IncorrectUserRoleException e) {
            Item item = new Item(itemID, itemName, quantity, price);
            this.logger.severe("Permission alert! Customer with ID: " + managerID +
                    " was trying add the following item: " + item.toString());
            throw new IncorrectUserRoleException(e.getMessage());
        }

        Item item = this.store.addItem(managerID, itemID, itemName, quantity, price);
        return item;
    }

    @Override
    public Item removeItem(String managerID, String itemID, int quantity) throws IncorrectUserRoleException {
        try {
            this.validateUser(managerID, UserRole.M);
        } catch(IncorrectUserRoleException e) {
            String msg = quantity == -1 ? "completely remove" : "remove " + quantity + " units from";
            this.logger.severe("Permission alert! Customer with ID: " + managerID +
                    " was trying to " + msg + " an item with ID: " + itemID);
            throw new IncorrectUserRoleException(e.getMessage());
        }
        Item item = this.store.removeItem(managerID, itemID, quantity);
        return item;
    }

    @Override
    public List<Item> listItemAvailability(String managerID) {
        return this.store.listItemAvailability(managerID);
    }

    @Override
    public boolean purchaseItem(String customerID, String itemID, Date dateOfPurchase) {
        return true;
    }

    @Override
    public List<Item> findItem(String customerID, String itemName) {
        return null;
    }

    @Override
    public boolean returnItem(String customerID, String itemID, Date dateOfReturn) {
        return true;
    }

    public void initializeStore(int port) {
        Runnable startStore = () -> {
            this.store.listen(port);
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

    public void setupLogger() throws IOException {
        String logFile = this.locationName + ".log";
        Handler fileHandler  = new FileHandler("/Users/yaroslav/school/423/Distributed-Systems-Design/" +
                "Supply_Management_System/logs/stores/" + logFile);
        this.logger.setUseParentHandlers(false);
        this.logger.addHandler(fileHandler);
    }
}
