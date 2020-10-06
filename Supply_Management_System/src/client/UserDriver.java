package client;

import java.io.IOException;
import java.rmi.NotBoundException;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.*;


public class UserDriver {
    private static final Map<String, Customer> customers = new HashMap<>();
    private static final Map<String, Manager> managers = new HashMap<>();
    private static final Set<String> locations = new HashSet<>();
    private static final DateFormat sourceFormat = new SimpleDateFormat("ddMMyyyy");

    public static void main(String[] args) {
        locations.add("QC");
        locations.add("BC");
        locations.add("ON");

        try {
            prepopulate();
            while(true) {
                Scanner myObj = new Scanner(System.in);
                System.out.println("Choose user client:");
                System.out.println("1.managerClient");
                System.out.println("2.customerClient");
                System.out.println("3.exit");

                String clientChoice = myObj.nextLine();
                System.out.println();
                if(clientChoice.equals("1")) {
                    runManagerClient();
                } else if(clientChoice.equals("2")) {
                    runCustomerClient();
                } else {
                    System.exit(1);
                }
            }
        } catch(IOException e) {
            System.err.println("Could not setup user logger");
            e.printStackTrace();
        } catch(NotBoundException e) {
            System.err.println("Could not lookup store in registry");
        } catch (Exception e) {
            System.err.println("User exception:");
            e.printStackTrace();
        }
    }

    private static void runManagerClient() throws IOException, NotBoundException, NumberFormatException {
        while(true) {
            Scanner myObj = new Scanner(System.in);
            System.out.println("1.addItem");
            System.out.println("2.removeItem");
            System.out.println("3.listItemAvailability");
            System.out.println("4.Back to the client menu");
            String operationChoice = myObj.nextLine();
            System.out.println();
            if(operationChoice.equals("1")) {
                System.out.print("Operation (managerID, itemID, itemName, quantity, price): ");
                String[] args = myObj.nextLine().split(" ");
                if(!validateUserID(args[0]) || !validateItemID(args[1])) {
                    System.out.println("INVALID INPUT");
                    continue;
                }
                Manager manager = getManager(args[0]);
                manager.addItem(args[0], args[1], args[2], Integer.parseInt(args[3]), Integer.parseInt(args[4]));
            } else if(operationChoice.equals("2")) {
                System.out.print("Operation (managerID, itemID, quantity): ");
                String[] args = myObj.nextLine().split(" ");
                if(!validateUserID(args[0]) || !validateItemID(args[1])) {
                    System.out.println("INVALID INPUT");
                    continue;
                }
                Manager manager = getManager(args[0]);
                manager.removeItem(args[0], args[1], Integer.parseInt(args[2]));
            } else if(operationChoice.equals("3")) {
                System.out.print("Operation (managerID): ");
                String[] args = myObj.nextLine().split(" ");
                if(!validateUserID(args[0])) {
                    System.out.println("INVALID INPUT");
                    continue;
                }
                Manager manager = getManager(args[0]);
                manager.listItemAvailability(args[0]);
            } else if(operationChoice.equals("4")) {
                break;
            }
            System.out.println();
        }

    }

    private static void runCustomerClient() throws ParseException, IOException, NotBoundException {
        while(true) {
            Scanner myObj = new Scanner(System.in);
            System.out.println("1.purchaseItem");
            System.out.println("2.findItem");
            System.out.println("3.returnItem");
            System.out.println("4.Back to the client menu");
            String operationChoice = myObj.nextLine();
            System.out.println();
            if(operationChoice.equals("1")) {
                System.out.print("Operation (customerID, itemID, dateOfPurchase(ddmmyyyy): ");
                String[] args = myObj.nextLine().split(" ");
                if(!validateUserID(args[0]) || !validateItemID(args[1])) {
                    System.out.println("INVALID INPUT");
                    continue;
                }
                Customer customer = getCustomer(args[0]);
                customer.purchaseItem(args[0], args[1], sourceFormat.parse(args[2]));
            } else if(operationChoice.equals("2")) {
                System.out.print("Operation (customerID, itemName): ");
                String[] args = myObj.nextLine().split(" ");
                if(!validateUserID(args[0])) {
                    System.out.println("INVALID INPUT");
                    continue;
                }
                Customer customer = getCustomer(args[0]);
                customer.findItem(args[0], args[1]);
            } else if(operationChoice.equals("3")) {
                System.out.print("Operation (customerID, itemID, dateOfReturn(ddmmyyyy)): ");
                String[] args = myObj.nextLine().split(" ");
                if(!validateUserID(args[0]) || !validateItemID(args[1])) {
                    System.out.println("INVALID INPUT");
                    continue;
                }
                Customer customer = getCustomer(args[0]);
                customer.returnItem(args[0], args[1], sourceFormat.parse(args[2]));
            } else if(operationChoice.equals("4")) {
                break;
            }
            System.out.println();
        }
    }

    public static Customer getCustomer(String customerID) throws IOException {
        Customer customer = customers.get(customerID);
        if(customer == null) {
            customer = new Customer(customerID, customerID.substring(0, 2));
            customer.setupLogger();
            customers.put(customerID, customer);
        }
        return customer;
    }


    public static Manager getManager(String managerID) throws IOException {
        Manager manager = managers.get(managerID);
        if(manager == null) {
            manager = new Manager(managerID, managerID.substring(0, 2));
            manager.setupLogger();
            managers.put(managerID, manager);
        }
        return manager;
    }

    private static boolean validateUserID(String userID) {
        if(userID.length() != 7 || !locations.contains(userID.substring(0, 2))) {
            return false;
        }
        char role = userID.charAt(2);
        if(role != 'U' && role != 'M') {
            return false;
        }
        return true;
    }

    private static boolean validateItemID(String itemID) {
        if(itemID.length() != 6 || !locations.contains(itemID.substring(0, 2))) {
            return false;
        }
        return true;
    }

    private static void prepopulate() throws IOException{
        List<String> managers = new ArrayList<>(Arrays.asList("QCM9572", "BCM4399", "ONM2936", "QCM3492", "BCM5811", "ONM4325"));
        List<String> customers = new ArrayList<>(Arrays.asList("QCU9572", "BCU4399", "ONU2936", "QCU3492", "BCU5811", "ONU4325"));

        for(String managerID : managers) { getManager(managerID); }
        for(String customerID : customers) { getCustomer(customerID); }

    }
}
