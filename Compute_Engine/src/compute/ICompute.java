package compute;

import java.rmi.Remote;
import java.rmi.RemoteException;

/*
    Remote Interface for Client - Server communication.
    Can be invoked by other JVM
    RemoteException is checked: Catch or Throws
 */
public idl_interface ICompute extends Remote {
    <T> T executeTask(ITask<T> t) throws RemoteException; // Comm failure or protocol error
}
