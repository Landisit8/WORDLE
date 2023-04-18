import java.io.IOException;
import java.net.*;
import java.util.*;
import java.nio.*;
import java.nio.channels.*;

// Classe serverMain -> Parsing, Iterazione con i client, creazione della nuova parola, gestione delle statistiche.
public class ServerMain {
    public static int DEFAULT_PORT = 5000;
    public static void main(String[] args) {
        int port;
        port = DEFAULT_PORT;
        //  Creo il ServerSocketChannel e il selettore
        ServerSocketChannel serverChannel;
        Selector selector;
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
                    if (key.isAcceptable()) {    //  è stata accettata la connessione
                        ServerSocketChannel server = (ServerSocketChannel) key.channel();
                        //  l'accept mi resitutisce il canale su cui comunico con quel client
                        SocketChannel client = server.accept();
                        System.out.println("Connessione accettata da " + client);
                        client.configureBlocking(false);
                        //   registro il socket che mi collega a quel settore con l'operazioni di write
                        SelectionKey clientKey = client.register(selector, SelectionKey.OP_WRITE);
                        //  creo un buffer per il client
                        ByteBuffer output = ByteBuffer.allocate(4);
                        output.putInt(0);    //  ci scrivo
                        output.flip();   //  modalità lettura
                        //  Per ricordare il punto in cui ero rimasto nella sequenza. L'attachment ha lo stato del canale
                        clientKey.attach(output);
                    } else if (key.isWritable()) {    //  Quando la scrittura è disponibile, vado a scrivere
                        SocketChannel client = (SocketChannel) key.channel();
                        ByteBuffer output = (ByteBuffer) key.attachment();
                        if (!output.hasRemaining()) {
                            output.rewind(); // faccio la clear resettando il buffer
                            int value = output.getInt();
                            output.clear();
                            output.putInt(value + 1);   //  ci metto n+1
                            output.flip();   //  modalità scrittura
                        }
                        client.write(output);
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











       /* Scanner scanner = new Scanner(System.in);   //  Scanner
        Register register;  //  Registrazione
        Login login;    //  Login
        boolean inputOk = false;    //  Controllo input
        String input;
        String stringa;

        int Inserimento;

        do{
            Inserimento= scanner.nextInt();
        switch (Inserimento){
            case 0:
                System.out.println("Registrazione, Inserisci l'username");
                stringa = scanner.next();
                System.out.println("Inserisci la password");
                input = scanner.next();
                register = new Register(stringa, input);
                break;
            case 1:
                System.out.println("Login");
                System.out.println("Inserisci l'username");
                stringa = scanner.next();
                System.out.println("Inserisci la password");
                input = scanner.next();
                login = new Login(stringa, input);
                login.controllo(stringa, input);
                inputOk = true;
                break;
        }
        }while(inputOk!=true);
        */
}
