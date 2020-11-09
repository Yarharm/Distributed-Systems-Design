package server;

import server.data.inventory.InventoryPool;
import server.data.sales.SalesManagerPool;

import javax.xml.ws.Endpoint;
import java.util.HashMap;
import java.util.Map;

public class StoreDriverON {
    public static void main(String[] args) {
        Map<String, Integer> portsConfig = new HashMap();
        portsConfig.put("QC", 8887);
        portsConfig.put("ON", 8888);
        portsConfig.put("BC", 8889);

        StoreProxy storeProxy = new StoreProxy("ON",
                InventoryPool.getInventoryOnLocation("ON"),
                SalesManagerPool.getSalesManagerOnLocation("ON"),
                portsConfig);
        try {
            storeProxy.initializeStore(portsConfig.get("ON"));
        } catch (Exception ignore){}

        Endpoint.publish("http://localhost:9999/ON", storeProxy);
    }
}
