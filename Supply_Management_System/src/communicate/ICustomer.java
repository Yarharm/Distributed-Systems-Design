package communicate;

import java.rmi.RemoteException;
import java.util.Date;
import java.util.List;

public interface ICustomer {
    boolean purchaseItem(String customerID, String itemID, Date dateOfPurchase) throws RemoteException;
    List<Item> findItem(String customerID, String itemName) throws RemoteException;
    boolean returnItem(String customerID, String itemID, Date dateOfReturn) throws RemoteException;
}
