package shared.ranking;

import java.rmi.Remote;
import java.rmi.RemoteException;

public interface RankingServerInterface extends Remote {
    void registerForCallback(RankingInterfaceUpdate client) throws RemoteException;

    void unregisterForCallback(RankingInterfaceUpdate client) throws RemoteException;
}
