package client;

import communicate.ICustomer;
import exceptions.*;

import java.io.IOException;
import java.rmi.NotBoundException;
import java.rmi.RemoteException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Scanner;
import java.util.logging.FileHandler;
import java.util.logging.Handler;
import java.util.logging.Logger;

public class Customer implements ICustomer {
    private final String customerID;
    private final String locationName;
    private final StubFacade stub;
    private final Logger logger;

    public Customer(String customerID, String locationName) {
        super();
        this.customerID = customerID;
        this.locationName = locationName;
        this.stub = new StubFacade(locationName);
        this.logger = Logger.getLogger(customerID);
    }

    @Override
    public boolean purchaseItem(String customerID, String itemID, Date dateOfPurchase) throws RemoteException, NotBoundException {
        boolean purchaseStatus = false;
        try {
            purchaseStatus = this.stub.purchaseItem(customerID, itemID, dateOfPurchase);
            if(purchaseStatus) {
                this.logger.info("Customer with ID: " + customerID + " successfully purchased an item with ID: " + itemID + "" +
                        " on " + dateOfPurchase + ".");
            }
        } catch(ItemOutOfStockException e) {
            boolean addToQueue = this.addToQueuePrompt();
            String message =  "Did not add customer to the Queue.";
            if(addToQueue) {
                this.stub.addCustomerToWaitQueue(customerID, itemID);
                message = "Added Customer to the Queue.";
            }
            this.logger.info("Customer with ID: " + customerID + " attempted to purchase item with ID: " + itemID + "" +
                    " on " + dateOfPurchase + " but it is out of stock. " + message);

        } catch(NotEnoughFundsException e) {
            this.logger.info("Customer with ID: " + customerID + " attempted to purchase item with ID: " + itemID + "" +
                    " on " + dateOfPurchase + " but customer does not have enough funds.");
        } catch(ExternalStorePurchaseLimitException e) {
            this.logger.info("Customer with ID: " + customerID + " attempted to purchase an item with" +
                    " ID: " + itemID + " on " + dateOfPurchase + ", but he/she already made purchase from " +
                    "" + e.getMessage() + " store.");
        }
        catch(IncorrectUserRoleException e) {
            this.logger.severe("Permission alert! Manager with ID: " + customerID + "" +
                    " was trying to purchase an item with ID: " + itemID + " on " + dateOfPurchase);
        }
        return purchaseStatus;
    }

    @Override
    public List<String> findItem(String customerID, String itemName) throws RemoteException, NotBoundException {
        List<String> items = new ArrayList<>();
        try {
            items = this.stub.findItem(customerID, itemName);
            StringBuilder sb = new StringBuilder();
            items.forEach(serialItem -> sb.append(serialItem).append(" "));
            String msg = items.size() == 0 ? " no available items " :  " the following list of items: " + sb.toString();
            this.logger.info("Customer with ID: " + customerID + " received " + msg + "" +
                    "based on the item with " + itemName + " name.");
        } catch (IncorrectUserRoleException e) {
            this.logger.severe("Permission alert! Manager with ID: " + customerID + " tried to find items based on " +
                    "the " + itemName + " name.");
        }
        return items;
    }

    @Override
    public void returnItem(String customerID, String itemID, Date dateOfReturn) throws RemoteException, NotBoundException {
        try {
            this.stub.returnItem(customerID, itemID, dateOfReturn);
            this.logger.info("Customer with ID: " + customerID + " successfully returned an item with ID: " + itemID + "" +
                    " on " + dateOfReturn + ".");
        } catch (ReturnPolicyException e) {
            this.logger.info("Customer with ID: " + customerID + " was trying to return an item with ID: " + itemID + "" +
                    ", but it is beyond a Return Policy.");
        } catch (ItemWasNeverPurchasedException e) {
            this.logger.info("Customer with ID: " + customerID + " was trying to return an item with ID: " + itemID + "" +
                    ", but such an item was never purchased from the store.");
        } catch (CustomerNeverPurchasedItemException e) {
            this.logger.info("Customer with ID: " + customerID + " was trying to return an item with ID: " + itemID + "" +
                    ", but the customer never purchased such an item.");
        } catch (IncorrectUserRoleException e) {
            this.logger.severe("Permission alert! Manager with ID: " + customerID + " was trying return an item" +
                    " with ID: " + itemID);
        }

    }

    public void setupLogger() throws IOException {
        String logFile = this.customerID + ".log";
        Handler fileHandler  = new FileHandler("/Users/yaroslav/school/423/Distributed-Systems-Design" +
                "/Supply_Management_System/logs/clients/customers/" + logFile);
        // this.logger.setUseParentHandlers(false);
        this.logger.addHandler(fileHandler);
    }

    private boolean addToQueuePrompt() {
        Scanner myObj = new Scanner(System.in);
        System.out.println("Item out of stock. Would you like to be added to the queue for this item?(Y/N)");

        String userInput = myObj.nextLine().toLowerCase();
        return userInput.equals("y");
    }
}
