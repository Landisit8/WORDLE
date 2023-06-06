package shared.ranking;

import java.rmi.Remote;
import java.rmi.RemoteException;
import java.util.Vector;

public interface RankingInterfaceUpdate extends Remote {
    void updateRanking(Vector<String> list) throws RemoteException;

    Vector<String> getWinners() throws RemoteException;
}
