package server.rmi;

import shared.ranking.RankingInterfaceUpdate;
import shared.ranking.RankingServerInterface;

import java.rmi.RemoteException;
import java.rmi.server.RemoteObject;
import java.util.ArrayList;
import java.util.List;

public class RankingServerImpl extends RemoteObject implements RankingServerInterface {
    private List<RankingInterfaceUpdate> clients;

    public RankingServerImpl() {
        super();
        this.clients = new ArrayList<>();
    }

    public synchronized void registerForCallback(RankingInterfaceUpdate client) throws RemoteException {
        if (client == null) {
            System.out.println("Client is null");
            return;
        }
        if (!(clients.contains(client))) {
            clients.add(client);
            System.out.println("Registered new client ");
        }
    }

    public synchronized void unregisterForCallback(RankingInterfaceUpdate client) throws RemoteException {
        if (client == null) {
            System.out.println("Client is null");
            return;
        }
        if (clients.remove(client)) {
            System.out.println("Unregistered client ");
        } else {
            System.out.println("unregister: client wasn't registered.");
        }
    }

    public synchronized void updateRanking(String notify) throws RemoteException {
        for (RankingInterfaceUpdate client : clients) {
            client.updateRanking(notify);
        }
    }
}
