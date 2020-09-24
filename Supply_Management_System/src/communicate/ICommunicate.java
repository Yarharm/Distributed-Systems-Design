package communicate;

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
    boolean addItem(String managerID, String itemID, String itemName, int quantity, int price) throws RemoteException;
    void removeItem(String managerID, String itemID, int quantity, boolean removeCompletely) throws RemoteException;
    List<String> listItemAvailability(String managerID) throws RemoteException;

    // Customer roles
    boolean purchaseItem(String customerID, String itemID, Date dateOfPurchase) throws RemoteException;
    List<String> findItem(String customerID, String itemName) throws RemoteException;
    boolean returnItem(String customerID, String itemID, Date dateOfReturn) throws RemoteException;
}
