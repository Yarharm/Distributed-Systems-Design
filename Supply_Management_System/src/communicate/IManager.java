package communicate;

import java.rmi.RemoteException;
import java.util.List;

public interface IManager {
    boolean addItem(String managerID, String itemID, String itemName, int quantity, int price) throws RemoteException;
    void removeItem(String managerID, String itemID, int quantity, boolean removeCompletely) throws RemoteException;
    List<String> listItemAvailability(String managerID) throws RemoteException;
}
