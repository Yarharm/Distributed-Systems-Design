package server;

import communicate.ICommunicate;
import communicate.Item;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.SocketException;
import java.util.*;
import java.util.concurrent.locks.ReadWriteLock;
import java.util.concurrent.locks.ReentrantReadWriteLock;
import java.util.logging.Logger;

public class Store implements ICommunicate {
    private String locationName;
    private Map<String, Item> items;
    private Map<String, Deque<String>> itemsWaitlist;
    private Map<String, Map<String, List<Long>>> purchasedItems; // clientID -> itemID -> dates[0..*]
    private Set<String> clientsFromOtherStore;
    private Logger logger;
    private ReadWriteLock lock;

    public Store(String locationName, Logger logger) {
        super();
        this.locationName = locationName;
        this.items = new HashMap<>();
        this.itemsWaitlist = new HashMap<>();
        this.clientsFromOtherStore = new HashSet<>();
        this.logger = logger;
        this.lock = new ReentrantReadWriteLock();
    }

    /*
        TO-DO: Assign items to queue of users
     */
    @Override
    public Item addItem(String managerID, String itemID, String itemName, int quantity, int price) {
        this.lock.writeLock().lock();
        Item item = new Item(itemID, itemName, quantity, price);
        try {
            if(this.items.containsKey(itemID)) {
                int oldQuantity = this.items.get(itemID).getQuantity();
                item.setQuantity(quantity + oldQuantity);
                this.logger.info("Manager with ID: " + managerID + " updated an existing item. Item information " + item.toString());
            } else {
                this.logger.info("Manager with ID: " + managerID + " added a new item. Item information " + item.toString());
            }
            this.items.put(itemID, item);
        } finally {
            this.lock.writeLock().unlock();
        }
        return item;
    }

    /*
        TO-DO Remove items from users who already purchased those
     */
    @Override
    public Item removeItem(String managerID, String itemID, int quantity) {
        this.lock.writeLock().lock();
        try {
            if(!this.items.containsKey(itemID)) {
                String msg = quantity == -1 ? "completely remove" : "remove " + quantity + " units from";
                this.logger.info("Manager with ID: " + managerID + " was trying to " +
                        "" + msg + " an item with ID: " + itemID + "" +
                        ", but such an item does not exists in a store.");
                return null;
            }
            Item item = this.items.get(itemID);
            int updatedQuantity = item.getQuantity() - quantity;
            if(quantity == -1 || updatedQuantity <= 0) {
                this.items.remove(itemID);
                this.logger.info("Manager with ID: " + managerID + " " +
                        "has completely removed an item with ID: " + itemID);
                return null;
            }
            item.setQuantity(updatedQuantity);
            this.items.put(itemID, item);
            this.logger.info("Manager with ID: " + managerID + " removed " + quantity + "" +
                    " units from an item with ID: " + itemID);
            return item;
        } finally {
            this.lock.writeLock().unlock();
        }
    }

    @Override
    public List<Item> listItemAvailability(String managerID) {
        return null;
    }

    @Override
    public boolean purchaseItem(String customerID, String itemID, Date dateOfPurchase) {
        return true;
    }

    @Override
    public List<Item> findItem(String customerID, String itemName) {
        return null;
    }

    @Override
    public boolean returnItem(String customerID, String itemID, Date dateOfReturn) {
        return true;
    }

    public void listen(int port) {
        DatagramSocket aSocket = null;
        try {
            aSocket = new DatagramSocket(port);
            byte[] buffer = new byte[1000];
            System.out.println("Store started on port: " + port);
            while (true) {
                DatagramPacket request = new DatagramPacket(buffer, buffer.length);
                aSocket.receive(request);
                DatagramPacket reply = new DatagramPacket(request.getData(), request.getLength(), request.getAddress(),
                        request.getPort());
                aSocket.send(reply);
            }
        } catch (SocketException e) {
            System.out.println("Socket: " + e.getMessage());
        } catch (IOException e) {
            System.out.println("IO: " + e.getMessage());
        } finally {
            if (aSocket != null)
                aSocket.close();
        }
    }
}
