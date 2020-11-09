package client;

import java.io.IOException;
import java.net.MalformedURLException;
import java.util.Scanner;
import java.util.logging.FileHandler;
import java.util.logging.Handler;
import java.util.logging.Logger;
import exceptions.*;

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

    public void purchaseItem(String customerID, String itemID, String dateOfPurchase) throws MalformedURLException {
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

    public void findItem(String customerID, String itemName) {
        try {
            String items = this.stub.findItem(customerID, itemName);
            String msg = items.isEmpty() ? " no available items " :  " the following list of items: " + items;
            this.logger.info("Customer with ID: " + customerID + " received " + msg + "" +
                    " based on the item with name " + itemName);
        } catch (IncorrectUserRoleException | MalformedURLException e) {
            this.logger.severe("Permission alert! Manager with ID: " + customerID + " tried to find items based on " +
                    "the " + itemName + " name.");
        }
    }

    public void returnItem(String customerID, String itemID, String dateOfReturn) throws MalformedURLException {
        try {
            this.stub.returnItem(customerID, itemID, dateOfReturn);
            this.logger.info("Customer with ID: " + customerID + " successfully returned an item with ID: " + itemID + "" +
                    " on " + dateOfReturn + ".");
        } catch (ReturnPolicyException e) {
            this.logger.info("Customer with ID: " + customerID + " was trying to return an item with ID: " + itemID + "" +
                    ", but it is a Return Policy violation.");
        } catch (CustomerNeverPurchasedItemException e) {
            this.logger.info("Customer with ID: " + customerID + " was trying to return an item with ID: " + itemID + "" +
                    ", but the customer never purchased such an item.");
        } catch (IncorrectUserRoleException e) {
            this.logger.severe("Permission alert! Manager with ID: " + customerID + " was trying return an item" +
                    " with ID: " + itemID);
        }
    }

    public void exchangeItem(String customerID, String newItemID, String oldItemID, String dateOfExchange) throws MalformedURLException {
        try {
            this.stub.exchangeItem(customerID, newItemID, oldItemID, dateOfExchange);
            this.logger.info("Customer with ID: " + customerID + " has successfully exchanged an item with ID: " + oldItemID + "" +
                    " for an item with ID: " + newItemID + " on " + dateOfExchange);
        }  catch (ReturnPolicyException e) {
            this.logger.info("Customer with ID: " + customerID + " tried to exchange an item with ID: " + oldItemID + "" +
                    ", for a new item with ID: " + newItemID + ", but it is beyond the return policy.");
        } catch (CustomerNeverPurchasedItemException e) {
            this.logger.info("Customer with ID: " + customerID + " tried to return an item with ID: " + oldItemID + "" +
                    " during exchange operation, but the customer never purchased such an item.");
        } catch(ExternalStorePurchaseLimitException e) {
            this.logger.info("Customer with ID: " + customerID + " attempted to exchange an item with" +
                    " ID: " + oldItemID + " on " + dateOfExchange + " for an item with ID: " + newItemID + ", but he/she already made purchase from " +
                    "" + newItemID.substring(0, 2) + " store.");
        } catch(ItemOutOfStockException e) {
            this.logger.info("Customer with ID: " + customerID + " attempted to exchange an item with ID:" +
                    " " + oldItemID + " on " + dateOfExchange + " for an item with ID: " + newItemID + ", but such an item is out of stock.");
        } catch(NotEnoughFundsException e) {
            this.logger.info("Customer with ID: " + customerID + " attempted to exchange an item with" +
                    " ID: " + oldItemID + " on " + dateOfExchange + " for an item with ID: " + newItemID + ", but does not have enough funds.");
        } catch (IncorrectUserRoleException e) {
            this.logger.severe("Permission alert! Manager with ID: " + customerID + " was trying to exchange an item" +
                    " with ID: " + oldItemID + " to a new item with ID: " + newItemID + " on " + dateOfExchange);
        }

    }

    public void setupLogger() throws IOException {
        long creationTime = System.currentTimeMillis();
        String logFile = "/Users/yaroslav/school/423/Distributed-Systems-Design" +
                "/SOAP_Supply_Management_System/logs/clients/customers/" + this.customerID + "_" + creationTime + ".log";
        Handler fileHandler = new FileHandler(logFile, true);
        this.logger.addHandler(fileHandler);
    }

    private boolean addToQueuePrompt() {
        Scanner myObj = new Scanner(System.in);
        System.out.println("item out of stock. Would you like to be added to the queue for this item?(Y/N)");

        String userInput = myObj.nextLine().toLowerCase();
        return userInput.equals("y");
    }
}
