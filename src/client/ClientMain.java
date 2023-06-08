package client;

import client.ranking.RankingImpl;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.reflect.TypeToken;
import shared.Utils;
import shared.ranking.RankingInterfaceUpdate;
import shared.ranking.RankingServerInterface;
import shared.rmi.RegisterInterface;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.lang.reflect.Type;
import java.net.InetSocketAddress;
import java.net.SocketAddress;
import java.nio.channels.SocketChannel;
import java.rmi.NotBoundException;
import java.rmi.Remote;
import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import java.rmi.server.UnicastRemoteObject;
import java.util.Scanner;
import java.util.Vector;
import java.util.concurrent.atomic.AtomicBoolean;

public class ClientMain {
    public static void main(String[] args) {
        //  variabili thread
        NotifyHandler notifyHandler;
        Thread thread = null;

        //  RMI Client
        RegisterInterface serverObject;
        Remote remoteObject;
        //  RMICALLBACK
        RankingInterfaceUpdate rankingInterfaceUpdate = null;
        RankingInterfaceUpdate stub = null;
        RankingServerInterface rankingServerInterface = null;

        // variabili per lo share
        Vector<String> games = new Vector<>();

        //  variabili per i menu
        boolean exit = false;
        boolean login = false;
        boolean logout = false;
        Scanner scanner = new Scanner(System.in);
        int menu;
        String stringa;

        //  variabili configurazione
        Configuration configuration = new Configuration();
        Gson gson = new GsonBuilder().setPrettyPrinting().create();
        String configurationGson = gson.toJson(configuration);
        String fileName = "config.json";
        String absolutePath = configuration.setFileSeparator(fileName);
        File file = new File(absolutePath);
        try {
            if (file.createNewFile()) {
                System.out.println("File creato");
            } else {
                System.out.println("File già esistente");
            }
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        try(FileWriter fileWriter = new FileWriter(file)){
            fileWriter.write(configurationGson);
        }catch (IOException e){
            System.out.println("Errore di scrittura");
        }
        //System.out.println("Serializzazione" + configurationGson);
        configuration = gson.fromJson(configurationGson, Configuration.class);
        //System.out.println("Deserializzazione" + configuration);
        SocketChannel client = null;
        SocketAddress address = new InetSocketAddress(configuration.getHostname(), configuration.getDefaultPort());

        //  variabili utili
        Vector<String> winners = new Vector<>();
        Vector<String> previousWinners = new Vector<>();
        AtomicBoolean print = new AtomicBoolean(true);
        Registry registry;
        try {
            registry = LocateRegistry.getRegistry(configuration.getRegistryPort());
        } catch (RemoteException e) {
            throw new RuntimeException(e);
        }

        try {
            client = SocketChannel.open(address);

        //  menu
            do{
                do {
                    print.set(false);

                    //  controllo che l'ingresso sia un intero
                    while (true) {
                        System.out.println("Benvenuto nel menu di login");
                        System.out.println("0. Registrazione");
                        System.out.println("1. Login");
                        System.out.println("2. Esci");

                        if (scanner.hasNextInt()) {
                            menu = scanner.nextInt();
                            break;
                        } else {
                            System.err.println("Valore non valido, riprova");
                            scanner.nextLine();
                        }
                    }
                    switch (menu) {
                        case 0:
                            //  registrazione
                            System.out.println("Inserisci username");
                            String username = scanner.next();
                            System.out.println("Inserisci password");
                            String password = scanner.next();
                            try {
                                remoteObject = registry.lookup("REGISTER-SERVICE");
                                serverObject = (RegisterInterface) remoteObject;
                                switch (serverObject.register(username, password)){
                                    case 0:
                                        System.out.println("Codice 001, Registrazione avvenuta con successo");
                                        break;
                                    case 1:
                                        System.out.println("Codice 002, Utente già registrato");
                                        break;
                                    case 2:
                                        System.out.println("Codice 003, Username o password non inseriti");
                                        break;
                                }
                            } catch (RemoteException | NotBoundException e) {
                                System.out.println("Errore di connessione al server" + e.getMessage());
                                e.printStackTrace();
                            }
                            break;
                        case 1:
                            //  Login
                            try {
                                System.out.println("Inserisci username");
                                String username1 = scanner.next();
                                System.out.println("Inserisci password");
                                String password1 = scanner.next();
                                Utils.write("login " + username1 + " " + password1, client);
                            } catch (IOException e) {
                                throw new RuntimeException(e);
                            }
                            try {
                                //  lettura
                                stringa = Utils.read(client);
                                if (stringa.contains("011") || stringa.contains("012") || stringa.contains("013")) {
                                    System.out.println(stringa);
                                } else {
                                    System.out.println(stringa + " Benvenuto");
                                    notifyHandler = new NotifyHandler(configuration.getUDP_PORT(),configuration.getMulticastAddress() ,games,print);
                                    thread = new Thread(notifyHandler);
                                    thread.start();
                                    rankingServerInterface = (RankingServerInterface) registry.lookup("RANKING-SERVICE");
                                    rankingInterfaceUpdate = new RankingImpl(winners);
                                    stub = (RankingInterfaceUpdate) UnicastRemoteObject.exportObject(rankingInterfaceUpdate, 0);
                                    if (rankingServerInterface != null) {
                                        rankingServerInterface.registerForCallback(stub);
                                    }
                                    login = true;
                                }
                            } catch (Exception e) {
                                e.printStackTrace();
                            }
                            break;
                        case 2:
                            //  exit
                            System.out.println("Chiusura...");
                            exit = true;
                            break;
                        default:
                            System.out.println("Scelta non valida");
                            break;
                    }
                } while (!exit && !login);

                if (login){
                    do{
                        while (true){
                            System.out.println("WORDLE GAME");
                            System.out.println("0. Playwordle");
                            System.out.println("1. Send Word");
                            System.out.println("2. send me statistics");
                            System.out.println("3. Share");
                            System.out.println("4. showMeSharing");
                            System.out.println("5. showMeRanking");
                            System.out.println("6: Logout");

                            print.set(false);
                            //  controllo che l'ingresso sia un intero
                            if (scanner.hasNextInt()) {
                                menu = scanner.nextInt();
                                break;
                            } else {
                                System.err.println("Valore non valido, riprova");
                                scanner.nextLine();
                            }
                        }

                        print.set(true);
                        switch (menu) {
                            case 0:
                                //  Playwordle
                                try {
                                    Utils.write("playWordle", client);
                                    //  lettura
                                    stringa = Utils.read(client);
                                    System.out.println(stringa);
                                } catch (Exception e) {
                                    e.printStackTrace();
                                }
                                break;
                            case 1:
                                //  SendWord
                                try {
                                    print.set(false);
                                    System.out.println("Inserisci parola");
                                    String parola = scanner.next();
                                    Utils.write("sendWord " + parola, client);
                                    //  lettura
                                    stringa = Utils.read(client);
                                    System.out.println(stringa);
                                } catch (Exception e) {
                                    e.printStackTrace();
                                }
                                print.set(true);
                                break;
                            case 2:
                                // sendMeStatistics
                                try {
                                    Utils.write("sendMeStatistics", client);
                                    //  lettura
                                    stringa = Utils.read(client);
                                    String output = stringa.replace(";", "\n");
                                    System.out.println(output);
                                } catch (Exception e) {
                                    e.printStackTrace();
                                }
                                break;
                            case 3:
                                // Share
                                Utils.write("share", client);
                                //  lettura
                                stringa = Utils.read(client);
                                System.out.println(stringa);
                                break;
                            case 4:
                                //  showMeSharing
                                    if (games.isEmpty()){
                                        System.out.println("Codice 071, Nessun giocatore ha condiviso la partita");
                                    } else {
                                        System.out.println("Codice 070, Giocatori che hanno condiviso la partita:");
                                        for (String game : games)   System.out.println(game);
                                    }
                                break;
                            case 5:
                                //  showMeRanking
                                try {
                                    Utils.write("showMeRanking", client);
                                    //  lettura
                                    stringa = Utils.read(client);
                                    //  ascolto la notifica
                                    synchronized (winners) {
                                        winners = rankingInterfaceUpdate.getNotify();
                                    }
                                    if (!previousWinners.equals(winners)){
                                        for(int i = 0; i < winners.size(); i++){
                                            System.out.println(i+1 + ") " + winners.get(i));
                                        }
                                    }
                                    previousWinners = winners;

                                    // deserializzo la stringa
                                    Type type = new TypeToken<Vector<String>>(){}.getType();
                                    Vector<String> ranking = null;
                                    ranking = gson.fromJson(stringa, type);
                                    if (ranking == null){
                                        System.out.println("Codice 061, non è stata giocata nessuna partita");
                                        break;
                                    } else {
                                        //  stampo la classifica
                                        System.out.println("Classifica:");
                                        for (int i = 0; i < ranking.size(); i++)
                                            System.out.println(i + 1 + "° classificato: " + ranking.get(i));

                                    }
                                } catch (Exception e) {
                                    e.printStackTrace();
                                }
                                break;
                            case 6:
                                //  logout
                                try {
                                    //  scrittura
                                    System.out.println("Inserisci username");
                                    String username1 = scanner.next();
                                    Utils.write("logout " + username1, client);
                                    //  lettura
                                    stringa = Utils.read(client);
                                    if (stringa.contains("021") || stringa.contains("022")){
                                        System.out.println(stringa);
                                    } else {
                                        System.out.println(stringa);
                                        logout = true;
                                        login = false;
                                        if (rankingServerInterface != null) {
                                            rankingServerInterface.unregisterForCallback(stub);
                                        }
                                    }
                                } catch (Exception e) {
                                    e.printStackTrace();
                                }
                                break;
                            case 601487:
                                //  easter egg
                                System.out.println("Codice 104, Easter egg attivato");
                                System.out.printf("Questo è il progetto di reti di %-5s %-5s%n", "Federico", "Landini");
                                break;
                            default:
                                System.out.println("Scelta non valida");
                                break;
                        }
                        print.set(false);
                    }while(!logout);
                }
            } while(!exit);
        } catch (IOException e) {
            System.err.println("Impossibile connettersi al server: " + e.getMessage());
        }
        
        //  chiusura NotifyHandler
        if (thread != null) {
            thread.interrupt();
        }

        try {
            if (client != null) {
                client.close();
            }
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        System.exit(0);
    }
}