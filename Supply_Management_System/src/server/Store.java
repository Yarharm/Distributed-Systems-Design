package server;

import UDP.*;
import communicate.ICommunicate;
import communicate.Item;
import exceptions.ExternalStorePurchaseLimitException;
import exceptions.ItemOutOfStockException;
import exceptions.NotEnoughFundsException;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.SocketException;
import java.util.*;
import java.util.concurrent.*;
import java.util.concurrent.locks.ReadWriteLock;
import java.util.concurrent.locks.ReentrantReadWriteLock;
import java.util.logging.Logger;
import java.util.stream.Collectors;

import static UDP.RequestTypesUDP.*;

public class Store implements ICommunicate {
    public class StoreServerUDP implements Runnable {
        private final int port;
        private final RequestHandlerUDP handler;
        private final ExecutorService executor;

        public StoreServerUDP(int port) {
            this.port = port;
            this.handler = new RequestHandlerUDP();
            this.executor = Executors.newWorkStealingPool();
        }

        @Override
        public void run() {
            try (DatagramSocket aSocket = new DatagramSocket(port)) {
                System.out.println("Store started on port: " + port);
                while (true) {
                    byte[] buffer = new byte[20000];
                    DatagramPacket request = new DatagramPacket(buffer, buffer.length);
                    aSocket.receive(request);
                    List<String> args = this.handler.unmarshallMessage(request);

                    this.executor.execute(() -> {
                        byte[] buf = new byte[0];
                        try {
                            if (args.get(0).equals(FIND_ITEM)) {
                                String itemName= args.get(2);
                                Store.this.logger.info("Received request to " + args.get(0) + " from " + args.get(1) + " store " +
                                        "based on " + itemName + " name.");
                                buf = this.handler.marshallMesage(Store.this.listAllItemsByName(itemName));
                            } else if(args.get(0).equals(PURCHASE_ITEM)) {
                                int budget = Integer.parseInt(args.get(2));
                                String itemID = args.get(3);
                                Store.this.logger.info("Received request to " + args.get(0) + " from " + args.get(1) + " store " + "" +
                                        "for the item with ID: " + itemID + " and customer budget of " + budget);
                                StringBuilder sb = new StringBuilder();
                                if(!Store.this.items.containsKey(itemID)) {
                                    sb.append(ItemOutOfStockException.class.getSimpleName());
                                } else if(budget < Store.this.items.get(itemID).getPrice()) {
                                    sb.append(NotEnoughFundsException.class.getSimpleName());
                                } else {
                                    Item item = Store.this.items.get(itemID);
                                    Store.this.reduceQuantityAfterPurchase(item);
                                    sb.append(item.getPrice());
                                }
                                buf = this.handler.marshallMesage(Collections.singletonList(sb.toString()));
                            }
                            DatagramPacket reply = new DatagramPacket(buf, buf.length, request.getAddress(), request.getPort());
                            aSocket.send(reply);
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    });
                }
            } catch (SocketException e) {
                System.out.println("Socket: " + e.getMessage());
            } catch (IOException e) {
                System.out.println("IO: " + e.getMessage());
            } catch (Exception e) {
                e.printStackTrace();
            } finally {
                this.executor.shutdown();
            }
        }
    }

    private static final int CUSTOMER_BUDGET = 1000;
    private final String locationName;
    private final Map<String, Item> items;
    private final Map<String, Set<String>> itemsByName; // itemName -> itemID
    private final Map<String, Integer> customers;
    private final Map<String, Deque<String>> itemsWaitlist;
    private final Map<String, Map<String, TreeSet<Long>>> locallyPurchasedItems; // itemID -> customerID -> dates[0..*]
    private final Map<String, Map<String, Long>> externallyPurchasedItems; // itemdID -> customerID -> date
    private final Map<String, Set<String>> customersWithExternalPurchases; // customerID -> Store[0..*]
    private final Logger logger;
    private final ReadWriteLock lock;
    private final Map<String, Integer> portsConfig;

    public Store(String locationName, Logger logger, Map<String, Integer> portsConfig) {
        super();
        this.locationName = locationName;
        this.items = new HashMap<>();
        this.itemsByName = new HashMap<>();
        this.customers = new HashMap<>();
        this.itemsWaitlist = new HashMap<>();
        this.locallyPurchasedItems = new HashMap<>();
        this.externallyPurchasedItems = new HashMap<>();
        this.customersWithExternalPurchases = new HashMap<>();
        this.logger = logger;
        this.lock = new ReentrantReadWriteLock();
        this.portsConfig = new HashMap<>(portsConfig);
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
            this.itemsByName.putIfAbsent(itemName, new HashSet<>());
            this.itemsByName.get(itemName).add(itemID);
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
        Item item = null;
        try {
            if(!this.items.containsKey(itemID)) {
                String msg = quantity == -1 ? "completely remove" : "remove " + quantity + " units from";
                this.logger.info("Manager with ID: " + managerID + " was trying to " +
                        "" + msg + " an item with ID: " + itemID + "" +
                        ", but such an item does not exists in a store.");
                return null;
            }
            item = this.items.get(itemID);
            int updatedQuantity = item.getQuantity() - quantity;
            if(quantity == -1 || updatedQuantity <= 0) {
                this.removeItemFromStore(item);
                this.logger.info("Manager with ID: " + managerID + " " +
                        "has completely removed an item with ID: " + itemID);
                return null;
            }
            item.setQuantity(updatedQuantity);
            this.items.put(itemID, item);
            this.logger.info("Manager with ID: " + managerID + " removed " + quantity + "" +
                    " units from an item with ID: " + itemID);
        } finally {
            this.lock.writeLock().unlock();
        }
        return item;
    }

    @Override
    public List<Item> listItemAvailability(String managerID) {
        this.lock.readLock().lock();
        List<Item> items = null;
        try {
            items = new ArrayList<>(this.items.values());
            this.logger.info("Manager with ID: " + managerID + " requested a list of available items.");
        } finally {
            lock.readLock().unlock();
        }
        return items;
    }

    @Override
    public boolean purchaseItem(String customerID, String itemID, Date dateOfPurchase) throws ItemOutOfStockException,
            NotEnoughFundsException, ExternalStorePurchaseLimitException {
        this.lock.writeLock().lock();
        boolean purchaseStatus = false;
        try {
            String store = itemID.substring(0, 2);
            if(itemFromCurrentStore(store)) {
                purchaseStatus = this.purchaseItemLocally(customerID, itemID, dateOfPurchase);
            }
            else {
                if(this.customersWithExternalPurchases.containsKey(customerID) &&
                        this.customersWithExternalPurchases.get(customerID).contains(store)) {
                throw new ExternalStorePurchaseLimitException(store);
                }
                purchaseStatus = this.purchaseItemExternally(store, customerID, itemID, dateOfPurchase);
            }
        } catch (InterruptedException | ExecutionException e) {
            e.printStackTrace();
        } finally {
            this.lock.writeLock().unlock();
        }
        if(purchaseStatus) {
            this.logger.info("Customer with ID: " + customerID + " successfully purchased an item with ID: " + itemID + "" +
                    " on " + dateOfPurchase + ".");
        }
        return purchaseStatus;
    }

    @Override
    public List<String> findItem(String customerID, String itemName) {
        this.lock.readLock().lock();
        List<String> collectedItems = new ArrayList<>();
        ExecutorService executor = Executors.newWorkStealingPool();
        try {
            collectedItems.addAll(this.listAllItemsByName(itemName));

            // Generate callables for other stores
            List<StoreClientUDP> callableStores = new ArrayList<>();
            for(Map.Entry<String, Integer> storeInfo : this.portsConfig.entrySet()) {
                String storeLocationName = storeInfo.getKey();
                int storePort = storeInfo.getValue();
                if(!storeLocationName.equals(this.locationName)) {
                    callableStores.add(new StoreClientUDP(Arrays.asList(FIND_ITEM, this.locationName, itemName), storePort));
                }
            }

            // Send requests
            collectedItems.addAll(executor.invokeAll(callableStores)
                    .stream()
                    .map(future -> {
                        List<String> requestedItems = new ArrayList<>();
                        try {
                            requestedItems.addAll(future.get());
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                        return requestedItems;
                    })
                    .flatMap(List::stream)
                    .collect(Collectors.toList()));
            executor.shutdown();
            executor.awaitTermination(60, TimeUnit.SECONDS);

            this.logger.info("Customer with ID: " + customerID + " requested to find all items based on " + itemName + " name." +
                    " Fetched the list of the following items from all available stores: " + collectedItems);
        } catch (InterruptedException e) {
            this.logger.severe("UDP REQUEST FAILED TO FIND ITEM!");
            e.printStackTrace();
        } catch (Exception e) {
            throw new IllegalStateException(e);
        } finally {
            this.lock.readLock().unlock();
        }
        return collectedItems;
    }

    @Override
    public boolean returnItem(String customerID, String itemID, Date dateOfReturn) {
        return true;
    }

    @Override
    public void addCustomerToWaitQueue(String customerID, String itemID) {
        this.itemsWaitlist.putIfAbsent(itemID, new LinkedList<>());
        this.itemsWaitlist.get(itemID).addLast(customerID);
    }

    public void listen() {
        new Thread(new StoreServerUDP(this.portsConfig.get(this.locationName))).start();
    }

    private List<String> listAllItemsByName(String itemName) {
        this.lock.readLock().lock();
        List<String> itemsFromCurrentStore = new ArrayList<>();
        try {
            if(this.itemsByName.containsKey(itemName)) {
                itemsFromCurrentStore.addAll(this.itemsByName.get(itemName)
                        .stream().map(this.items::get)
                        .map(Item::serializeByName)
                        .collect(Collectors.toList()));
            }
        } finally {
            this.lock.readLock().unlock();
        }
        return itemsFromCurrentStore;
    }

    private boolean purchaseItemLocally(String customerID, String itemID, Date dateOfPurchase) throws NotEnoughFundsException, ItemOutOfStockException {
        if(!this.items.containsKey(itemID)) {
            throw new ItemOutOfStockException("Item with ID: " + itemID + " is out of stock.");
        }
        Item item = this.items.get(itemID);
        int budget = this.getClientBudget(customerID);
        if(budget < item.getPrice()) {
            throw new NotEnoughFundsException("Customer does not have enough funds!");
        }
        this.checkoutLocalPurchase(customerID, itemID, budget, item, dateOfPurchase.getTime());
        return true;
    }

    private boolean purchaseItemExternally(String store, String customerID, String itemID, Date dateOfPurchase) throws ExecutionException, InterruptedException, ItemOutOfStockException, NotEnoughFundsException {
        int budget = this.getClientBudget(customerID);
        ExecutorService executor = Executors.newSingleThreadExecutor();
        Future<List<String>> future = executor.submit(new StoreClientUDP(Arrays.asList(PURCHASE_ITEM, this.locationName, String.valueOf(budget), itemID),
                this.portsConfig.get(store)));
        String purchaseResult = future.get().get(0);
        if(purchaseResult.equals(ItemOutOfStockException.class.getSimpleName())) {
            throw new ItemOutOfStockException("Item with ID: " + itemID + " is out of stock.");
        }
        if(purchaseResult.equals(NotEnoughFundsException.class.getSimpleName())) {
            throw new NotEnoughFundsException("Customer does not have enough funds!");
        }
        executor.shutdown();
        int itemPrice = Integer.parseInt(purchaseResult);
        this.checkoutExternalPurchase(store, customerID, itemID, budget, itemPrice, dateOfPurchase);
        return true;
    }

    private void checkoutExternalPurchase(String store, String customerID, String itemID, int budget, int price, Date dateOfPurchase) {
        this.externallyPurchasedItems.putIfAbsent(itemID, new HashMap<>());
        this.externallyPurchasedItems.get(itemID).putIfAbsent(customerID, dateOfPurchase.getTime());
        this.customers.put(customerID, budget - price);
        this.customersWithExternalPurchases.putIfAbsent(customerID, new HashSet<>());
        this.customersWithExternalPurchases.get(customerID).add(store);
    }

    private void checkoutLocalPurchase(String customerID, String itemID, int budget, Item item, long timestamp) {
        this.locallyPurchasedItems.putIfAbsent(itemID, new HashMap());
        this.locallyPurchasedItems.get(itemID).putIfAbsent(customerID, new TreeSet<>());
        this.customers.put(customerID, budget - item.getPrice());
        this.locallyPurchasedItems.get(itemID).get(customerID).add(timestamp);
        this.reduceQuantityAfterPurchase(item);
    }

    private void reduceQuantityAfterPurchase(Item item) {
        this.lock.writeLock().lock();
        try {
            item.removeSingleQuantity();
            if(item.getQuantity() <= 0) {
                this.removeItemFromStore(item);
            } else {
                this.items.put(item.getItemID(), item);
            }
        } finally {
            this.lock.writeLock().unlock();
        }
    }

    private int getClientBudget(String customerID) {
        this.customers.putIfAbsent(customerID, CUSTOMER_BUDGET);
        return this.customers.get(customerID);
    }

    private boolean itemFromCurrentStore(String storeLocation) {
        return storeLocation.equals(this.locationName);
    }

    private void removeItemFromStore(Item item) {
        String itemID = item.getItemID();
        this.items.remove(itemID);
        this.itemsByName.get(item.getItemName()).remove(itemID);
    }
}
