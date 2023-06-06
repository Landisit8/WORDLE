package client.ranking;

import shared.ranking.RankingInterfaceUpdate;

import java.rmi.RemoteException;
import java.rmi.server.RemoteObject;
import java.util.Vector;

public class RankingImpl extends RemoteObject implements RankingInterfaceUpdate {

    private Vector<String> winners;

    public RankingImpl(Vector<String> winners) throws RemoteException {
        super();
        this.winners = winners;
    }

    @Override
    public void updateRanking(Vector<String> list) throws RemoteException {
        winners = list;
    }

    public Vector<String> getWinners() {
        return winners;
    }
}
