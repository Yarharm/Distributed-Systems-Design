package server;

import communicate.ICommunicate;
import communicate.IItem;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.SocketException;
import java.util.*;

public class Store implements ICommunicate {
    // Item Struct
    private class Item implements IItem{
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
    }

    private String location;
    private Map<String, Item> items;
    private Map<String, Deque<String>> itemsWaitlist;
    public Store(String location) {
        super();
        this.location = location;
        this.items = new HashMap<>();
        this.itemsWaitlist = new HashMap<>();
    }

    @Override
    public boolean addItem(String managerID, String itemID, String itemName, int quantity, int price) {
        return true;
    }

    @Override
    public void removeItem(String managerID, String itemID, int quantity, boolean removeCompletely) {
        return;
    }

    @Override
    public List<String> listItemAvailability(String managerID) {
        return new ArrayList<String>(Arrays.asList("ITEM 1", "ITEM 2"));
    }

    @Override
    public boolean purchaseItem(String customerID, String itemID, Date dateOfPurchase) {
        return true;
    }

    @Override
    public List<String> findItem(String customerID, String itemName) {
        return null;
    }

    @Override
    public boolean returnItem(String customerID, String itemID, Date dateOfReturn) {
        return true;
    }

    public void listen() {
        DatagramSocket aSocket = null;
        try {
            aSocket = new DatagramSocket(8888);
            byte[] buffer = new byte[1000];
            System.out.println("Server 8888 Started............");
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
