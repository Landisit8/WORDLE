import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import java.io.*;
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
import java.util.Random;
import java.util.Set;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

// Classe serverMain -> Parsing, Iterazione con i client, creazione della nuova parola, gestione delle statistiche.
public class ServerMain {

    public static void main(String[] args) {
        //  ThreadPool
        ThreadPoolExecutor threadPoolExecutor = new ThreadPoolExecutor(10, 10,
                10, TimeUnit.SECONDS, new LinkedBlockingQueue<>());
        //  variabili
        Memory memory = new Memory();
        Long startTime = System.nanoTime();
        Configuration configuration = new Configuration();
        Gson gson = new GsonBuilder().setPrettyPrinting().create();
        Random random = new Random();

        //  Quando hai capito dove gestire la parte del tempo, metti i 2 parsing e poi decidi come trasportare la word
        //  cosi avrai sia il salvataggio e il recupero della parola.
        //  decidere se creare una variabile che comprende il numero passimo in un file
        //  Parsing del dizionario
        try {
            FileReader fileReader = new FileReader(".\\src\\words.txt");
            BufferedReader bufferedReader = new BufferedReader(fileReader);
            int index = random.nextInt(30824);
            System.out.println("Indice: " + index);
            String word = null;
            for (int i = 0; i < index; i++) {
                bufferedReader.readLine();
                if (i == index - 1) {
                    word = bufferedReader.readLine();
                }
            }
            System.out.println("La parola da indovinare è: " + word);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

        //  Parsing del backup
        String backupGson = gson.toJson(memory);
        File memoryFile = new File(".\\src\\backup.json");    // su mac non funziona, guardare i file separator
        try {
            if (memoryFile.createNewFile()) {
                System.out.println("File backup creato");
            } else {
                System.out.println("File backup già esistente");
            }
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        // Deserializzazione del backup
        memory = gson.fromJson(backupGson, Memory.class);

        //  Parsing della configurazione
        File file = new File(".\\src\\config.json");    // su mac non funziona, guardare i file separator
        String configurationGson = gson.toJson(configuration);
        try {
            if (file.createNewFile()) {
                System.out.println("File di configurazione creato");
            } else {
                System.out.println("File di configurazione già esistente");
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
        // System.out.println("Deserializatione" + configuration);

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
                //  bloccante affinché non arriva una richiesta di connessione
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
                        //  accept mi restituisce il canale su cui comunico con quel client
                        SocketChannel client = server.accept();
                        System.out.println("Connessione accettata da " + client);
                        client.configureBlocking(false);
                        //   registro il socket che mi collega a quel settore con l'operazioni di write
                        client.register(selector, SelectionKey.OP_READ);
                    } else if (key.isReadable() && key.isValid()) {    //  Quando la lettura è disponibile, vado a leggere
                        SocketChannel client = (SocketChannel) key.channel();
                        String stringa = Utils.read(client);
                        if (stringa.isEmpty()){
                            key.cancel();
                            System.err.println("Connessione chiusa");
                        } else {
                            threadPoolExecutor.execute(new Worker(stringa, memory, client));
                        }
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
        // chiudo il thread pool
        threadPoolExecutor.shutdown();
        try{
            if (threadPoolExecutor.awaitTermination(1, TimeUnit.MINUTES)){
                System.out.println("Tutti i thread sono terminati");
            } else {
                System.out.println("Timeout scaduto");
            }
        } catch (InterruptedException e) {
            e.printStackTrace();
        } finally {
            threadPoolExecutor.shutdownNow();
        }
    }
}

/*
//  salvo la memoria ogni minuto
                    Long endTime = System.nanoTime();
                    if (((endTime - startTime)/1000000) >= 60000) {
                        System.out.println("E' passato 1 minuto");
                        try(FileWriter fileWriter = new FileWriter(memoryFile)){
                            fileWriter.write(backupGson);
                            System.out.println("Backup salvato");
                        }catch (IOException e){
                            System.out.println("Errore di scrittura");
                        }
                    }
 */
