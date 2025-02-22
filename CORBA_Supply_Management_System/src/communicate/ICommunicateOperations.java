package communicate;


/**
* communicate/ICommunicateOperations.java .
* Generated by the IDL-to-Java compiler (portable), version "3.2"
* from ICommunicate.idl
* Sunday, November 8, 2020 4:59:29 PM EST
*/

public interface ICommunicateOperations 
{
  String addItem (String managerID, String itemID, String itemName, int quantity, int price) throws communicate.ICommunicatePackage.IncorrectUserRoleException, communicate.ICommunicatePackage.ManagerExternalStoreItemException, communicate.ICommunicatePackage.ManagerItemPriceMismatchException;
  String removeItem (String managerID, String itemID, int quantity) throws communicate.ICommunicatePackage.IncorrectUserRoleException, communicate.ICommunicatePackage.ManagerExternalStoreItemException, communicate.ICommunicatePackage.ManagerRemoveNonExistingItemException, communicate.ICommunicatePackage.ManagerRemoveBeyondQuantityException;
  String listItemAvailability (String managerID) throws communicate.ICommunicatePackage.IncorrectUserRoleException;
  String purchaseItem (String customerID, String itemID, String dateOfPurchase) throws communicate.ICommunicatePackage.IncorrectUserRoleException, communicate.ICommunicatePackage.ItemOutOfStockException, communicate.ICommunicatePackage.NotEnoughFundsException, communicate.ICommunicatePackage.ExternalStorePurchaseLimitException;
  String findItem (String customerID, String itemName) throws communicate.ICommunicatePackage.IncorrectUserRoleException;
  String returnItem (String customerID, String itemID, String dateOfReturn) throws communicate.ICommunicatePackage.IncorrectUserRoleException, communicate.ICommunicatePackage.CustomerNeverPurchasedItemException, communicate.ICommunicatePackage.ReturnPolicyException;
  String addCustomerToWaitQueue (String customerID, String itemID);
  String exchangeItem (String customerID, String newItemID, String oldItemID, String dateOfExchange) throws communicate.ICommunicatePackage.IncorrectUserRoleException, communicate.ICommunicatePackage.ReturnPolicyException, communicate.ICommunicatePackage.CustomerNeverPurchasedItemException, communicate.ICommunicatePackage.ExternalStorePurchaseLimitException, communicate.ICommunicatePackage.ItemOutOfStockException, communicate.ICommunicatePackage.NotEnoughFundsException;
} // interface ICommunicateOperations
