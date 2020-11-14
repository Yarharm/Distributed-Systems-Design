package client;

import interface_repository.ICommunicate;
import exceptions.*;

import javax.xml.namespace.QName;
import javax.xml.ws.Service;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.HashMap;
import java.util.Map;

/*
    Client Stub for communication with remote objects.
 */
public class StubFacade {
    private final String locationName;
    public StubFacade(String locationName, String[] args) {
        super();
        this.locationName = locationName;
    }

    public String addItem(String managerID, String itemID, String itemName, int quantity, int price) throws IncorrectUserRoleException, ManagerExternalStoreItemException, ManagerItemPriceMismatchException, MalformedURLException {
        ICommunicate store = this.fetchStore();
        return store.addItem(managerID, itemID, itemName, quantity, price);
    }

    public String removeItem(String managerID, String itemID, int quantity) throws IncorrectUserRoleException, ManagerExternalStoreItemException, ManagerRemoveBeyondQuantityException, ManagerRemoveNonExistingItemException, MalformedURLException {
        ICommunicate store = this.fetchStore();
        return store.removeItem(managerID, itemID, quantity);
    }

    public String listItemAvailability(String managerID) throws IncorrectUserRoleException, MalformedURLException {
        ICommunicate store = this.fetchStore();
        return store.listItemAvailability(managerID);
    }

    public void purchaseItem(String customerID, String itemID, String dateOfPurchase) throws IncorrectUserRoleException, ItemOutOfStockException, NotEnoughFundsException, ExternalStorePurchaseLimitException, MalformedURLException {
        ICommunicate store = this.fetchStore();
        store.purchaseItem(customerID, itemID, dateOfPurchase);
    }

    public String findItem(String customerID, String itemName) throws IncorrectUserRoleException, MalformedURLException {
        ICommunicate store = this.fetchStore();
        return store.findItem(customerID, itemName);
    }

    public void returnItem(String customerID, String itemID, String dateOfReturn) throws ReturnPolicyException, IncorrectUserRoleException, CustomerNeverPurchasedItemException, MalformedURLException {
        ICommunicate store = this.fetchStore();
        store.returnItem(customerID, itemID, dateOfReturn);
    }

    public void addCustomerToWaitQueue(String customerID, String itemID) throws MalformedURLException {
        ICommunicate store = this.fetchStore();
        store.addCustomerToWaitQueue(customerID, itemID);
    }

    public void exchangeItem(String customerID, String newItemID, String oldItemID, String dateOfExchange) throws ReturnPolicyException,
            CustomerNeverPurchasedItemException, ExternalStorePurchaseLimitException, ItemOutOfStockException, NotEnoughFundsException, IncorrectUserRoleException, MalformedURLException {
        ICommunicate store = this.fetchStore();
        store.exchangeItem(customerID, newItemID, oldItemID, dateOfExchange);
    }

    public ICommunicate fetchStore() throws MalformedURLException {
        Map<String, String> urls = new HashMap();
        urls.put("QC", "http://localhost:8887/QC");
        urls.put("ON", "http://localhost:8888/ON");
        urls.put("BC", "http://localhost:8889/BC");

        URL url = new URL(urls.get(this.locationName) + "?wsdl");
        QName qname = new QName("http://server/", "StoreProxyService");

        Service service = Service.create(url, qname);

        return service.getPort(ICommunicate.class);
    }
}
