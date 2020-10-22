package server;

import communicate.ICommunicatePOA;
import communicate.ICommunicatePackage.*;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.SocketException;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.concurrent.*;
import java.util.logging.Logger;
import java.util.stream.Collectors;

import UDP.*;
import communicate.Item;

import static UDP.RequestTypesUDP.*;

public class Store extends ICommunicatePOA {
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
                                            "communicate.Item with ID: " + itemID + " is out of stock.");
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
                                    Store.this.checkoutExternalPurchaseSync(store, customerID, itemID,
                                            itemPrice, new Date(System.currentTimeMillis()));
                                    buf = this.handler.marshallMesage(Collections.singletonList(UDP_REQUEST_STATUS_SUCCESS));
                                }
                            } else if(args.get(0).equals(CLEAR_ALL_ITEM_PURCHASES)) {
                                String store = args.get(1);
                                String itemID = args.get(2);
                                Store.this.logger.info("Received request to " + CLEAR_ALL_ITEM_PURCHASES + " from " +
                                        "" + store + " store for item with ID: " + itemID + ".");
                                Store.this.clearExternalPurchasesByItemIDSync(store, itemID);
                                buf = this.handler.marshallMesage(Collections.singletonList(UDP_REQUEST_STATUS_SUCCESS));
                            } else if(args.get(0).equals(FETCH_PRODUCT_PRICE)) {
                                String store = args.get(1);
                                String itemID = args.get(2);
                                Store.this.logger.info("Received request to " + FETCH_PRODUCT_PRICE + " from " +
                                        "" + store + " store for item with ID: " + itemID + ".");
                                Item item = Store.this.getItemByIDSync(itemID);
                                int price = item == null ? 0 : item.getPrice();
                                buf = this.handler.marshallMesage(Collections.singletonList(String.valueOf(price)));
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
    private final ConcurrentHashMap<String, Item> items;
    private final ConcurrentHashMap<String, ConcurrentHashMap<String, String>> itemsByName;
    private final ConcurrentHashMap<String, Integer> customers;
    private final ConcurrentHashMap<String, ConcurrentLinkedDeque<String>> itemsWaitlist;
    private final ConcurrentHashMap<String, ConcurrentHashMap<String, TreeSet<Long>>> locallyPurchasedItems;
    private final ConcurrentHashMap<String, ConcurrentHashMap<String, Long>> externallyPurchasedItems;
    private final ConcurrentHashMap<String, ConcurrentHashMap<String, String>> customersWithExternalPurchases;
    private final ConcurrentHashMap<String, Item> itemHistory;
    private final Logger logger;
    private final Map<String, Integer> portsConfig;

    public Store(String locationName, Logger logger, Map<String, Integer> portsConfig) {
        super();
        this.locationName = locationName;
        this.items = new ConcurrentHashMap<>();
        this.itemsByName = new ConcurrentHashMap<>();
        this.customers = new ConcurrentHashMap<>();
        this.itemsWaitlist = new ConcurrentHashMap<>();
        this.locallyPurchasedItems = new ConcurrentHashMap<>();
        this.externallyPurchasedItems = new ConcurrentHashMap<>();
        this.customersWithExternalPurchases = new ConcurrentHashMap<>();
        this.itemHistory = new ConcurrentHashMap<>();
        this.logger = logger;
        this.portsConfig = new HashMap<>(portsConfig);
    }

    @Override
    public String addItem(String managerID, String itemID, String itemName, int quantity, int price) throws ManagerItemPriceMismatchException {
        Item item = new Item(itemID, itemName, quantity, price);
        String message =  "added a new item.";
        if(this.items.containsKey(itemID)) {
            if(this.items.get(itemID).getPrice() != price && !managerID.equals(AUTOMATIC_STORE_MANAGER)) {
                throw new ManagerItemPriceMismatchException("Add item unsuccessful. communicate.Item price does not match");
            }
            synchronized (this) {
                int oldQuantity = this.items.get(itemID).getQuantity();
                item.setQuantity(quantity + oldQuantity);
            }
            message = "updated an existing item.";
        }
        if(!managerID.equals(AUTOMATIC_STORE_MANAGER)) {
            this.logger.info("Manager with ID: " + managerID + " " + message + " communicate.Item information " + item.toString());
        }
        this.items.put(itemID, item);
        this.itemsByName.putIfAbsent(itemName, new ConcurrentHashMap<>());
        this.itemsByName.get(itemName).put(itemID, itemID);
        this.itemHistory.put(itemID, item);
        this.automaticallyAssignItem(itemID);
        return item.toString();
    }

    @Override
    public String removeItem(String managerID, String itemID, int quantity) throws ManagerRemoveBeyondQuantityException, ManagerRemoveNonExistingItemException {
        Item item = null;
        try {
            if(!this.items.containsKey(itemID)) {
                throw new ManagerRemoveNonExistingItemException("item does not exist");
            }
            item = this.items.get(itemID);
            if(item.getQuantity() < quantity) {
                throw new ManagerRemoveBeyondQuantityException("Can not remove beyond the quantity.");
            }
            synchronized (this) {
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
                    return item.toString();
                }
                item.setQuantity(updatedQuantity);
            }
            this.items.put(itemID, item);
            this.logger.info("Manager with ID: " + managerID + " removed " + quantity + "" +
                    " units from an item with ID: " + itemID);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        return item.toString();
    }

    @Override
    public String listItemAvailability(String managerID) {
        this.logger.info("Manager with ID: " + managerID + " requested a list of available items.");
        return this.items.values().stream().map(item -> item.getItemID() + " " + item.getItemName() + " " + item.getQuantity()  + " " + item.getPrice())
                .collect(Collectors.joining(", "));
    }

    @Override
    public void purchaseItem(String customerID, String itemID, String dateOfPurchase) throws ItemOutOfStockException,
            NotEnoughFundsException, ExternalStorePurchaseLimitException {
        Date processedDate = parseDate(dateOfPurchase);
        try {
            String store = this.getStore(itemID);
            if(belongsToCurrentStore(store)) {
                this.purchaseItemLocally(customerID, itemID, processedDate);
            }
            else {
                if(this.externalPurchaseLimitSync(store, customerID)) {
                    throw new ExternalStorePurchaseLimitException("External purchase limit!");
                }
                this.logger.info("Sent " + PURCHASE_ITEM + " request to the " + store + " store " +
                        "from the customer with ID: " + customerID + " for the item with ID: " + itemID);
                int itemPrice = this.purchaseItemExternally(store, customerID, itemID, processedDate);
                this.checkoutExternalPurchaseSync(store, customerID, itemID, itemPrice, processedDate);
            }
            this.logger.info("Customer with ID: " + customerID + " successfully purchased an item with ID: " + itemID + "" +
                    " on " + dateOfPurchase + ".");
        } catch (InterruptedException | ExecutionException e) {
            e.printStackTrace();
        }
    }

    @Override
    public String findItem(String customerID, String itemName) {
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
        }
        return String.join(" ", collectedItems);
    }

    @Override
    public void returnItem(String customerID, String itemID, String dateOfReturn) throws ItemWasNeverPurchasedException,
            CustomerNeverPurchasedItemException, ReturnPolicyException {
        String store = this.getStore(itemID);
        long returnWindow = generateReturnWindow(dateOfReturn);
        int productPrice = 0;
        try {
            if(this.belongsToCurrentStore(store)) {
                Long purchaseTimestamp = verifyLocalReturn(customerID, itemID, dateOfReturn, returnWindow);
                Item item = this.itemHistory.get(itemID);
                this.addItem(AUTOMATIC_STORE_MANAGER, item.getItemID(), item.getItemName(), 1, item.getPrice());
                this.locallyPurchasedItems.get(itemID).get(customerID).remove(purchaseTimestamp);
                productPrice = item.getPrice();
            } else {
                verifyExternalReturn(customerID, itemID, dateOfReturn, returnWindow);
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
        }
    }

    @Override
    public void exchangeItem(String customerID, String newItemID, String oldItemID, String dateOfExchange) throws ReturnPolicyException,
            ItemWasNeverPurchasedException, CustomerNeverPurchasedItemException, ExternalStorePurchaseLimitException, ItemOutOfStockException, NotEnoughFundsException
    {
        try {
            Date processedExchangeDate = this.parseDate(dateOfExchange);
            String oldItemStore = this.getStore(oldItemID);
            String newItemStore = this.getStore(newItemID);
            long returnWindow = this.generateReturnWindow(dateOfExchange);
            long localOldTimestamp = 0;

            /* Verify if return is possible and fetch old product price */
            if(this.belongsToCurrentStore(oldItemStore)) {
                localOldTimestamp = this.verifyLocalReturn(customerID, oldItemID, dateOfExchange, returnWindow);
            } else {
                this.verifyExternalReturn(customerID, oldItemID, dateOfExchange, returnWindow);
            }

            /* Handle same itemID */
            if(oldItemID.equals(newItemID)) {
                this.refreshTimestamp(customerID, oldItemID, localOldTimestamp, processedExchangeDate.getTime());
            }

            int oldProductPrice = this.fetchProductPrice(oldItemID);
            try {
                /* Increase customers budget temporarily */
                this.customers.put(customerID, this.customers.get(customerID) + oldProductPrice);
                if(!this.belongsToCurrentStore(newItemStore) && oldItemStore.equals(newItemStore)) {
                    int itemPrice = this.purchaseItemExternally(newItemStore, customerID, newItemID, processedExchangeDate);

                    // Return item
                    this.returnItemIgnoreVerification(customerID, oldItemID, dateOfExchange, localOldTimestamp);

                    /* Finish processing purchase of item from the same external store */
                    this.checkoutExternalPurchaseSync(newItemStore, customerID, newItemID, itemPrice, processedExchangeDate);
                } else {
                    this.purchaseItem(customerID, newItemID, dateOfExchange);
                    this.returnItemIgnoreVerification(customerID, oldItemID, dateOfExchange, localOldTimestamp);
                }
            } finally {
                this.customers.put(customerID, this.customers.get(customerID) - oldProductPrice);
            }
            this.logger.info("Customer with ID: " + customerID + " has successfully exchanged an item with ID: " + oldItemID + "" +
                    " for an item with ID: " + newItemID + " on " + dateOfExchange);

        } catch (InterruptedException | ExecutionException e) {
            e.printStackTrace();
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

    private List<String> listAllItemsByNameSync(String itemName) {
        List<String> itemsFromCurrentStore = new ArrayList<>();
        if(this.itemsByName.containsKey(itemName)) {
            itemsFromCurrentStore.addAll(this.itemsByName.get(itemName).keySet()
                    .stream().map(this.items::get)
                    .map(Item::serializeByName)
                    .collect(Collectors.toList()));
        }
        return itemsFromCurrentStore;
    }

    private synchronized void reduceQuantityAfterPurchaseSync(Item item) {
        item.removeSingleQuantity();
        if(item.getQuantity() <= 0) {
            this.removeItemFromStore(item);
        }
    }

    private void addCustomerToWaitlistSync(String customerID, String itemID) {
        this.itemsWaitlist.putIfAbsent(itemID, new ConcurrentLinkedDeque<>());
        this.itemsWaitlist.get(itemID).addLast(customerID);
        this.automaticallyAssignItem(itemID);
    }

    private Item getItemByIDSync(String itemID) {
        return this.itemHistory.get(itemID);
    }

    private int getClientBudgetSync(String customerID) {
        this.customers.putIfAbsent(customerID, CUSTOMER_BUDGET);
        return this.customers.get(customerID);
    }

    private boolean externalPurchaseLimitSync(String store, String customerID) {
        return this.customersWithExternalPurchases.containsKey(customerID) &&
                this.customersWithExternalPurchases.get(customerID).containsKey(store);
    }

    private void clearExternalPurchasesByItemIDSync(String store, String itemID) {
        if(this.externallyPurchasedItems.containsKey(itemID)) {
            this.externallyPurchasedItems.get(itemID).keySet()
                    .forEach(customerID -> this.customersWithExternalPurchases.get(customerID).remove(store));
            this.externallyPurchasedItems.remove(itemID);
        }
    }

    private void checkoutExternalPurchaseSync(String store, String customerID, String itemID, int price, Date dateOfPurchase) {
        int budget = this.getClientBudgetSync(customerID);
        this.externallyPurchasedItems.putIfAbsent(itemID, new ConcurrentHashMap<>());
        this.externallyPurchasedItems.get(itemID).putIfAbsent(customerID, dateOfPurchase.getTime());
        this.customers.put(customerID, budget - price);
        this.customersWithExternalPurchases.putIfAbsent(customerID, new ConcurrentHashMap<>());
        this.customersWithExternalPurchases.get(customerID).put(store, store);
    }

    private long verifyExternalReturn(String customerID, String itemID, String dateOfReturn, long returnWindow) throws ItemWasNeverPurchasedException, CustomerNeverPurchasedItemException, ReturnPolicyException {
        if(!this.externallyPurchasedItems.containsKey(itemID)) {
            throw new ItemWasNeverPurchasedException("communicate.Item with ID: " + itemID + " was never purchased.");
        }
        if(!this.externallyPurchasedItems.get(itemID).containsKey(customerID)) {
            throw new CustomerNeverPurchasedItemException("communicate.Item with ID: " + itemID + " was never purchased " +
                    "by customer with ID: " + customerID);
        }
        Long purchaseTimestamp = this.externallyPurchasedItems.get(itemID).get(customerID);
        if(purchaseTimestamp < returnWindow || purchaseTimestamp > parseDate(dateOfReturn).getTime()) {
            throw new ReturnPolicyException("Purchase was made beyond the return policy.");
        }
        return purchaseTimestamp;
    }

    private long verifyLocalReturn(String customerID, String itemID, String dateOfReturn, long returnWindow) throws ItemWasNeverPurchasedException, CustomerNeverPurchasedItemException, ReturnPolicyException {
        if(!this.locallyPurchasedItems.containsKey(itemID)) {
            throw new ItemWasNeverPurchasedException("communicate.Item with ID: " + itemID + " was never purchased.");
        }
        if(!this.locallyPurchasedItems.get(itemID).containsKey(customerID)) {
            throw new CustomerNeverPurchasedItemException("communicate.Item with ID: " + itemID + " was never purchased " +
                    "by customer with ID: " + customerID);
        }
        Long purchaseTimestamp = this.locallyPurchasedItems.get(itemID).get(customerID).ceiling(returnWindow);
        if(purchaseTimestamp == null || purchaseTimestamp > parseDate(dateOfReturn).getTime()) {
            throw new ReturnPolicyException("Purchase was made beyond the return policy.");
        }
        return purchaseTimestamp;
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
            throw new ItemOutOfStockException("communicate.Item with ID: " + itemID + " is out of stock.");
        }
        Item item = this.items.get(itemID);
        int budget = this.getClientBudgetSync(customerID);
        if(budget < item.getPrice()) {
            throw new NotEnoughFundsException("Customer does not have enough funds!");
        }
        this.checkoutLocalPurchase(customerID, itemID, budget, item, dateOfPurchase.getTime());
    }

    private int purchaseItemExternally(String store, String customerID, String itemID, Date dateOfPurchase) throws ExecutionException,
            InterruptedException, ItemOutOfStockException, NotEnoughFundsException
    {
        int budget = this.getClientBudgetSync(customerID);
        ExecutorService executor = Executors.newSingleThreadExecutor();
        Future<List<String>> future = executor.submit(new StoreClientUDP(Arrays.asList(PURCHASE_ITEM, this.locationName, customerID, String.valueOf(budget), itemID),
                this.portsConfig.get(store)));
        String purchaseResult = future.get().get(0);
        if(purchaseResult.equals(ItemOutOfStockException.class.getSimpleName())) {
            throw new ItemOutOfStockException("communicate.Item with ID: " + itemID + " is out of stock.");
        }
        if(purchaseResult.equals(NotEnoughFundsException.class.getSimpleName())) {
            throw new NotEnoughFundsException("Customer does not have enough funds!");
        }
        executor.shutdown();
        return Integer.parseInt(purchaseResult);
    }

    private void checkoutLocalPurchase(String customerID, String itemID, int budget, Item item, long timestamp) {
        this.locallyPurchasedItems.putIfAbsent(itemID, new ConcurrentHashMap());
        this.locallyPurchasedItems.get(itemID).putIfAbsent(customerID, new TreeSet<>());
        this.locallyPurchasedItems.get(itemID).get(customerID).add(timestamp);
        this.customers.put(customerID, budget - item.getPrice());
        this.reduceQuantityAfterPurchaseSync(item);
    }

    private int fetchProductPrice(String itemID) throws ExecutionException, InterruptedException {
        String store = this.getStore(itemID);
        int productPrice = 0;
        if(this.belongsToCurrentStore(store)) {
            Item oldItem = this.getItemByIDSync(itemID);
            productPrice = oldItem == null ? 0 : oldItem.getPrice();
        } else {
            ExecutorService executor = Executors.newSingleThreadExecutor();
            Future<List<String>> future = executor.submit(new StoreClientUDP(Arrays.asList(FETCH_PRODUCT_PRICE, this.locationName, itemID),
                    this.portsConfig.get(store)));
            productPrice = Integer.parseInt(future.get().get(0));
            executor.shutdown();
        }
        return productPrice;
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

    private Date parseDate(String date) {
        DateFormat sourceFormat = new SimpleDateFormat("ddMMyyyy");
        try {
            return sourceFormat.parse(date);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return new Date();
    }

    private long generateReturnWindow(String date) {
        Calendar cal = Calendar.getInstance();
        cal.setTime(parseDate(date));
        cal.add(Calendar.DATE, -RETURN_POLICY_DAYS);
        return cal.getTime().getTime();
    }

    private void refreshTimestamp(String customerID, String itemID, long oldTimestamp, long freshTimestamp) {
        String store = this.getStore(itemID);
        if(this.belongsToCurrentStore(store)) {
            this.locallyPurchasedItems.get(itemID).get(customerID).remove(oldTimestamp);
            this.locallyPurchasedItems.get(itemID).get(customerID).add(freshTimestamp);
        } else {
            this.externallyPurchasedItems.get(itemID).put(customerID, freshTimestamp);
        }
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

    public void returnItemIgnoreVerification(String customerID, String itemID, String dateOfReturn, Long purchaseTimestamp) {
        String store = this.getStore(itemID);
        int productPrice = 0;
        try {
            if(this.belongsToCurrentStore(store)) {
                Item item = this.itemHistory.get(itemID);
                this.addItem(AUTOMATIC_STORE_MANAGER, item.getItemID(), item.getItemName(), 1, item.getPrice());
                if(this.locallyPurchasedItems.containsKey(itemID) && this.locallyPurchasedItems.get(itemID).containsKey(customerID)) {
                    this.locallyPurchasedItems.get(itemID).get(customerID).remove(purchaseTimestamp);
                }
                productPrice = item.getPrice();
            } else {
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
        }
    }

    public void listen() {
        new Thread(new StoreServerUDP(this.portsConfig.get(this.locationName))).start();
    }
}
