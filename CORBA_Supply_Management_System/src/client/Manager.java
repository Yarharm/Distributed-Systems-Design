package client;

import communicate.ICommunicatePackage.*;
import org.omg.CORBA.ORBPackage.InvalidName;
import org.omg.CosNaming.NamingContextPackage.CannotProceed;
import org.omg.CosNaming.NamingContextPackage.NotFound;

import java.io.IOException;
import java.util.logging.FileHandler;
import java.util.logging.Handler;
import java.util.logging.Logger;

public class Manager {
    private final String managerID;
    private final String locationName;
    private final StubFacade stub;
    private final Logger logger;

    public Manager(String managerID, String locationName, String[] args) {
        super();
        this.managerID = managerID;
        this.locationName = locationName;
        this.stub = new StubFacade(locationName, args);
        this.logger = Logger.getLogger(managerID);
    }

    public void addItem(String managerID, String itemID, String itemName, int quantity, int price) throws InvalidName, CannotProceed, org.omg.CosNaming.NamingContextPackage.InvalidName, NotFound {
        try {
            String itemDesc = this.stub.addItem(managerID, itemID, itemName, quantity, price);
            this.logger.info("Manager with ID: " + this.managerID + " added an item to " + this.locationName + " store." +
                    " item information: " + itemDesc);
        } catch(ManagerItemPriceMismatchException e) {
            this.logger.info("Manager with ID: " + managerID + " was trying to add an item with ID: " + itemID + "," +
                    " but the price does not match.");
        } catch(IncorrectUserRoleException e) {
            this.logger.severe("Permission alert! Customer with ID: " + managerID + " is not allowed to add items.");
        } catch(ManagerExternalStoreItemException e) {
            this.logger.severe("Manager with ID: " + managerID + " was trying to add an item " + itemID + " which" +
                    " belongs to a different store.");
        }
    }

    public void removeItem(String managerID, String itemID, int quantity) throws InvalidName, CannotProceed, org.omg.CosNaming.NamingContextPackage.InvalidName, NotFound {
        String msg = quantity == -1 ? "" : quantity + " units from ";
        try {
            String item = this.stub.removeItem(managerID, itemID, quantity);
            this.logger.info(this.managerID + " successfully removed " + msg + "an item with ID: " + itemID + ". " +
                    "item info: " + item);
        } catch(ManagerRemoveNonExistingItemException e) {
            this.logger.severe("Manager with ID: " + managerID + " was trying to remove " + msg + "an item with ID: " + itemID +
                    " but such an item does not exist.");
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
    }

    public void listItemAvailability(String managerID) throws InvalidName, CannotProceed, org.omg.CosNaming.NamingContextPackage.InvalidName, NotFound {
        try {
            String items = this.stub.listItemAvailability(managerID);
            this.logger.info(managerID + " requested a list of available items: " + (items.isEmpty() ? "Store is empty" : items));
        } catch (IncorrectUserRoleException e) {
            this.logger.severe("Permission alert! Customer with ID: " + managerID + "" +
                    " was trying to list all available items in the store.");
        }
    }

    public void setupLogger() throws IOException {
        long creationTime = System.currentTimeMillis();
        String logFile = "/Users/yaroslav/school/423/Distributed-Systems-Design" +
                "/CORBA_Supply_Management_System/logs/clients/managers/" + this.managerID + "_" + creationTime + ".log";
        Handler fileHandler =  new FileHandler(logFile, true);
        this.logger.addHandler(fileHandler);
    }
}
