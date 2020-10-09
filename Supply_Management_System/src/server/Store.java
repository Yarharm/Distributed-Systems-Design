package server;

import UDP.*;
import communicate.ICommunicate;
import communicate.Item;
import exceptions.*;

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
                        byte[] buf = this.handler.marshallMesage(Collections.singletonList(UDP_REQUEST_STATUS_FAILURE));
                        try {
                            if (args.get(0).equals(FIND_ITEM)) {
                                String itemName= args.get(2);
                                Store.this.logger.info("Received request to " + FIND_ITEM + " from " + args.get(1) + " store " +
                                        "based on " + itemName + " name.");
                                buf = this.handler.marshallMesage(Store.this.listAllItemsByNameSync(itemName));
                            } else if(args.get(0).equals(PURCHASE_ITEM)) {
                                String storeLocation = args.get(1);
                                String customerID = args.get(2);
                                int budget = Integer.parseInt(args.get(3));
                                String itemID = args.get(4);
                                Store.this.logger.info("Received request to " + PURCHASE_ITEM + " from " + storeLocation + " store " + "" +
                                        "for the item with ID: " + itemID + ". " +
                                        "Customer information: ID=" + customerID + " and customer budget=" + budget);
                                StringBuilder sb = new StringBuilder();
                                if(!Store.this.items.containsKey(itemID)) {
                                    sb.append(ItemOutOfStockException.class.getSimpleName());
                                    Store.this.logger.info("Could not satisfy " + PURCHASE_ITEM + " request from " + storeLocation + " store. " +
                                            "Item with ID: " + itemID + " is out of stock.");
                                } else if(budget < Store.this.items.get(itemID).getPrice()) {
                                    Store.this.logger.info("Could not satisfy " + PURCHASE_ITEM + " request from " + storeLocation + " store. " +
                                            "Customer with ID: " + customerID + " and budget: " + budget + " " +
                                            "does not have enough funds to purchase an item with ID: " + itemID + ".");
                                    sb.append(NotEnoughFundsException.class.getSimpleName());
                                } else {
                                    Item item = Store.this.items.get(itemID);
                                    Store.this.reduceQuantityAfterPurchaseSync(item);
                                    sb.append(item.getPrice());
                                }
                                buf = this.handler.marshallMesage(Collections.singletonList(sb.toString()));
                            } else if(args.get(0).equals(RETURN_ITEM)) {
                                String itemID = args.get(2);
                                Store.this.logger.info("Received request to " + RETURN_ITEM + " from " + args.get(1) + "" +
                                        " store for the item ID: " + itemID);
                                Item item = Store.this.getItemByIDSync(itemID);
                                Store.this.addItem(AUTOMATIC_STORE_MANAGER, item.getItemID(), item.getItemName(), 1, item.getPrice());
                                buf = this.handler.marshallMesage(Collections.singletonList(String.valueOf(item.getPrice())));
                            } else if(args.get(0).equals(ADD_CUSTOMER_TO_WAIT_QUEUE)) {
                                String customerID = args.get(2);
                                String itemID = args.get(3);
                                Store.this.logger.info("Received request to " + ADD_CUSTOMER_TO_WAIT_QUEUE + " from " +
                                        "" + args.get(1) + " store for customer with ID: " + customerID + " and item" +
                                        " with ID: " + itemID);
                                Store.this.addCustomerToWaitlistSync(customerID, itemID);
                                buf = this.handler.marshallMesage(Collections.singletonList(UDP_REQUEST_STATUS_SUCCESS));
                            } else if(args.get(0).equals(AUTOMATICALLY_ASSIGN_ITEM)) {
                                String store = args.get(1);
                                String customerID = args.get(2);
                                String itemID = args.get(3);
                                int itemPrice = Integer.parseInt(args.get(4));
                                Store.this.logger.info("Received request to " + AUTOMATICALLY_ASSIGN_ITEM + " from " +
                                        "" + store + " store for customer with ID: " + customerID + " and item" +
                                        " with ID: " + itemID);
                                int budget = Store.this.getClientBudgetSync(customerID);
                                if(budget >= itemPrice && !Store.this.externalPurchaseLimitSync(store, customerID)) {
                                    Store.this.checkoutExternalPurchaseSync(store, customerID, itemID, budget,
                                            itemPrice, new Date(System.currentTimeMillis()));
                                    buf = this.handler.marshallMesage(Collections.singletonList(UDP_REQUEST_STATUS_SUCCESS));
                                }
                            } else if(args.get(0).equals(CLEAR_ALL_ITEM_PURCHASES)) {
                                String store = args.get(1);
                                String itemID = args.get(2);
                                Store.this.logger.info("Received request to " + CLEAR_ALL_ITEM_PURCHASES + " from " +
                                        "" + store + " store for item with ID: " + itemID + ".");
                                Store.this.clearExternalPurchasesByitemIDSync(store, itemID);
                                buf = this.handler.marshallMesage(Collections.singletonList(UDP_REQUEST_STATUS_SUCCESS));
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
    private static final int RETURN_POLICY_DAYS = 30;
    private static final String AUTOMATIC_STORE_MANAGER = "AUTOMATIC_STORE_MANAGER";
    private final String locationName;
    private final Map<String, Item> items;
    private final Map<String, Set<String>> itemsByName;
    private final Map<String, Integer> customers;
    private final Map<String, Deque<String>> itemsWaitlist;
    private final Map<String, Map<String, TreeSet<Long>>> locallyPurchasedItems;
    private final Map<String, Map<String, Long>> externallyPurchasedItems;
    private final Map<String, Set<String>> customersWithExternalPurchases;
    private final Map<String, Item> itemHistory;
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
        this.itemHistory = new HashMap<>();
        this.logger = logger;
        this.lock = new ReentrantReadWriteLock();
        this.portsConfig = new HashMap<>(portsConfig);
    }

    @Override
    public Item addItem(String managerID, String itemID, String itemName, int quantity, int price) throws ManagerItemPriceMismatchException {
        this.lock.writeLock().lock();
        Item item = new Item(itemID, itemName, quantity, price);
        try {
            String message =  "added a new item.";
            if(this.items.containsKey(itemID)) {
                if(this.items.get(itemID).getPrice() != price && !managerID.equals(AUTOMATIC_STORE_MANAGER)) {
                    throw new ManagerItemPriceMismatchException("Add item unsuccessful. Item price does not match");
                }
                int oldQuantity = this.items.get(itemID).getQuantity();
                item.setQuantity(quantity + oldQuantity);
                message = "updated an existing item.";
            }
            if(!managerID.equals(AUTOMATIC_STORE_MANAGER)) {
                this.logger.info("Manager with ID: " + managerID + " " + message + " Item information " + item.toString());
            }
            this.items.put(itemID, item);
            this.itemsByName.putIfAbsent(itemName, new HashSet<>());
            this.itemsByName.get(itemName).add(itemID);
            this.itemHistory.put(itemID, item);
            this.automaticallyAssignItem(itemID);
        } finally {
            this.lock.writeLock().unlock();
        }
        return item;
    }

    @Override
    public Item removeItem(String managerID, String itemID, int quantity) throws ManagerRemoveBeyondQuantityException, ManagerRemoveNonExistingItemException {
        this.lock.writeLock().lock();
        Item item = null;
        try {
            if(!this.items.containsKey(itemID)) {
                throw new ManagerRemoveNonExistingItemException("Item does not exist");
            }
            item = this.items.get(itemID);
            if(item.getQuantity() < quantity) {
                throw new ManagerRemoveBeyondQuantityException("Can not remove beyond the quantity.");
            }
            int updatedQuantity = item.getQuantity() - quantity;
            if(quantity == -1 || updatedQuantity <= 0) {
                this.removeItemFromStore(item);
                this.locallyPurchasedItems.remove(itemID);
                ExecutorService executor = Executors.newWorkStealingPool();
                List<StoreClientUDP> storeCallables = this.generetaAllStoreCallables(Arrays.asList(CLEAR_ALL_ITEM_PURCHASES, this.locationName, itemID));

                // Send requests
                executor.invokeAll(storeCallables);
                executor.shutdown();
                this.logger.info("Manager with ID: " + managerID + " " +
                        "has completely removed an item with ID: " + itemID);
                return item;
            }
            item.setQuantity(updatedQuantity);
            this.items.put(itemID, item);
            this.logger.info("Manager with ID: " + managerID + " removed " + quantity + "" +
                    " units from an item with ID: " + itemID);
        } catch (InterruptedException e) {
            e.printStackTrace();
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
    public void purchaseItem(String customerID, String itemID, Date dateOfPurchase) throws ItemOutOfStockException,
            NotEnoughFundsException, ExternalStorePurchaseLimitException {
        this.lock.writeLock().lock();
        try {
            String store = this.getStore(itemID);
            if(belongsToCurrentStore(store)) {
                this.purchaseItemLocally(customerID, itemID, dateOfPurchase);
            }
            else {
                if(this.externalPurchaseLimitSync(store, customerID)) {
                throw new ExternalStorePurchaseLimitException(store);
                }
                this.logger.info("Sent " + PURCHASE_ITEM + " request to the " + store + " store " +
                        "from the customer with ID: " + customerID + " for the item with ID: " + itemID);
                this.purchaseItemExternally(store, customerID, itemID, dateOfPurchase);
            }
            this.logger.info("Customer with ID: " + customerID + " successfully purchased an item with ID: " + itemID + "" +
                    " on " + dateOfPurchase + ".");
        } catch (InterruptedException | ExecutionException e) {
            e.printStackTrace();
        } finally {
            this.lock.writeLock().unlock();
        }
    }

    @Override
    public List<String> findItem(String customerID, String itemName) {
        this.lock.readLock().lock();
        List<String> collectedItems = new ArrayList<>();
        ExecutorService executor = Executors.newWorkStealingPool();
        try {
            collectedItems.addAll(this.listAllItemsByNameSync(itemName));
            // Generate callables for other stores
            List<StoreClientUDP> storeCallables = this.generetaAllStoreCallables(Arrays.asList(FIND_ITEM, this.locationName, itemName));

            // Send requests
            collectedItems.addAll(executor.invokeAll(storeCallables)
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
            if(collectedItems.get(0).isEmpty()){
                collectedItems.clear();
            }
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
    public void returnItem(String customerID, String itemID, Date dateOfReturn) throws ItemWasNeverPurchasedException,
            CustomerNeverPurchasedItemException, ReturnPolicyException {
        String store = this.getStore(itemID);
        Calendar cal = Calendar.getInstance();
        cal.setTime(dateOfReturn);
        cal.add(Calendar.DATE, -RETURN_POLICY_DAYS);
        long returnWindow = cal.getTime().getTime();
        Long purchaseTimestamp = null;
        int productPrice = 0;
        this.lock.writeLock().lock();
        try {
            if(this.belongsToCurrentStore(store)) {
                if(!this.locallyPurchasedItems.containsKey(itemID)) {
                    throw new ItemWasNeverPurchasedException("Item with ID: " + itemID + " was never purchased.");
                }
                if(!this.locallyPurchasedItems.get(itemID).containsKey(customerID)) {
                    throw new CustomerNeverPurchasedItemException("Item with ID: " + itemID + " was never purchased " +
                            "by customer with ID: " + customerID);
                }
                purchaseTimestamp = this.locallyPurchasedItems.get(itemID).get(customerID).ceiling(returnWindow);
                if(purchaseTimestamp == null) {
                    throw new ReturnPolicyException("Purchase was made beyond the return policy.");
                }
                Item item = this.itemHistory.get(itemID);
                this.addItem(AUTOMATIC_STORE_MANAGER, item.getItemID(), item.getItemName(), 1, item.getPrice());
                this.locallyPurchasedItems.get(itemID).get(customerID).remove(purchaseTimestamp);
                productPrice = item.getPrice();
            } else {
                if(!this.externallyPurchasedItems.containsKey(itemID)) {
                    throw new ItemWasNeverPurchasedException("Item with ID: " + itemID + " was never purchased.");
                }
                if(!this.externallyPurchasedItems.get(itemID).containsKey(customerID)) {
                    throw new CustomerNeverPurchasedItemException("Item with ID: " + itemID + " was never purchased " +
                            "by customer with ID: " + customerID);
                }
                purchaseTimestamp = this.externallyPurchasedItems.get(itemID).get(customerID);
                if(purchaseTimestamp < returnWindow) {
                    throw new ReturnPolicyException("Purchase was made beyond the return policy.");
                }

                ExecutorService executor = Executors.newSingleThreadExecutor();
                Future<List<String>> future = executor.submit(new StoreClientUDP(Arrays.asList(RETURN_ITEM, this.locationName, itemID),
                        this.portsConfig.get(store)));
                productPrice = Integer.parseInt(future.get().get(0));
                executor.shutdown();
                this.externallyPurchasedItems.get(itemID).remove(customerID);
                this.customersWithExternalPurchases.get(customerID).remove(store);
            }
            this.customers.put(customerID, this.customers.get(customerID) + productPrice);
            this.logger.info("Successfully returned item with ID: " + itemID + " purchased by the customer " +
                    "with ID: " + customerID + " on " + dateOfReturn);
        } catch (InterruptedException | ExecutionException | ManagerItemPriceMismatchException e) {
            e.printStackTrace();
        } finally {
            this.lock.writeLock().unlock();
        }

    }

    @Override
    public void addCustomerToWaitQueue(String customerID, String itemID) {
        String store = this.getStore(itemID);
        if(store.equals(this.locationName)) {
            this.addCustomerToWaitlistSync(customerID, itemID);
        } else {
            ExecutorService executor = Executors.newSingleThreadExecutor();
            executor.submit(new StoreClientUDP(Arrays.asList(ADD_CUSTOMER_TO_WAIT_QUEUE, this.locationName, customerID, itemID),
                    this.portsConfig.get(store)));
            executor.shutdown();
        }

    }

    public void listen() {
        new Thread(new StoreServerUDP(this.portsConfig.get(this.locationName))).start();
    }

    private List<String> listAllItemsByNameSync(String itemName) {
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

    private void reduceQuantityAfterPurchaseSync(Item item) {
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

    private void addCustomerToWaitlistSync(String customerID, String itemID) {
        this.lock.writeLock().lock();
        try {
            this.itemsWaitlist.putIfAbsent(itemID, new LinkedList<>());
            this.itemsWaitlist.get(itemID).addLast(customerID);
            this.automaticallyAssignItem(itemID);
        } finally {
            this.lock.writeLock().unlock();
        }
    }

    private Item getItemByIDSync(String itemID) {
        this.lock.readLock().lock();
        try {
            return this.itemHistory.get(itemID);
        } finally {
            this.lock.readLock().unlock();
        }
    }

    private int getClientBudgetSync(String customerID) {
        this.lock.writeLock().lock();
        try {
            this.customers.putIfAbsent(customerID, CUSTOMER_BUDGET);
            return this.customers.get(customerID);
        } finally {
            this.lock.writeLock().unlock();
        }
    }

    private boolean externalPurchaseLimitSync(String store, String customerID) {
        this.lock.readLock().lock();
        try {
            return this.customersWithExternalPurchases.containsKey(customerID) &&
                    this.customersWithExternalPurchases.get(customerID).contains(store);
        } finally {
            this.lock.readLock().unlock();
        }
    }

    private void clearExternalPurchasesByitemIDSync(String store, String itemID) {
        this.lock.writeLock().lock();
        try {
            if(this.externallyPurchasedItems.containsKey(itemID)) {
                this.externallyPurchasedItems.get(itemID).keySet()
                        .forEach(customerID -> this.customersWithExternalPurchases.get(customerID).remove(store));
                this.externallyPurchasedItems.remove(itemID);
            }
        } finally {
            this.lock.writeLock().unlock();
        }
    }

    private void automaticallyAssignItem(String itemID) {
        while(this.items.containsKey(itemID) && this.items.get(itemID).getQuantity() > 0
                && this.itemsWaitlist.containsKey(itemID) && !this.itemsWaitlist.get(itemID).isEmpty())
        {
            String customerID = this.itemsWaitlist.get(itemID).pollFirst();
            if(customerID == null) { continue; }
            String store = this.getStore(customerID);
            try {
                boolean assignmentStatus = false;
                if(this.belongsToCurrentStore(store)) {
                    this.purchaseItemLocally(customerID, itemID, new Date(System.currentTimeMillis()));
                    assignmentStatus = true;
                } else {
                    String itemPrice = String.valueOf(this.items.get(itemID).getPrice());
                    ExecutorService executor = Executors.newSingleThreadExecutor();
                    Future<List<String>> future = executor.submit(new StoreClientUDP(Arrays.asList(AUTOMATICALLY_ASSIGN_ITEM, this.locationName, customerID, itemID, itemPrice),
                            this.portsConfig.get(store)));
                    String purchaseResult = future.get().get(0);
                    assignmentStatus = purchaseResult.equals(UDP_REQUEST_STATUS_SUCCESS);
                    if(assignmentStatus) {
                        this.reduceQuantityAfterPurchaseSync(this.items.get(itemID));
                    }
                }
                if(assignmentStatus) {
                    this.logger.info("Automatically assigned item with ID: " + itemID + " to the " +
                            "customer with ID: " + customerID);
                } else {
                    this.logger.info("Attempted to automatically assigned item with ID: " + itemID + " to the " +
                            "customer with ID: " + customerID + " but customer does not have enough funds.");
                }
            } catch (ItemOutOfStockException | NotEnoughFundsException | InterruptedException | ExecutionException e) {
                e.printStackTrace();
            }
        }
    }

    private void purchaseItemLocally(String customerID, String itemID, Date dateOfPurchase) throws NotEnoughFundsException, ItemOutOfStockException {
        if(!this.items.containsKey(itemID)) {
            throw new ItemOutOfStockException("Item with ID: " + itemID + " is out of stock.");
        }
        Item item = this.items.get(itemID);
        int budget = this.getClientBudgetSync(customerID);
        if(budget < item.getPrice()) {
            throw new NotEnoughFundsException("Customer does not have enough funds!");
        }
        this.checkoutLocalPurchase(customerID, itemID, budget, item, dateOfPurchase.getTime());
    }

    private void purchaseItemExternally(String store, String customerID, String itemID, Date dateOfPurchase) throws ExecutionException,
            InterruptedException, ItemOutOfStockException, NotEnoughFundsException
    {
        int budget = this.getClientBudgetSync(customerID);
        ExecutorService executor = Executors.newSingleThreadExecutor();
        Future<List<String>> future = executor.submit(new StoreClientUDP(Arrays.asList(PURCHASE_ITEM, this.locationName, customerID, String.valueOf(budget), itemID),
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
        this.checkoutExternalPurchaseSync(store, customerID, itemID, budget, itemPrice, dateOfPurchase);
    }

    private void checkoutExternalPurchaseSync(String store, String customerID, String itemID, int budget, int price, Date dateOfPurchase) {
        this.lock.writeLock().lock();
        try{
            this.externallyPurchasedItems.putIfAbsent(itemID, new HashMap<>());
            this.externallyPurchasedItems.get(itemID).putIfAbsent(customerID, dateOfPurchase.getTime());
            this.customers.put(customerID, budget - price);
            this.customersWithExternalPurchases.putIfAbsent(customerID, new HashSet<>());
            this.customersWithExternalPurchases.get(customerID).add(store);
        } finally {
            this.lock.writeLock().unlock();
        }
    }

    private void checkoutLocalPurchase(String customerID, String itemID, int budget, Item item, long timestamp) {
        this.locallyPurchasedItems.putIfAbsent(itemID, new HashMap());
        this.locallyPurchasedItems.get(itemID).putIfAbsent(customerID, new TreeSet<>());
        this.customers.put(customerID, budget - item.getPrice());
        this.locallyPurchasedItems.get(itemID).get(customerID).add(timestamp);
        this.reduceQuantityAfterPurchaseSync(item);
    }

    private boolean belongsToCurrentStore(String storeLocation) {
        return storeLocation.equals(this.locationName);
    }

    private void removeItemFromStore(Item item) {
        String itemID = item.getItemID();
        this.items.remove(itemID);
        this.itemsByName.get(item.getItemName()).remove(itemID);
    }

    private String getStore(String storeDescriptor) {
        return storeDescriptor.substring(0, 2);
    }

    private List<StoreClientUDP> generetaAllStoreCallables(List<String> args) {
        List<StoreClientUDP> storeCallables = new ArrayList<>();
        for(Map.Entry<String, Integer> storeInfo : this.portsConfig.entrySet()) {
            String storeLocationName = storeInfo.getKey();
            int storePort = storeInfo.getValue();
            if(!storeLocationName.equals(this.locationName)) {
                storeCallables.add(new StoreClientUDP(args, storePort));
            }
        }
        return storeCallables;
    }
}
