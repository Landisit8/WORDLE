package client.ranking;

import shared.ranking.RankingInterfaceUpdate;

import java.rmi.RemoteException;
import java.rmi.server.RemoteObject;
import java.util.Vector;

public class RankingImpl extends RemoteObject implements RankingInterfaceUpdate {
    private Vector<String> notifyMessages;

    public RankingImpl(Vector<String> notifyMessages) throws RemoteException {
        super();
        this.notifyMessages = notifyMessages;
    }

    @Override
    public void updateRanking(String notify) throws RemoteException {
        synchronized (notify){
            notifyMessages.add(notify);
        }
    }

    @Override
    public Vector<String> getNotify() throws RemoteException {
        return notifyMessages;
    }

}
