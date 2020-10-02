package communicate;

import exceptions.ExternalStorePurchaseLimitException;
import exceptions.IncorrectUserRoleException;
import exceptions.ItemOutOfStockException;
import exceptions.NotEnoughFundsException;

import java.rmi.NotBoundException;
import java.rmi.RemoteException;
import java.util.Date;
import java.util.List;

public interface ICustomer {
    boolean purchaseItem(String customerID, String itemID, Date dateOfPurchase) throws RemoteException, NotBoundException, IncorrectUserRoleException, ItemOutOfStockException, NotEnoughFundsException, ExternalStorePurchaseLimitException;
    List<String> findItem(String customerID, String itemName) throws RemoteException, IncorrectUserRoleException, NotBoundException;
    boolean returnItem(String customerID, String itemID, Date dateOfReturn) throws RemoteException, IncorrectUserRoleException, NotBoundException;
}
