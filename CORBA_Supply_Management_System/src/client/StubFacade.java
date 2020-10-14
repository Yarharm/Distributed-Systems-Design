package client;

import communicate.*;
import communicate.ICommunicatePackage.*;
import org.omg.CORBA.ORBPackage.InvalidName;
import org.omg.CosNaming.*;
import org.omg.CosNaming.NamingContextPackage.*;
import org.omg.CORBA.*;

/*
    Client Stub for communication with remote objects.
 */
public class StubFacade {
    private final String locationName;
    private final ORB orb;
    public StubFacade(String locationName, String[] args) {
        super();
        this.locationName = locationName;
        this.orb = ORB.init(args, null);
    }

    public String addItem(String managerID, String itemID, String itemName, int quantity, int price) throws InvalidName, CannotProceed, org.omg.CosNaming.NamingContextPackage.InvalidName, NotFound,
            IncorrectUserRoleException, ManagerExternalStoreItemException, ManagerItemPriceMismatchException
    {
        ICommunicate store = this.fetchStore();
        return store.addItem(managerID, itemID, itemName, quantity, price);
    }

    public String removeItem(String managerID, String itemID, int quantity) throws InvalidName, CannotProceed, org.omg.CosNaming.NamingContextPackage.InvalidName,
            NotFound, IncorrectUserRoleException, ManagerExternalStoreItemException, ManagerRemoveBeyondQuantityException, ManagerRemoveNonExistingItemException
    {
        ICommunicate store = this.fetchStore();
        return store.removeItem(managerID, itemID, quantity);
    }

    public String listItemAvailability(String managerID) throws InvalidName, CannotProceed, org.omg.CosNaming.NamingContextPackage.InvalidName, NotFound, IncorrectUserRoleException {
        ICommunicate store = this.fetchStore();
        return store.listItemAvailability(managerID);
    }

    public void purchaseItem(String customerID, String itemID, String dateOfPurchase) throws InvalidName, CannotProceed, org.omg.CosNaming.NamingContextPackage.InvalidName, NotFound,
            IncorrectUserRoleException, ItemOutOfStockException, NotEnoughFundsException, ExternalStorePurchaseLimitException
    {
        ICommunicate store = this.fetchStore();
        store.purchaseItem(customerID, itemID, dateOfPurchase);
    }

    public String findItem(String customerID, String itemName) throws InvalidName, CannotProceed, org.omg.CosNaming.NamingContextPackage.InvalidName, NotFound, IncorrectUserRoleException {
        ICommunicate store = this.fetchStore();
        return store.findItem(customerID, itemName);
    }

    public void returnItem(String customerID, String itemID, String dateOfReturn) throws InvalidName, CannotProceed, org.omg.CosNaming.NamingContextPackage.InvalidName, NotFound,
            ItemWasNeverPurchasedException, ReturnPolicyException, IncorrectUserRoleException, CustomerNeverPurchasedItemException
    {
        ICommunicate store = this.fetchStore();
        store.returnItem(customerID, itemID, dateOfReturn);
    }

    public void addCustomerToWaitQueue(String customerID, String itemID) throws InvalidName, CannotProceed, org.omg.CosNaming.NamingContextPackage.InvalidName, NotFound {
        ICommunicate store = this.fetchStore();
        store.addCustomerToWaitQueue(customerID, itemID);
    }

    public ICommunicate fetchStore() throws InvalidName, CannotProceed, org.omg.CosNaming.NamingContextPackage.InvalidName, NotFound {
        // get the root naming context
        org.omg.CORBA.Object objRef =
                orb.resolve_initial_references("NameService");
        // Use NamingContextExt instead of NamingContext. This is
        // part of the Interoperable naming Service.
        NamingContextExt ncRef = NamingContextExtHelper.narrow(objRef);

        // resolve the Object Reference in Naming
        ICommunicate store = ICommunicateHelper.narrow(ncRef.resolve_str(this.locationName));
        return store;
    }
}
