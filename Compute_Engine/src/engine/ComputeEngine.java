package engine;

import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import java.rmi.server.UnicastRemoteObject;
import compute.ICompute;
import compute.ITask;

/*
    *** SERVER ***
    Only methods defined in the remote interface ICompute are visible to the Client classes.
    We can add as much functionality as we want but the Client will only see `executeTask`.
    Bind, Rebind, Unbind can be performed only within the same host.
 */
public class ComputeEngine implements ICompute{
    public ComputeEngine() {
        super();
    }

    public <T> T executeTask(ITask<T> task) {
        return task.execute();
    }

    public static void main(String[] args) {
        if (System.getSecurityManager() == null) {
            System.setSecurityManager(new SecurityManager());
        }
        try {
            String name = "Compute";
            ICompute engine = new ComputeEngine();
            ICompute stub = (ICompute) UnicastRemoteObject.exportObject(engine, 0);
            Registry registry = LocateRegistry.getRegistry();
            registry.rebind(name, stub);
            System.out.println("ComputeEngine bound");
        } catch (Exception e) {
            System.err.println("ComputeEngine exception:");
            e.printStackTrace();
        }
    }
}
