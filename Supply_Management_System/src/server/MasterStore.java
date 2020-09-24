package server;

import communicate.ICommunicate;

import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import java.rmi.server.UnicastRemoteObject;

public class MasterStore {
    public static void main(String[] args) {
        if (System.getSecurityManager() == null) {
            System.setSecurityManager(new SecurityManager());
        }
        try {
            String location = "QC";
            StoreProxy storeProxy = new StoreProxy(location);
            ICommunicate stub = (ICommunicate) UnicastRemoteObject.exportObject(storeProxy, 0);
            Registry registry = LocateRegistry.getRegistry();
            registry.rebind(location, stub);
            storeProxy.initializeStore();
            System.out.println("Master Store is active. Registration successful!");
        } catch (Exception e) {
            System.err.println("Store exception:");
            e.printStackTrace();
        }
    }
}
