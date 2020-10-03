package communicate;

import exceptions.*;

import java.rmi.NotBoundException;
import java.rmi.Remote;
import java.rmi.RemoteException;
import java.util.Date;
import java.util.List;

/*
    Remote Interface for Client - Server communication.
    Can be invoked by other JVM
    RemoteException is checked: Catch or Throws
 */
public interface ICommunicate extends Remote, ICustomer, IManager {
    // Manager roles
    Item addItem(String managerID, String itemID, String itemName, int quantity, int price) throws RemoteException, IncorrectUserRoleException, NotBoundException;
    Item removeItem(String managerID, String itemID, int quantity) throws RemoteException, IncorrectUserRoleException, NotBoundException;
    List<Item> listItemAvailability(String managerID) throws RemoteException, NotBoundException, IncorrectUserRoleException;

    // Customer roles
    boolean purchaseItem(String customerID, String itemID, Date dateOfPurchase) throws RemoteException, NotBoundException,
            IncorrectUserRoleException, ItemOutOfStockException, NotEnoughFundsException, ExternalStorePurchaseLimitException;
    List<String> findItem(String customerID, String itemName) throws RemoteException, IncorrectUserRoleException, NotBoundException;
    void returnItem(String customerID, String itemID, Date dateOfReturn) throws RemoteException, IncorrectUserRoleException, NotBoundException, ItemWasNeverPurchasedException, CustomerNeverPurchasedItemException, ReturnPolicyException;

    void addCustomerToWaitQueue(String customerID, String itemID) throws RemoteException, NotBoundException;
}
