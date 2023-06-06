package shared.ranking;

import java.rmi.Remote;
import java.rmi.RemoteException;

public interface RankingServerInterface extends Remote {
    public void registerForCallback(RankingInterfaceUpdate client) throws RemoteException;

    public void unregisterForCallback(RankingInterfaceUpdate client) throws RemoteException;
}
