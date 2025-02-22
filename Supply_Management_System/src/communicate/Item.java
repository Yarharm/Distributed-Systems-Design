package communicate;

import java.io.Serializable;

public class Item implements Serializable {
    private static final long serialVersionUID = 1L;
    private final String itemID;
    private final String itemName;
    private final int price;
    private int quantity;

    public Item(String itemID, String itemName, int quantity, int price) {
        this.itemID = itemID;
        this.itemName = itemName;
        this.quantity = quantity;
        this.price = price;
    }

    @Override
    public String toString() {
        return "itemID=" + itemID + ", " + "itemName=" + itemName +
                ", quantity=" + quantity + ", price=" + price;
    }

    public String serializeByName() {
        return this.itemID + " " + this.quantity + " " + this.price;
    }

    public void setQuantity(int quantity) {
        this.quantity = quantity;
    }
    public String getItemID() {
        return this.itemID;
    }
    public void removeSingleQuantity() { this.quantity--; }
    public String getItemName() {
        return this.itemName;
    }
    public int getQuantity() {
        return this.quantity;
    }
    public int getPrice() {
        return this.price;
    }
}