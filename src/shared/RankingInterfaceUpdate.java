package shared;

import java.rmi.Remote;
import java.rmi.RemoteException;
import java.util.List;

public interface RankingInterfaceUpdate extends Remote {
    public void updateRanking(List<String> list) throws RemoteException;

}
