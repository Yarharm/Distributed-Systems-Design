package communicate;

import exceptions.IncorrectUserRoleException;

import java.rmi.NotBoundException;
import java.rmi.RemoteException;
import java.util.List;

public interface IManager {
    Item addItem(String managerID, String itemID, String itemName, int quantity, int price) throws RemoteException, IncorrectUserRoleException, NotBoundException;
    Item removeItem(String managerID, String itemID, int quantity) throws RemoteException, IncorrectUserRoleException, NotBoundException;
    List<Item> listItemAvailability(String managerID) throws RemoteException;
}
