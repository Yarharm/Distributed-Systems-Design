package client;

import communicate.ICommunicate;
import communicate.Item;
import exceptions.IncorrectUserRoleException;

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
    public List<Item> listItemAvailability(String managerID) throws RemoteException {
        return null;
    }

    @Override
    public boolean purchaseItem(String customerID, String itemID, Date dateOfPurchase) throws RemoteException {
        return true;
    }

    @Override
    public List<Item> findItem(String customerID, String itemName) throws RemoteException {
        return null;
    }

    @Override
    public boolean returnItem(String customerID, String itemID, Date dateOfReturn) throws RemoteException {
        return true;
    }

    public ICommunicate fetchStore() throws RemoteException, NotBoundException {
        Registry registry = LocateRegistry.getRegistry("127.0.0.1");
        ICommunicate store = (ICommunicate) registry.lookup(this.locationName);

        return store;
    }
}
