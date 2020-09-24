package client;

import communicate.ICustomer;

import java.util.Date;
import java.util.List;

public class Customer implements ICustomer {
    private String customerID;
    private String location;
    private int budget;
    private StubFacade stub;

    public Customer(String customerID, String location) {
        super();
        this.customerID = customerID;
        this.location = location;
        this.budget = 1000;
        this.stub = new StubFacade(location);
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
}
