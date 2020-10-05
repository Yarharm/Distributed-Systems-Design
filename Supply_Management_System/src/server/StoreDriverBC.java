package server;

import communicate.ICommunicate;

import java.io.IOException;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import java.rmi.server.UnicastRemoteObject;
import java.util.HashMap;
import java.util.Map;

public class StoreDriverBC {
    public static void main(String[] args) {
        try {
            Map<String, Integer> portsConfig = new HashMap();
            portsConfig.put("QC", 8887);
            portsConfig.put("ON", 8888);
            portsConfig.put("BC", 8889);

            Registry registry = LocateRegistry.getRegistry();
            String locationName = "BC";
            StoreProxy storeProxy = new StoreProxy(locationName, portsConfig);
            ICommunicate stub = (ICommunicate) UnicastRemoteObject.exportObject(storeProxy, 0);
            registry.rebind(locationName, stub);
            storeProxy.initializeStore();
        }
        catch(IOException e) {
            System.err.println("Could not setup logger on a server");
            e.printStackTrace();
        } catch (Exception e) {
            System.err.println("Store Master exception:");
            e.printStackTrace();
        }
    }
}
