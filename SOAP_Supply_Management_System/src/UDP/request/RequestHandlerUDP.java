package UDP.request;


import exceptions.ItemOutOfStockException;
import exceptions.NotEnoughFundsException;
import server.data.inventory.InventoryPool;
import server.data.inventory.StoreInventory;
import server.data.sales.SalesManager;
import server.data.sales.SalesManagerPool;

import java.util.Collections;
import java.util.List;

public class RequestHandlerUDP implements RequestHandler {
    private final StoreInventory storeInventory;
    private final SalesManager salesManager;
    public RequestHandlerUDP(String locationName) {
        this.storeInventory = InventoryPool.getInventoryOnLocation(locationName);
        this.salesManager = SalesManagerPool.getSalesManagerOnLocation(locationName);
    }

    @Override
    public List<String> findItem(String itemName) {
        return this.storeInventory.getStockByName(itemName);
    }

    @Override
    public List<String> purchaseItem(String itemID, int budget) {
        StringBuilder result = new StringBuilder();
        int itemPrice = this.storeInventory.getItemPrice(itemID);

        if(!this.storeInventory.isItemInStockWithQuantity(itemID)) {
            result.append(ItemOutOfStockException.class.getSimpleName());
        } else if(budget < itemPrice) {
            result.append(NotEnoughFundsException.class.getSimpleName());
        } else {
            this.storeInventory.reduceItemQuantityInStock(itemID);
            result.append(itemPrice);
        }
        return Collections.singletonList(result.toString());
    }

    @Override
    public int getItemPrice(String itemID) {
        return this.storeInventory.getItemPrice(itemID);
    }

    @Override
    public void appendToWaitQueue(String customerID, String itemID) {
        this.salesManager.appendCustomerToWaitQueue(customerID, itemID);
    }
}
