package client;

import shared.RankingInterfaceUpdate;

import java.rmi.RemoteException;
import java.rmi.server.RemoteObject;
import java.util.List;
import java.util.Vector;

public class RankingImpl extends RemoteObject implements RankingInterfaceUpdate {

    private List<String> winners;

    public RankingImpl(List<String> winners) throws RemoteException {
        super();
        this.winners = winners;
    }

    @Override
    public void updateRanking(Vector<String> list) throws RemoteException {
        winners = list;
        for (String s : winners) {
            System.out.println(s);
        }
    }
}
