package interface_repository;

import exceptions.*;

import javax.jws.WebMethod;
import javax.jws.WebService;
import javax.jws.soap.SOAPBinding;
import javax.jws.soap.SOAPBinding.*;

@WebService
@SOAPBinding(style=Style.DOCUMENT, use=Use.LITERAL)
public interface ICommunicate {

    @WebMethod
    String addItem(String managerID, String itemID, String itemName, int quantity, int price) throws IncorrectUserRoleException, ManagerExternalStoreItemException, ManagerItemPriceMismatchException;

    @WebMethod
    String removeItem(String managerID, String itemID, int quantity) throws IncorrectUserRoleException, ManagerExternalStoreItemException, ManagerRemoveBeyondQuantityException, ManagerRemoveNonExistingItemException;

    @WebMethod
    String listItemAvailability(String managerID) throws IncorrectUserRoleException;

    @WebMethod
    String purchaseItem(String customerID, String itemID, String dateOfPurchase) throws IncorrectUserRoleException, ItemOutOfStockException, NotEnoughFundsException, ExternalStorePurchaseLimitException;

    @WebMethod
    String findItem(String customerID, String itemName) throws IncorrectUserRoleException;

    @WebMethod
    String returnItem(String customerID, String itemID, String dateOfReturn) throws ReturnPolicyException, CustomerNeverPurchasedItemException, IncorrectUserRoleException;

    @WebMethod
    String addCustomerToWaitQueue(String customerID, String itemID);

    @WebMethod
    String exchangeItem(String customerID, String newItemID, String oldItemID, String dateOfExchange) throws ReturnPolicyException, CustomerNeverPurchasedItemException, ExternalStorePurchaseLimitException, ItemOutOfStockException, NotEnoughFundsException, IncorrectUserRoleException;
}
