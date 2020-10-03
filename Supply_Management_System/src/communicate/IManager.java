package communicate;

import exceptions.IncorrectUserRoleException;
import exceptions.ManagerExternalStoreItemException;

import java.rmi.NotBoundException;
import java.rmi.RemoteException;
import java.util.List;

public interface IManager {
    Item addItem(String managerID, String itemID, String itemName, int quantity, int price) throws RemoteException, IncorrectUserRoleException, NotBoundException, ManagerExternalStoreItemException;
    Item removeItem(String managerID, String itemID, int quantity) throws RemoteException, IncorrectUserRoleException, NotBoundException, ManagerExternalStoreItemException;
    List<Item> listItemAvailability(String managerID) throws RemoteException, NotBoundException, IncorrectUserRoleException;
}
