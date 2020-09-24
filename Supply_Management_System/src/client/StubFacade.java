package client;

import communicate.ICommunicate;

import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.List;

/*
    Client Stub for communication with remote objects.
    Implements Facade interface.
 */
public class StubFacade implements ICommunicate {
    private String location;

    public StubFacade(String location) {
        super();
        this.location = location;
    }

    @Override
    public boolean addItem(String managerID, String itemID, String itemName, int quantity, int price) throws RemoteException {
        ICommunicate store = this.fetchStore();
        if(store == null) {
            return false;
        }
        return store.addItem(managerID, itemID, itemName, quantity, price);
    }

    @Override
    public void removeItem(String managerID, String itemID, int quantity, boolean removeCompletely) throws RemoteException {
        return;
    }

    @Override
    public List<String> listItemAvailability(String managerID) throws RemoteException {
        ICommunicate store = this.fetchStore();
        if(store == null) {
            return new ArrayList<>(Arrays.asList("COULD Not fetch registry"));
        }
        return store.listItemAvailability(managerID);
    }

    @Override
    public boolean purchaseItem(String customerID, String itemID, Date dateOfPurchase) throws RemoteException {
        return true;
    }

    @Override
    public List<String> findItem(String customerID, String itemName) throws RemoteException {
        return null;
    }

    @Override
    public boolean returnItem(String customerID, String itemID, Date dateOfReturn) throws RemoteException {
        return true;
    }

    public ICommunicate fetchStore() {
        ICommunicate store = null;
        try {
            Registry registry = LocateRegistry.getRegistry("127.0.0.1");
            store = (ICommunicate) registry.lookup(this.location);
        }  catch (Exception e) {
            System.err.println("Could not fetch registry:");
            e.printStackTrace();
        }

        return store;
    }
}
