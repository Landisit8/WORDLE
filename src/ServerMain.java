import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import java.io.IOException;
import java.net.InetSocketAddress;
import java.net.ServerSocket;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.nio.channels.ServerSocketChannel;
import java.nio.channels.SocketChannel;
import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import java.rmi.server.UnicastRemoteObject;
import java.util.Iterator;
import java.util.Set;

// Classe serverMain -> Parsing, Iterazione con i client, creazione della nuova parola, gestione delle statistiche.
public class ServerMain {
    public static void main(String[] args) {
        //  Parsing della configurazione
        Configuration configuration = new Configuration();
        Gson gson = new GsonBuilder().setPrettyPrinting().create();
        String configurationGson = gson.toJson(configuration);
        //System.out.println("Serializzazione" + configurationGson);
        configuration = gson.fromJson(configurationGson, Configuration.class);
        // System.out.println("Deserializzazione" + configuration);
        String[] options;
        Memory memory = new Memory();
        String messageForClient = "";
        //  RMI
        try {
            //  creazione di un'istanza dell'oggetto RegisterServiceImpl
            RegisterServiceImpl registerService = new RegisterServiceImpl(memory);
            //  esportazione dell'oggetto
            RegisterInterface stub = (RegisterInterface) UnicastRemoteObject.exportObject(registerService, 0);
            //  creazione del registry sulla porta 1717
            LocateRegistry.createRegistry(configuration.getRegistryPort());
            Registry r = LocateRegistry.getRegistry(configuration.getRegistryPort());
            //  pubblicazione dello stub nel registry
            r.rebind("REGISTER-SERVICE", stub);
            System.out.println("Server ready");
        } catch (RemoteException e) {
            System.out.println("Tipologia errore: " + e);
        }

        //  Creo il ServerSocketChannel e il selettore
        ServerSocketChannel serverChannel;
        Selector selector;

        //  apro il serverSocketChannel
        try {
            serverChannel = ServerSocketChannel.open();
            ServerSocket ss = serverChannel.socket();
            //  mi collego alla porta
            InetSocketAddress address = new InetSocketAddress(configuration.getDefaultPort());
            //  lo collego all'indirizzo, e ho attivato un servizio su quell'indirizzo e su quella porta
            ss.bind(address);
            serverChannel.configureBlocking(false);
            //  registro il canale con un selettore, dopo averlo aperto e con la sk di accept
            selector = Selector.open();
            serverChannel.register(selector, SelectionKey.OP_ACCEPT);
        } catch (IOException e) {
            e.printStackTrace();
            return;
        }
        while (true) {
            try {
                //  bloccante finchè non arriva una richiesta di connessione
                selector.select();
            } catch (IOException e) {
                e.printStackTrace();
                break;
            }
            //  restituisce la selectionKey, che è un insieme di chiavi
            Set<SelectionKey> readyKeys = selector.selectedKeys();
            Iterator<SelectionKey> iterator = readyKeys.iterator();
            while (iterator.hasNext()) {  //  intero sulle chiavi
                SelectionKey key = iterator.next();
                //  rimuovo la chiave, sennò rimane tra le chiavi selezionate ed ha un comportamento incrementale
                iterator.remove();
                //  rimuove la chiave dal Select Set, ma non dal Registered Set
                try {
                    if (key.isAcceptable() && key.isValid()) {    //  è stata accettata la connessione
                        ServerSocketChannel server = (ServerSocketChannel) key.channel();
                        //  l'accept mi resitutisce il canale su cui comunico con quel client
                        SocketChannel client = server.accept();
                        System.out.println("Connessione accettata da " + client);
                        client.configureBlocking(false);
                        //   registro il socket che mi collega a quel settore con l'operazioni di write
                        client.register(selector, SelectionKey.OP_READ);
                    } else if (key.isWritable() && key.isValid()) {    //  Quando la scrittura è disponibile, vado a scrivere
                        SocketChannel client = (SocketChannel) key.channel();
                        Utils.write(messageForClient, client);
                        //  cambio l'operazione da write a read
                        key.interestOps(SelectionKey.OP_READ);
                    } else if (key.isReadable() && key.isValid()) {    //  Quando la lettura è disponibile, vado a leggere
                        SocketChannel client = (SocketChannel) key.channel();
                        String stringa = Utils.read(client);
                        options = stringa.split(" ");
                        if (options[0].isEmpty()){
                            key.cancel();
                            System.err.println("Connessione chiusa");
                            continue;
                        } else {
                            switch (options[0]) {
                                case "login":
                                    //  login
                                    if (memory.login(options[1], options[2])){
                                        messageForClient = "Login effettuato con successo";
                                    } else {
                                        messageForClient = "Login fallito";
                                    }
                                    break;
                                case "exit":
                                    //  Exit
                                    if (memory.logout(options[1])){
                                        messageForClient = "Uscita dal gioco effettuato con successo";
                                    } else {
                                        //  Nel caso che logout restituisce false, mando la stringa vuota
                                        messageForClient = "";
                                    }
                                    break;
                                case "3":
                                    //  PlayWordle
                                    break;
                                case "4":
                                    //  sendWord
                                    break;
                                case "5":
                                    //  sendMeStatistics
                                    break;
                                case "6":
                                    //  share
                                    break;
                                case "7":
                                    //  showMeSharing
                                    break;
                                case "8":
                                    //  logout
                                    break;
                            }
                        }
                        //  cambio l'operazione da read a write
                        key.interestOps(SelectionKey.OP_WRITE);
                    }
                } catch (IOException e) {
                    key.cancel();
                    try {
                        key.channel().close();
                    } catch (IOException e1) {
                        e1.printStackTrace();
                    }
                }
            }
        }
    }
}
