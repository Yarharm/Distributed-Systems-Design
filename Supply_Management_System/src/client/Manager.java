package client;

import communicate.IManager;

import java.rmi.RemoteException;
import java.util.List;

public class Manager implements IManager {
    private String managerID;
    private String location;
    private StubFacade stub;

    public Manager(String managerID, String location) {
        super();
        this.managerID = managerID;
        this.location = location;
        this.stub = new StubFacade(location);
    }

    @Override
    public boolean addItem(String managerID, String itemID, String itemName, int quantity, int price) throws RemoteException {
        return this.stub.addItem(managerID, itemID, itemName, quantity, price);
    }

    @Override
    public void removeItem(String managerID, String itemID, int quantity, boolean removeCompletely) {
        return;
    }

    @Override
    public List<String> listItemAvailability(String managerID) throws RemoteException {
        return this.stub.listItemAvailability(managerID);
    }
}
