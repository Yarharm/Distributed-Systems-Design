package client;

import communicate.ICommunicate;
import communicate.Item;
import exceptions.*;

import java.rmi.NotBoundException;
import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import java.util.Date;
import java.util.List;

/*
    Client Stub for communication with remote objects.
 */
public class StubFacade implements ICommunicate {
    private String locationName;

    public StubFacade(String locationName) {
        super();
        this.locationName = locationName;
    }

    @Override
    public Item addItem(String managerID, String itemID, String itemName, int quantity, int price) throws RemoteException, IncorrectUserRoleException, NotBoundException {
        ICommunicate store = this.fetchStore();
        return store.addItem(managerID, itemID, itemName, quantity, price);
    }

    @Override
    public Item removeItem(String managerID, String itemID, int quantity) throws RemoteException, IncorrectUserRoleException, NotBoundException {
        ICommunicate store = this.fetchStore();
        return store.removeItem(managerID, itemID, quantity);
    }

    @Override
    public List<Item> listItemAvailability(String managerID) throws RemoteException, IncorrectUserRoleException, NotBoundException {
        ICommunicate store = this.fetchStore();
        return store.listItemAvailability(managerID);
    }

    @Override
    public boolean purchaseItem(String customerID, String itemID, Date dateOfPurchase) throws RemoteException, NotBoundException,
            IncorrectUserRoleException, ItemOutOfStockException, NotEnoughFundsException, ExternalStorePurchaseLimitException
    {
        ICommunicate store = this.fetchStore();
        return store.purchaseItem(customerID, itemID, dateOfPurchase);
    }

    @Override
    public List<String> findItem(String customerID, String itemName) throws RemoteException, IncorrectUserRoleException, NotBoundException {
        ICommunicate store = this.fetchStore();
        return store.findItem(customerID, itemName);
    }

    @Override
    public void returnItem(String customerID, String itemID, Date dateOfReturn) throws RemoteException, NotBoundException,
            ItemWasNeverPurchasedException, ReturnPolicyException, IncorrectUserRoleException, CustomerNeverPurchasedItemException
    {
        ICommunicate store = this.fetchStore();
        store.returnItem(customerID, itemID, dateOfReturn);
    }

    @Override
    public void addCustomerToWaitQueue(String customerID, String itemID) throws RemoteException, NotBoundException {
        ICommunicate store = this.fetchStore();
        store.addCustomerToWaitQueue(customerID, itemID);
    }

    public ICommunicate fetchStore() throws RemoteException, NotBoundException {
        Registry registry = LocateRegistry.getRegistry("127.0.0.1");
        ICommunicate store = (ICommunicate) registry.lookup(this.locationName);

        return store;
    }
}
