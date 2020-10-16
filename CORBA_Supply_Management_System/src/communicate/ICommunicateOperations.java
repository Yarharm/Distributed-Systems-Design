package communicate;


/**
* communicate/ICommunicateOperations.java .
* Generated by the IDL-to-Java compiler (portable), version "3.2"
* from ICommunicate.idl
* Friday, October 16, 2020 7:02:15 PM EDT
*/

public interface ICommunicateOperations 
{
  String addItem (String managerID, String itemID, String itemName, int quantity, int price) throws communicate.ICommunicatePackage.IncorrectUserRoleException, communicate.ICommunicatePackage.ManagerExternalStoreItemException, communicate.ICommunicatePackage.ManagerItemPriceMismatchException;
  String removeItem (String managerID, String itemID, int quantity) throws communicate.ICommunicatePackage.IncorrectUserRoleException, communicate.ICommunicatePackage.ManagerExternalStoreItemException, communicate.ICommunicatePackage.ManagerRemoveNonExistingItemException, communicate.ICommunicatePackage.ManagerRemoveBeyondQuantityException;
  String listItemAvailability (String managerID) throws communicate.ICommunicatePackage.IncorrectUserRoleException;
  void purchaseItem (String customerID, String itemID, String dateOfPurchase) throws communicate.ICommunicatePackage.IncorrectUserRoleException, communicate.ICommunicatePackage.ItemOutOfStockException, communicate.ICommunicatePackage.NotEnoughFundsException, communicate.ICommunicatePackage.ExternalStorePurchaseLimitException;
  String findItem (String customerID, String itemName) throws communicate.ICommunicatePackage.IncorrectUserRoleException;
  void returnItem (String customerID, String itemID, String dateOfReturn) throws communicate.ICommunicatePackage.IncorrectUserRoleException, communicate.ICommunicatePackage.ItemWasNeverPurchasedException, communicate.ICommunicatePackage.CustomerNeverPurchasedItemException, communicate.ICommunicatePackage.ReturnPolicyException;
  void addCustomerToWaitQueue (String customerID, String itemID);
  void exchangeItem (String customerID, String newItemID, String oldItemID, String dateOfExchange) throws communicate.ICommunicatePackage.IncorrectUserRoleException, communicate.ICommunicatePackage.ReturnPolicyException, communicate.ICommunicatePackage.ItemWasNeverPurchasedException, communicate.ICommunicatePackage.CustomerNeverPurchasedItemException, communicate.ICommunicatePackage.ExternalStorePurchaseLimitException, communicate.ICommunicatePackage.ItemOutOfStockException, communicate.ICommunicatePackage.NotEnoughFundsException;
} // interface ICommunicateOperations
