package communicate;

import java.io.Serializable;

public class Item implements Serializable {
    private static final long serialVersionUID = 1L;
    private String itemID;
    private String itemName;
    private int quantity;
    private int price;

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

    public void setQuantity(int quantity) {
        this.quantity = quantity;
    }

    public String getItemID() {
        return this.itemID;
    }
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