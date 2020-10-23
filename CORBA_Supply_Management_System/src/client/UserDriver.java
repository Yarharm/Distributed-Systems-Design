package client;

import org.omg.CORBA.ORBPackage.InvalidName;
import org.omg.CosNaming.NamingContextPackage.CannotProceed;
import org.omg.CosNaming.NamingContextPackage.NotFound;

import java.io.IOException;
import java.nio.file.DirectoryStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;


public class UserDriver {
    private static final ConcurrentHashMap<String, Customer> customers = new ConcurrentHashMap<>();
    private static final ConcurrentHashMap<String, Manager> managers = new ConcurrentHashMap<>();
    private static final Set<String> locations = new HashSet<>();
    private static String[] arguments;

    public static void main(String[] args) {
        locations.add("QC");
        locations.add("BC");
        locations.add("ON");
        arguments = args;
        try {
            prepopulate();
            while(true) {
                Scanner myObj = new Scanner(System.in);
                System.out.println("Choose user client:");
                System.out.println("1.managerClient");
                System.out.println("2.customerClient");
                System.out.println("3.Concurrent driver");
                System.out.println("4.exit");

                String clientChoice = myObj.nextLine();
                System.out.println();
                if(clientChoice.equals("1")) {
                    runManagerClient();
                } else if(clientChoice.equals("2")) {
                    runCustomerClient();
                } else if(clientChoice.equals("3")) {
                    runConcurrentDriver();
                } else {
                    System.exit(1);
                }
            }
        } catch (Exception e) {
            System.err.println("User exception:");
            e.printStackTrace();
        }
    }

    private static void runConcurrentDriver() throws InvalidName, CannotProceed, org.omg.CosNaming.NamingContextPackage.InvalidName, NotFound, IOException, InterruptedException {
        Scanner myObj = new Scanner(System.in);
        System.out.println("Choose operation:");
        System.out.println("1.Purchase + Exchange");
        System.out.println("2.Add + Purchase + Remove");
        System.out.println("3.Purchase + Return + Exchange");
        System.out.println("4.Exchange Atomicity 1");
        System.out.println("5.Exchange Atomicity 2");
        String operationChoice = myObj.nextLine();
        System.out.println();
        if(operationChoice.equals("1")) {
            Manager manager = getManager("QCM1111");
            manager.addItem("QCM1111", "QC1111", "fish", 1, 20);
            manager.addItem("QCM1111", "QC2222", "milk", 2, 100);

            Customer customerBC = getCustomer("BCU1111");
            Customer customerON = getCustomer("ONU1111");
            Customer customerQC = getCustomer("QCU1111");

            new Thread(() -> {
                try {
                    customerQC.purchaseItem("QCU1111", "QC1111", "11041995");
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }).start();
            new Thread(() -> {
                try {
                    customerON.purchaseItem("ONU1111", "QC1111", "11041995");
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }).start();
            new Thread(() -> {
                try {
                    customerBC.purchaseItem("BCU1111", "QC2222", "11041995");
                    customerBC.exchangeItem("BCU1111", "QC1111", "QC2222", "11041995");
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }).start();
        } else if(operationChoice.equals("2")) {
            Manager manager = getManager("QCM1111");
            manager.addItem("QCM1111", "QC1111", "fish", 5, 20);
            Customer customerBC1111 = getCustomer("BCU1111");
            Customer customerON1111 = getCustomer("ONU1111");
            Customer customerQC1111 = getCustomer("QCU1111");
            Customer customerBC2222 = getCustomer("BCU2222");

            new Thread(() -> {
                try {
                    customerBC1111.purchaseItem("BCU1111", "QC1111", "10041995");
                    customerBC1111.returnItem("BCU1111", "QC1111", "10041995");
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }).start();
            new Thread(() -> {
                try {
                    customerON1111.purchaseItem("ONU1111", "QC1111", "10041995");
                    customerON1111.returnItem("ONU1111", "QC1111", "10041995");
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }).start();
            new Thread(() -> {
                try {
                    manager.removeItem("QCM1111", "QC1111", 2);
                    manager.addItem("QCM1111", "QC1111", "fish", 2, 20);
                    manager.removeItem("QCM1111", "QC1111", 2);
                    manager.addItem("QCM1111", "QC1111", "fish", 2, 20);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }).start();
            new Thread(() -> {
                try {
                    customerQC1111.purchaseItem("QCU1111", "QC1111", "10041995");
                    customerQC1111.returnItem("QCU1111", "QC1111", "10041995");
                    customerQC1111.purchaseItem("QCU1111", "QC1111", "10041995");
                    customerQC1111.returnItem("QCU1111", "QC1111", "10041995");
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }).start();
            new Thread(() -> {
                try {
                    customerBC2222.purchaseItem("BCU2222", "QC1111", "10041995");
                    customerBC2222.returnItem("BCU2222", "QC1111", "10041995");
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }).start();
        } else if(operationChoice.equals("3")) {
            Manager managerQC = getManager("QCM1111");
            Manager managerBC = getManager("BCM1111");
            managerQC.addItem("QCM1111", "QC1111", "fish", 5, 20);
            managerBC.addItem("BCM1111", "BC2222", "gold", 5, 100);
            Customer customerBC = getCustomer("BCU1111");
            Customer customerON = getCustomer("ONU1111");
            Customer customerQC = getCustomer("QCU1111");
            Customer customerQC2222 = getCustomer("QCU2222");
            Customer customerON2222 = getCustomer("ONU2222");
            new Thread(() -> {
                try {
                    customerQC.purchaseItem("QCU1111", "QC1111", "11041995");
                    customerQC.exchangeItem("QCU1111", "BC2222", "QC1111", "11041995");
                    customerQC.returnItem("QCU1111","BC2222", "11041995");
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }).start();
            new Thread(() -> {
                try {
                    customerON.purchaseItem("ONU1111", "QC1111", "11041995");
                    customerON.exchangeItem("ONU1111", "BC2222", "QC1111", "11041995");
                    customerON.returnItem("ONU1111","BC2222", "11041995");
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }).start();
            new Thread(() -> {
                try {
                    customerBC.purchaseItem("BCU1111", "QC1111", "11041995");
                    customerBC.exchangeItem("BCU1111", "BC2222", "QC1111", "11041995");
                    customerBC.returnItem("BCU1111","BC2222", "11041995");
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }).start();
            new Thread(() -> {
                try {
                    customerQC2222.purchaseItem("QCU2222", "QC1111", "11041995");
                    customerQC2222.exchangeItem("QCU2222", "BC2222", "QC1111", "11041995");
                    customerQC2222.returnItem("QCU2222","BC2222", "11041995");
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }).start();
            new Thread(() -> {
                try {
                    customerON2222.purchaseItem("ONU2222", "QC1111", "11041995");
                    customerON2222.exchangeItem("ONU2222", "BC2222", "QC1111", "11041995");
                    customerON2222.returnItem("ONU2222","BC2222", "11041995");
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }).start();
        } else if(operationChoice.equals("4")) {
            Manager managerQC = getManager("QCM1111");
            managerQC.addItem("QCM1111", "QC3333", "rice", 5, 20);
            managerQC.addItem("QCM1111", "QC4444", "milk", 4, 20);
            Customer customerBC3333 = getCustomer("BCU3333");
            Customer customerON3333 = getCustomer("ONU3333");
            Customer customerBC5555 = getCustomer("BCU5555");
            Customer customerON4444 = getCustomer("ONU4444");
            Customer customerBC4444 = getCustomer("BCU4444");

            List<Thread> threadPool = new ArrayList<>(Arrays.asList(
                    new Thread(() -> {
                        try {
                            customerBC3333.purchaseItem("BCU3333", "QC3333", "11041995");
                            customerBC3333.exchangeItem("BCU3333", "QC4444", "QC3333", "11041995");
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    }),
                    new Thread(() -> {
                        try {
                            customerON3333.purchaseItem("ONU3333", "QC3333", "11041995");
                            customerON3333.exchangeItem("ONU3333", "QC4444", "QC3333", "11041995");
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    }),
                    new Thread(() -> {
                        try {
                            customerBC5555.purchaseItem("BCU5555", "QC3333", "11041995");
                            customerBC5555.exchangeItem("BCU5555", "QC4444", "QC3333", "11041995");
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    }),
                    new Thread(() -> {
                        try {
                            customerON4444.purchaseItem("ONU4444", "QC3333", "11041995");
                            customerON4444.exchangeItem("ONU4444", "QC4444", "QC3333", "11041995");
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    }),
                    new Thread(() -> {
                        try {
                            customerBC4444.purchaseItem("BCU4444", "QC3333", "11041995");
                            customerBC4444.exchangeItem("BCU4444", "QC4444", "QC3333", "11041995");
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    })
            ));

            for(Thread th : threadPool) {
                th.start();
            }
        } else if(operationChoice.equals("5")) {
            Manager managerQC = getManager("QCM1111");
            managerQC.addItem("QCM1111", "QC3333", "rice", 5, 20);
            managerQC.addItem("QCM1111", "QC4444", "milk", 1, 20);
            Customer customerBC3333 = getCustomer("BCU3333");
            Customer customerON3333 = getCustomer("ONU3333");
            Customer customerBC5555 = getCustomer("BCU5555");
            Customer customerON4444 = getCustomer("ONU4444");
            Customer customerBC4444 = getCustomer("BCU4444");

            customerBC3333.purchaseItem("BCU3333", "QC3333", "11041995");
            customerON3333.purchaseItem("ONU3333", "QC3333", "11041995");
            customerBC5555.purchaseItem("BCU5555", "QC3333", "11041995");
            customerON4444.purchaseItem("ONU4444", "QC3333", "11041995");
            customerBC4444.purchaseItem("BCU4444", "QC3333", "11041995");

            List<Thread> threadPool = new ArrayList<>(Arrays.asList(
                    new Thread(() -> {
                        try {
                            customerBC3333.exchangeItem("BCU3333", "QC4444", "QC3333", "11041995");
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    }),
                    new Thread(() -> {
                        try {
                            customerON3333.exchangeItem("ONU3333", "QC4444", "QC3333", "11041995");
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    }),
                    new Thread(() -> {
                        try {
                            customerBC5555.exchangeItem("BCU5555", "QC4444", "QC3333", "11041995");
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    }),
                    new Thread(() -> {
                        try {
                            customerON4444.exchangeItem("ONU4444", "QC4444", "QC3333", "11041995");
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    }),
                    new Thread(() -> {
                        try {
                            customerBC4444.exchangeItem("BCU4444", "QC4444", "QC3333", "11041995");
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    })
            ));

            for(Thread th : threadPool) {
                th.start();
            }
        }
    }

    private static void runManagerClient() throws IOException, NumberFormatException, InvalidName, CannotProceed, org.omg.CosNaming.NamingContextPackage.InvalidName, NotFound {
        while(true) {
            Scanner myObj = new Scanner(System.in);
            System.out.println("Choose operation:");
            System.out.println("1.addItem");
            System.out.println("2.removeItem");
            System.out.println("3.listItemAvailability");
            System.out.println("4.Back to the client menu");
            String operationChoice = myObj.nextLine();
            System.out.println();
            if(operationChoice.equals("1")) {
                System.out.print("Operation (managerID, itemID, itemName, quantity, price): ");
                String[] args = myObj.nextLine().split(" ");
                if(!validateUserID(args[0]) || !validateItemID(args[1]) || args.length != 5) {
                    System.out.println("INVALID INPUT");
                    continue;
                }
                Manager manager = getManager(args[0]);
                manager.addItem(args[0], args[1], args[2], Integer.parseInt(args[3]), Integer.parseInt(args[4]));
            } else if(operationChoice.equals("2")) {
                System.out.print("Operation (managerID, itemID, quantity): ");
                String[] args = myObj.nextLine().split(" ");
                if(!validateUserID(args[0]) || !validateItemID(args[1]) || args.length != 3) {
                    System.out.println("INVALID INPUT");
                    continue;
                }
                Manager manager = getManager(args[0]);
                manager.removeItem(args[0], args[1], Integer.parseInt(args[2]));
            } else if(operationChoice.equals("3")) {
                System.out.print("Operation (managerID): ");
                String[] args = myObj.nextLine().split(" ");
                if(!validateUserID(args[0]) || args.length != 1) {
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

    private static void runCustomerClient() throws IOException, InvalidName, CannotProceed, org.omg.CosNaming.NamingContextPackage.InvalidName, NotFound {
        while(true) {
            Scanner myObj = new Scanner(System.in);
            System.out.println("Choose operation:");
            System.out.println("1.purchaseItem");
            System.out.println("2.findItem");
            System.out.println("3.returnItem");
            System.out.println("4.exchangeItem");
            System.out.println("5.Back to the client menu");
            String operationChoice = myObj.nextLine();
            System.out.println();
            if(operationChoice.equals("1")) {
                System.out.print("Operation (customerID, itemID, dateOfPurchase(ddmmyyyy): ");
                String[] args = myObj.nextLine().split(" ");
                if(!validateUserID(args[0]) || !validateItemID(args[1]) || args.length != 3) {
                    System.out.println("INVALID INPUT");
                    continue;
                }
                Customer customer = getCustomer(args[0]);
                customer.purchaseItem(args[0], args[1], args[2]);
            } else if(operationChoice.equals("2")) {
                System.out.print("Operation (customerID, itemName): ");
                String[] args = myObj.nextLine().split(" ");
                if(!validateUserID(args[0]) || args.length != 2) {
                    System.out.println("INVALID INPUT");
                    continue;
                }
                Customer customer = getCustomer(args[0]);
                customer.findItem(args[0], args[1]);
            } else if(operationChoice.equals("3")) {
                System.out.print("Operation (customerID, itemID, dateOfReturn(ddmmyyyy)): ");
                String[] args = myObj.nextLine().split(" ");
                if(!validateUserID(args[0]) || !validateItemID(args[1]) || args.length != 3) {
                    System.out.println("INVALID INPUT");
                    continue;
                }
                Customer customer = getCustomer(args[0]);
                customer.returnItem(args[0], args[1], args[2]);
            } else if(operationChoice.equals("4")) {
                System.out.print("Operation (customerID, newItemID, oldItemID, dateOfExchange(ddmmyyyy)): ");
                String[] args = myObj.nextLine().split(" ");
                if(!validateUserID(args[0]) || !validateItemID(args[1]) || !validateItemID(args[2]) || args.length != 4) {
                    System.out.println("INVALID INPUT");
                    continue;
                }
                Customer customer = getCustomer(args[0]);
                customer.exchangeItem(args[0], args[1], args[2], args[3]);
            }  else if(operationChoice.equals("5")) {
                break;
            }
            System.out.println();
        }
    }

    public static Customer getCustomer(String customerID) throws IOException {
        Customer customer = customers.get(customerID);
        if(customer == null) {
            customer = new Customer(customerID, customerID.substring(0, 2), arguments);
            customer.setupLogger();
            customers.put(customerID, customer);
        }
        return customer;
    }


    public static Manager getManager(String managerID) throws IOException {
        Manager manager = managers.get(managerID);
        if(manager == null) {
            manager = new Manager(managerID, managerID.substring(0, 2), arguments);
            manager.setupLogger();
            managers.put(managerID, manager);
        }
        return manager;
    }

    private static boolean validateUserID(String userID) {
        String regex = "\\d+";
        String location = userID.substring(0, 2);
        String numbers = userID.substring(3);
        if(userID.length() != 7 || !locations.contains(location) || !numbers.matches(regex)) {
            return false;
        }
        char role = userID.charAt(2);
        if(role != 'U' && role != 'M') {
            return false;
        }
        return true;
    }

    private static boolean validateItemID(String itemID) {
        String regex = "\\d+";
        String location = itemID.substring(0, 2);
        String numbers = itemID.substring(2);
        if(itemID.length() != 6 || !locations.contains(location) || !numbers.matches(regex)) {
            return false;
        }
        return true;
    }

    private static void prepopulate() throws IOException{
        List<String> managers = new ArrayList<>(Arrays.asList(
                "QCM1111", "QCM2222", "QCM3333",
                "BCM1111", "BCM2222", "BCM3333",
                "ONM1111", "ONM2222", "ONM3333")
        );
        List<String> customers = new ArrayList<>(Arrays.asList(
                "QCU1111", "QCU2222", "QCU3333",
                "BCU1111", "BCU2222", "BCU3333",
                "ONU1111", "ONU2222", "ONU3333")
        );

        String baseDir = "/Users/yaroslav/school/423/Distributed-Systems-Design/Supply_Management_System/logs/clients/";
        Path customerDir = Paths.get(baseDir + "customers");
        Path managerDir = Paths.get(baseDir + "managers");
        if(Files.isDirectory(customerDir) && !Files.list(customerDir).findAny().isPresent()) {
            for(String customerID : customers) { getCustomer(customerID); }
        }
        if(Files.isDirectory(managerDir) && !Files.list(managerDir).findAny().isPresent()) {
            for(String managerID : managers) { getManager(managerID); }
        }
    }

    private static boolean isDirEmpty(final Path directory) throws IOException {
        try(DirectoryStream<Path> dirStream = Files.newDirectoryStream(directory)) {
            return !dirStream.iterator().hasNext();
        }
    }
}
