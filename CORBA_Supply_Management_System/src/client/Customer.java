package client;

import communicate.ICommunicatePackage.*;
import org.omg.CORBA.ORBPackage.InvalidName;
import org.omg.CosNaming.NamingContextPackage.CannotProceed;
import org.omg.CosNaming.NamingContextPackage.NotFound;

import java.io.IOException;
import java.util.Scanner;
import java.util.logging.FileHandler;
import java.util.logging.Handler;
import java.util.logging.Logger;

public class Customer {
    private final String customerID;
    private final StubFacade stub;
    private final Logger logger;

    public Customer(String customerID, String locationName, String[] args) {
        super();
        this.customerID = customerID;
        this.stub = new StubFacade(locationName, args);
        this.logger = Logger.getLogger(customerID);
    }

    public void purchaseItem(String customerID, String itemID, String dateOfPurchase) throws InvalidName, CannotProceed, org.omg.CosNaming.NamingContextPackage.InvalidName, NotFound {
        try {
            this.stub.purchaseItem(customerID, itemID, dateOfPurchase);
            this.logger.info("Customer with ID: " + customerID + " successfully purchased an item with ID: " + itemID + "" +
                    " on " + dateOfPurchase + ".");
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
                    "" + itemID.substring(0, 2) + " store.");
        }
        catch(IncorrectUserRoleException e) {
            this.logger.severe("Permission alert! Manager with ID: " + customerID + "" +
                    " was trying to purchase an item with ID: " + itemID + " on " + dateOfPurchase);
        }
    }

    public void findItem(String customerID, String itemName) throws InvalidName, CannotProceed, org.omg.CosNaming.NamingContextPackage.InvalidName, NotFound {
        try {
            String items = this.stub.findItem(customerID, itemName);
            String msg = items.isEmpty() ? " no available items " :  " the following list of items: " + items;
            this.logger.info("Customer with ID: " + customerID + " received " + msg + "" +
                    " based on the item with name " + itemName);
        } catch (IncorrectUserRoleException e) {
            this.logger.severe("Permission alert! Manager with ID: " + customerID + " tried to find items based on " +
                    "the " + itemName + " name.");
        }
    }

    public void returnItem(String customerID, String itemID, String dateOfReturn) throws InvalidName, CannotProceed, org.omg.CosNaming.NamingContextPackage.InvalidName, NotFound {
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
        long creationTime = System.currentTimeMillis();
        String logFile = "/Users/yaroslav/school/423/Distributed-Systems-Design" +
                "/CORBA_Supply_Management_System/logs/clients/customers/" + this.customerID + "_" + creationTime + ".log";
        Handler fileHandler = new FileHandler(logFile, true);
        this.logger.addHandler(fileHandler);
    }

    private boolean addToQueuePrompt() {
        Scanner myObj = new Scanner(System.in);
        System.out.println("Item out of stock. Would you like to be added to the queue for this item?(Y/N)");

        String userInput = myObj.nextLine().toLowerCase();
        return userInput.equals("y");
    }
}
