package server;

import server.data.inventory.InventoryPool;
import server.data.sales.SalesManagerPool;

import javax.xml.ws.Endpoint;
import java.util.HashMap;
import java.util.Map;

public class StoreDriverQC {
    public static void main(String[] args) {
        Map<String, Integer> portsConfig = new HashMap();
        portsConfig.put("QC", 8887);
        portsConfig.put("ON", 8888);
        portsConfig.put("BC", 8889);

        StoreProxy storeProxy = new StoreProxy("QC",
                InventoryPool.getInventoryOnLocation("QC"),
                SalesManagerPool.getSalesManagerOnLocation("QC"),
                portsConfig);
        try {
            storeProxy.initializeStore(portsConfig.get("QC"));
        } catch (Exception ignore){}

        Endpoint.publish("http://localhost:8887/QC", storeProxy);
    }
}
