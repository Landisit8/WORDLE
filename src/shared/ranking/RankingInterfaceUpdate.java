package shared.ranking;

import java.rmi.Remote;
import java.rmi.RemoteException;
import java.util.Vector;

public interface RankingInterfaceUpdate extends Remote {
    void updateRanking(String notify) throws RemoteException;

    Vector<String> getNotify() throws RemoteException;
}
