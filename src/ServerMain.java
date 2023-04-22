import java.io.IOException;
import java.net.*;
import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import java.rmi.server.UnicastRemoteObject;
import java.util.*;
import java.nio.channels.*;

// Classe serverMain -> Parsing, Iterazione con i client, creazione della nuova parola, gestione delle statistiche.
public class ServerMain {
    public static int DEFAULT_PORT = 5000;

    public static void main(String[] args) {
        int port;
        port = DEFAULT_PORT;

        //  RMI
        try {
            //  creazione di un'istanza dell'oggetto RegisterServiceImpl
            RegisterServiceImpl registerService = new RegisterServiceImpl();
            //  esportazione dell'oggetto
            RegisterInterface stub = (RegisterInterface) UnicastRemoteObject.exportObject(registerService, 0);
            //  creazione del registry sulla porta 1717
            LocateRegistry.createRegistry(1717);
            Registry r = LocateRegistry.getRegistry(1717);
            //  pubblicazione dello stub nel registry
            r.rebind("REGISTER-SERVICE", stub);
            System.out.println("Server ready");
        } catch (RemoteException e) {
            System.out.println("Tipologia errore: " + e.toString());
        }

        //  Creo il ServerSocketChannel e il selettore
        ServerSocketChannel serverChannel;
        Selector selector;

        String message = "Client ma che cazzo vuoi?";
        //  apro il serverSocketChannel
        try {
            serverChannel = ServerSocketChannel.open();
            ServerSocket ss = serverChannel.socket();
            //  mi collego alla porta
            InetSocketAddress address = new InetSocketAddress(port);
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
                        Utils.write(message, client);
                        //  cambio l'operazione da write a read
                        key.interestOps(SelectionKey.OP_READ);
                    } else if (key.isReadable() && key.isValid()) {    //  Quando la lettura è disponibile, vado a leggere
                        SocketChannel client = (SocketChannel) key.channel();
                        String stringa = Utils.read(client);
                        if (stringa.equals("")){
                            key.cancel();
                            System.err.println("Connessione chiusa");
                            continue;
                        }
                        System.out.println(stringa);
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
