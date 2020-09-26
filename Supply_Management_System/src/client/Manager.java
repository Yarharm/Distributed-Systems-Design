package client;

import communicate.IManager;
import communicate.Item;
import exceptions.IncorrectUserRoleException;

import java.io.IOException;
import java.rmi.NotBoundException;
import java.rmi.RemoteException;
import java.util.List;
import java.util.logging.FileHandler;
import java.util.logging.Handler;
import java.util.logging.Logger;

public class Manager implements IManager {
    private String managerID;
    private String locationName;
    private StubFacade stub;
    private Logger logger;

    public Manager(String managerID, String locationName) {
        super();
        this.managerID = managerID;
        this.locationName = locationName;
        this.stub = new StubFacade(locationName);
        this.logger = Logger.getLogger(managerID);
    }

    @Override
    public Item addItem(String managerID, String itemID, String itemName, int quantity, int price) throws RemoteException, NotBoundException {
        Item item = null;
        try {
            item = this.stub.addItem(managerID, itemID, itemName, quantity, price);
            this.logger.info(this.managerID + " added an Item to " + this.locationName + " store." +
                    " Item information: " + item.toString());
        } catch(IncorrectUserRoleException e) {
            item = new Item(itemID, itemName, quantity, price);
            this.logger.severe("Permission alert! Customer with ID: " + managerID + " is not allowed to add items." +
                    " Customer was trying to add the following item: " + item.toString());
        }
        return item;
    }

    @Override
    public Item removeItem(String managerID, String itemID, int quantity) throws RemoteException, NotBoundException {
        Item item = null;
        try {
            item = this.stub.removeItem(managerID, itemID, quantity);
            String msg = quantity == -1 || item == null ? "" : quantity + " units from ";
            this.logger.info(this.managerID + " successfully removed " + msg + "an item with ID: " + itemID);
        } catch(IncorrectUserRoleException e) {
            String msg = quantity == -1 ? "" : quantity + " units from ";
            this.logger.severe("Permission alert! Customer with ID: " + managerID + " " +
                    "was trying to remove " + msg + "an item with ID: " + itemID);
        }
        return item;
    }

    @Override
    public List<Item> listItemAvailability(String managerID) throws RemoteException {
        return this.stub.listItemAvailability(managerID);
    }

    public void setupLogger() throws IOException {
        String logFile = this.managerID + ".log";
        Handler fileHandler  = new FileHandler("/Users/yaroslav/school/423/Distributed-Systems-Design" +
                    "/Supply_Management_System/logs/clients/managers/" + logFile);
        // this.logger.setUseParentHandlers(false);
        this.logger.addHandler(fileHandler);
    }
}
