package client;

import communicate.ICustomer;
import communicate.Item;

import java.io.IOException;
import java.util.Date;
import java.util.List;
import java.util.logging.FileHandler;
import java.util.logging.Handler;
import java.util.logging.Logger;

public class Customer implements ICustomer {
    private String customerID;
    private String locationName;
    private int budget;
    private StubFacade stub;
    private Logger logger;

    public Customer(String customerID, String locationName) {
        super();
        this.customerID = customerID;
        this.locationName = locationName;
        this.budget = 1000;
        this.stub = new StubFacade(locationName);
        this.logger = Logger.getLogger(customerID);
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

    public void setupLogger() throws IOException {
        String logFile = this.customerID + ".log";
        Handler fileHandler  = new FileHandler("/Users/yaroslav/school/423/Distributed-Systems-Design" +
                "/Supply_Management_System/logs/clients/customers/" + logFile);
        this.logger.setUseParentHandlers(false);
        this.logger.addHandler(fileHandler);
    }
}
