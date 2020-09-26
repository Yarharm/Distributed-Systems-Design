package server;

import communicate.ICommunicate;

import java.io.IOException;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import java.rmi.server.UnicastRemoteObject;
import java.util.HashMap;
import java.util.Map;

public class StoreMaster {
    public static void main(String[] args) {
        if (System.getSecurityManager() == null) {
            System.setSecurityManager(new SecurityManager());
        }
        try {
            Map<String, Integer> storesInfo = new HashMap();
            storesInfo.put("QC", 8887);
            storesInfo.put("ON", 8888);
            storesInfo.put("BC", 8889);

            Registry registry = LocateRegistry.getRegistry();
            for(Map.Entry<String, Integer> store : storesInfo.entrySet()) {
                String locationName = store.getKey();
                int port = store.getValue();
                StoreProxy storeProxy = new StoreProxy(locationName);
                ICommunicate stub = (ICommunicate) UnicastRemoteObject.exportObject(storeProxy, 0);
                registry.rebind(locationName, stub);
                storeProxy.setupLogger();
                storeProxy.initializeStore(port);
            }
        } catch(IOException e) {
            System.err.println("Could not setup logger on a server");
            e.printStackTrace();
        } catch (Exception e) {
            System.err.println("Store Master exception:");
            e.printStackTrace();
        }
    }
}
