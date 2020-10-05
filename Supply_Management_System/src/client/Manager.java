package client;

import communicate.IManager;
import communicate.Item;
import exceptions.*;

import java.io.IOException;
import java.rmi.NotBoundException;
import java.rmi.RemoteException;
import java.util.List;
import java.util.logging.FileHandler;
import java.util.logging.Handler;
import java.util.logging.Logger;
import java.util.stream.Collectors;

public class Manager implements IManager {
    private final String managerID;
    private final String locationName;
    private final StubFacade stub;
    private final Logger logger;

    public Manager(String managerID, String locationName) {
        super();
        this.managerID = managerID;
        this.locationName = locationName;
        this.stub = new StubFacade(locationName);
        this.logger = Logger.getLogger(managerID);
    }

    @Override
    public Item addItem(String managerID, String itemID, String itemName, int quantity, int price) throws RemoteException, NotBoundException {
        Item item = new Item(itemID, itemName, quantity, price);
        try {
            item = this.stub.addItem(managerID, itemID, itemName, quantity, price);
            this.logger.info("Manager with ID: " + this.managerID + " added an Item to " + this.locationName + " store." +
                    " Item information: " + item.toString());
        } catch(ManagerItemPriceMismatchException e) {
            this.logger.info("Manager with ID: " + managerID + " was trying to add an item with ID: " + itemID + "," +
                    " but the price does not match.");
        } catch(IncorrectUserRoleException e) {
            this.logger.severe("Permission alert! Customer with ID: " + managerID + " is not allowed to add items." +
                    " Customer was trying to add the following item: " + item.toString());
        } catch(ManagerExternalStoreItemException e) {
            this.logger.severe("Manager with ID: " + managerID + " was trying to add an item " + itemID + " which" +
                    " belongs to a different store.");
        }
        return item;
    }

    @Override
    public Item removeItem(String managerID, String itemID, int quantity) throws RemoteException, NotBoundException {
        Item item = null;
        String msg = quantity == -1 ? "" : quantity + " units from ";
        try {
            item = this.stub.removeItem(managerID, itemID, quantity);
            this.logger.info(this.managerID + " successfully removed " + msg + "an item with ID: " + itemID + ". " +
                    "Item info: " + item);
        } catch(ManagerRemoveNonExistingItemException e) {
            this.logger.severe("Manager with ID: " + managerID + " was trying to remove " + msg + "an item with ID: " + itemID +
                    "but such an item does not exist.");
        } catch(ManagerRemoveBeyondQuantityException e) {
            this.logger.severe("Manager with ID: " + managerID + " was trying to remove more quantity than available in a store " +
                    "for the item with ID: " + itemID);
        } catch (IncorrectUserRoleException e) {
            this.logger.severe("Permission alert! Customer with ID: " + managerID + " " +
                    "was trying to remove " + msg + "an item with ID: " + itemID);
        } catch(ManagerExternalStoreItemException e) {
            this.logger.severe("Manager with ID: " + managerID + " was trying to remove item " + itemID + " which" +
                    " belongs to a different store.");
        }
        return item;
    }

    @Override
    public List<Item> listItemAvailability(String managerID) throws RemoteException, NotBoundException {
        List<Item> items = null;
        try {
            items = this.stub.listItemAvailability(managerID);
            String itemStr = items.stream().map(item -> item.getItemID() + " " + item.getItemName() + " " + item.getQuantity()  + " " + item.getPrice())
                    .collect(Collectors.joining(", "));
            this.logger.info(managerID + " requested a list of available items: " + (itemStr.isEmpty() ? "Store is empty" : itemStr));
        } catch (IncorrectUserRoleException e) {
            this.logger.severe("Permission alert! Customer with ID: " + managerID + "" +
                    " was trying to list all available items in the store.");
        }
        return items;
    }

    public void setupLogger() throws IOException {
        String logFile = this.managerID + ".log";
        Handler fileHandler  = new FileHandler("/Users/yaroslav/school/423/Distributed-Systems-Design" +
                    "/Supply_Management_System/logs/clients/managers/" + logFile);
        // this.logger.setUseParentHandlers(false);
        this.logger.addHandler(fileHandler);
    }
}
