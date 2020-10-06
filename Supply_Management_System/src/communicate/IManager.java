package communicate;

import exceptions.*;

import java.rmi.NotBoundException;
import java.rmi.RemoteException;
import java.util.List;

public idl_interface IManager {
    Item addItem(String managerID, String itemID, String itemName, int quantity, int price) throws RemoteException, IncorrectUserRoleException, NotBoundException, ManagerExternalStoreItemException, ManagerItemPriceMismatchException;
    Item removeItem(String managerID, String itemID, int quantity) throws RemoteException, IncorrectUserRoleException, NotBoundException, ManagerExternalStoreItemException, ManagerRemoveBeyondQuantityException, ManagerRemoveNonExistingItemException;
    List<Item> listItemAvailability(String managerID) throws RemoteException, NotBoundException, IncorrectUserRoleException;
}
