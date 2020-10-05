package server;

import communicate.ICommunicate;
import communicate.Item;
import exceptions.*;

import java.io.IOException;
import java.util.Date;
import java.util.List;
import java.util.Map;
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

    private final Store store;
    private final String locationName;
    private final Logger logger;
    public StoreProxy(String locationName, Map<String, Integer> portsConfig) {
        super();
        this.logger = Logger.getLogger(locationName);
        this.store = new Store(locationName, this.logger, portsConfig);
        this.locationName = locationName;

    }

    @Override
    public Item addItem(String managerID, String itemID, String itemName, int quantity, int price) throws IncorrectUserRoleException, ManagerExternalStoreItemException, ManagerItemPriceMismatchException {
        try {
            this.validateUser(managerID, UserRole.M);
            this.validateItem(managerID, itemID);
            return this.store.addItem(managerID, itemID, itemName, quantity, price);
        }  catch (ManagerItemPriceMismatchException e) {
            this.logger.info("Manager with ID: " + managerID + " tried to add an item with ID : " + itemID + "," +
                    " but the price does not match.");
            throw new ManagerItemPriceMismatchException(e.getMessage());
        } catch(IncorrectUserRoleException e) {
            Item item = new Item(itemID, itemName, quantity, price);
            this.logger.severe("Permission alert! Customer with ID: " + managerID +
                    " was trying add the following item: " + item.toString());
            throw new IncorrectUserRoleException(e.getMessage());
        } catch(ManagerExternalStoreItemException e) {
            this.logger.severe("Permission alert! Manager with ID: " + managerID + " " +
                    "was trying to add an item " + itemID + " which belongs to a different store.");
            throw new ManagerExternalStoreItemException(e.getMessage());
        }
    }

    @Override
    public Item removeItem(String managerID, String itemID, int quantity) throws IncorrectUserRoleException, ManagerExternalStoreItemException,
            ManagerRemoveBeyondQuantityException, ManagerRemoveNonExistingItemException
    {
        try {
            this.validateUser(managerID, UserRole.M);
            this.validateItem(managerID, itemID);
            return this.store.removeItem(managerID, itemID, quantity);
        } catch(ManagerRemoveNonExistingItemException e) {
            String msg = quantity == -1 ? "completely remove" : "remove " + quantity + " units from";
            this.logger.info("Manager with ID: " + managerID + " was trying to " +
                    "" + msg + " an item with ID: " + itemID + "" +
                    ", but such an item does not exists in a store.");
            throw new ManagerRemoveNonExistingItemException(e.getMessage());
        } catch (ManagerRemoveBeyondQuantityException e) {
            this.logger.info("Manager with ID: " + managerID + " was trying to remove more quantity than exists in a store " +
                    "for the item with ID: " + itemID);
            throw new ManagerRemoveBeyondQuantityException(e.getMessage());
        } catch(IncorrectUserRoleException e) {
            String msg = quantity == -1 ? "completely remove" : "remove " + quantity + " units from";
            this.logger.severe("Permission alert! Customer with ID: " + managerID +
                    " was trying to " + msg + " an item with ID: " + itemID);
            throw new IncorrectUserRoleException(e.getMessage());
        } catch(ManagerExternalStoreItemException e) {
            this.logger.severe("Permission alert! Manager with ID: " + managerID + " " +
                    "was trying to remove item " + itemID + " which belongs to a different store.");
            throw new ManagerExternalStoreItemException(e.getMessage());
        }
    }

    @Override
    public List<Item> listItemAvailability(String managerID) throws IncorrectUserRoleException {
        try {
            this.validateUser(managerID, UserRole.M);
        } catch(IncorrectUserRoleException e) {
            this.logger.severe("Permission alert! Customer with ID: " + managerID + "" +
                    " was trying to list available items in the store.");
            throw new IncorrectUserRoleException(e.getMessage());
        }
        return this.store.listItemAvailability(managerID);
    }

    @Override
    public void purchaseItem(String customerID, String itemID, Date dateOfPurchase)throws IncorrectUserRoleException,
            ItemOutOfStockException, NotEnoughFundsException, ExternalStorePurchaseLimitException {
        try {
            this.validateUser(customerID, UserRole.U);
            this.store.purchaseItem(customerID, itemID, dateOfPurchase);
        } catch(ItemOutOfStockException e) {
            this.logger.info("Customer with ID: " + customerID + " attempted to purchase an item with ID:" +
                    " " + itemID + " on " + dateOfPurchase + ", but such an item is out of stock.");
            throw new ItemOutOfStockException(e.getMessage());
        } catch(NotEnoughFundsException e) {
            this.logger.info("Customer with ID: " + customerID + " attempted to purchase an item with" +
                    " ID: " + itemID + " on " + dateOfPurchase + ", but does not have enough funds.");
            throw new NotEnoughFundsException(e.getMessage());
        } catch(ExternalStorePurchaseLimitException e) {
            this.logger.info("Customer with ID: " + customerID + " attempted to purchase an item with" +
                    " ID: " + itemID + " on " + dateOfPurchase + ", but he/she already made purchase from " +
                    "" + e.getMessage() + " store.");
            throw new ExternalStorePurchaseLimitException(e.getMessage());
        } catch (IncorrectUserRoleException e) {
            this.logger.severe("Permission alert! Manager with ID: " + customerID + "" +
                    " was trying to purchase an item with ID: " + itemID + " on " + dateOfPurchase);
            throw new IncorrectUserRoleException(e.getMessage());
        }
    }

    @Override
    public List<String> findItem(String customerID, String itemName) throws IncorrectUserRoleException {
        try {
            this.validateUser(customerID, UserRole.U);
        } catch (IncorrectUserRoleException e) {
            this.logger.severe("Permission alert! Manager with ID: " + customerID + " " +
                    "was trying to find items with " + itemName + " name.");
            throw new IncorrectUserRoleException(e.getMessage());
        }
        return this.store.findItem(customerID, itemName);
    }

    @Override
    public void returnItem(String customerID, String itemID, Date dateOfReturn) throws ReturnPolicyException,
            CustomerNeverPurchasedItemException, ItemWasNeverPurchasedException, IncorrectUserRoleException {
        try {
            this.validateUser(customerID, UserRole.U);
            this.store.returnItem(customerID, itemID, dateOfReturn);
        } catch (ReturnPolicyException e) {
            this.logger.info("Customer with ID: " + customerID + " tried to return an item with ID: " + itemID + "" +
                    " , but it is beyond the return policy.");
            throw new ReturnPolicyException(e.getMessage());
        } catch (CustomerNeverPurchasedItemException e) {
            this.logger.info("Customer with ID: " + customerID + " tried to return an item with ID: " + itemID + "" +
                    " , but the customer never purchased such an item.");
            throw new CustomerNeverPurchasedItemException(e.getMessage());
        } catch (ItemWasNeverPurchasedException e) {
            this.logger.info("Customer with ID: " + customerID + " tried to return an item with ID: " + itemID + "" +
                    " , but such an item was never purchased from the store.");
            throw new ItemWasNeverPurchasedException(e.getMessage());
        } catch (IncorrectUserRoleException e) {
            this.logger.severe("Permission alert! Manager with ID: " + customerID + " was trying to return an item" +
                    " with ID: " + itemID);
            throw new IncorrectUserRoleException(e.getMessage());
        }

    }

    @Override
    public void addCustomerToWaitQueue(String customerID, String itemID) {
        this.store.addCustomerToWaitQueue(customerID, itemID);
    }

    public void initializeStore() throws IOException {
        this.store.listen();
        setupLogger();
    }

    private void validateUser(String userID, UserRole expectedRole) throws IncorrectUserRoleException {
        char currentRole = userID.charAt(2);
        if(currentRole != expectedRole.getRole()) {
            throw new IncorrectUserRoleException("Invalid User ID");
        }
    }

    private void validateItem(String managerID, String itemID) throws ManagerExternalStoreItemException {
        String managerStore = managerID.substring(0, 2);
        String itemStore = itemID.substring(0, 2);
        if(!managerStore.equals(itemStore)) {
            throw new ManagerExternalStoreItemException("Manager is trying to add item from a different store!");
        }
    }

    private void setupLogger() throws IOException {
        String logFile = this.locationName + ".log";
        Handler fileHandler  = new FileHandler("/Users/yaroslav/school/423/Distributed-Systems-Design/" +
                "Supply_Management_System/logs/stores/" + logFile);
        this.logger.setUseParentHandlers(false);
        this.logger.addHandler(fileHandler);
    }
}
