package server;

import communicate.ICommunicate;
import communicate.ICommunicateHelper;
import org.omg.CORBA.ORB;
import org.omg.CosNaming.NameComponent;
import org.omg.CosNaming.NamingContextExt;
import org.omg.CosNaming.NamingContextExtHelper;
import org.omg.PortableServer.POA;
import org.omg.PortableServer.POAHelper;
import server.data.inventory.InventoryPool;
import server.data.sales.SalesManagerPool;

import java.util.HashMap;
import java.util.Map;

public class StoreDriverON {
    public static void main(String args[]) {
        Map<String, Integer> portsConfig = new HashMap();
        portsConfig.put("QC", 8887);
        portsConfig.put("ON", 8888);
        portsConfig.put("BC", 8889);
        try{
            // create and initialize the ORB
            ORB orb = ORB.init(args, null);

            // get reference to rootpoa and activate the POAManager
            POA rootpoa = POAHelper.narrow(orb.resolve_initial_references("RootPOA"));
            rootpoa.the_POAManager().activate();

            // create servant and register it with the ORB
            StoreProxy storeProxy = new StoreProxy("ON",
                    InventoryPool.getInventoryOnLocation("ON"),
                    SalesManagerPool.getSalesManagerOnLocation("ON"),
                    portsConfig);
            storeProxy.initializeStore(portsConfig.get("ON"));

            // get object reference from the servant
            org.omg.CORBA.Object ref = rootpoa.servant_to_reference(storeProxy);
            ICommunicate href = ICommunicateHelper.narrow(ref);

            // get the root naming context
            org.omg.CORBA.Object objRef =
                    orb.resolve_initial_references("NameService");
            // Use NamingContextExt which is part of the Interoperable
            // Naming Service (INS) specification.
            NamingContextExt ncRef = NamingContextExtHelper.narrow(objRef);

            // bind the Object Reference in Naming
            String name = "ON";
            NameComponent path[] = ncRef.to_name( name );
            ncRef.rebind(path, href);

            System.out.println("ON Store is ready and waiting ...");

            // wait for invocations from clients
            orb.run();
        }

        catch (Exception e) {
            System.err.println("ERROR: " + e);
            e.printStackTrace(System.out);
        }

        System.out.println("HelloServer Exiting ...");

    }
}
