package client;

import shared.RankingInterfaceUpdate;

import java.rmi.RemoteException;
import java.rmi.server.RemoteObject;
import java.util.List;

public class RankingImpl extends RemoteObject implements RankingInterfaceUpdate {

    private List<String> winners;

    public RankingImpl(List<String> winners) throws RemoteException {
        super();
        this.winners = winners;
    }

    @Override
    public void updateRanking(List<String> list) throws RemoteException {
        winners = list;
        for (String s : winners) {
            System.out.println(s);
        }
    }
}
