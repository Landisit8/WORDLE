import java.io.IOException;
import java.nio.channels.SocketChannel;
public class Worker implements Runnable{
    private String stringa;
    private Memory memory;
    private SocketChannel client;

    private WorkerWord generator;

    public Worker(String stringa, Memory memory, SocketChannel client, WorkerWord workerWord) {
        this.stringa = stringa;
        this.memory = memory;
        this.client = client;
        this.generator = workerWord;
    }

    @Override
    public void run() {
        // metto tutto quello che thread deve fare
        System.out.println("Worker: " + stringa);
        String[] options = stringa.split(" ");
        switch (options[0]) {
                case "login":
                    //  login
                    if (memory.login(options[1], options[2])){
                        try {
                            Utils.write("Login effettuato con successo", client);
                        } catch (IOException e) {
                            throw new RuntimeException(e);
                        }
                    } else {
                        try {
                            Utils.write("Login fallito", client);
                        } catch (IOException e) {
                            throw new RuntimeException(e);
                        }
                    }
                    break;
                case "playWordle":
                    //  PlayWordle
                    break;
                case "sendWord":
                    //  sendWordù
                    //
                    break;
                case "sendMeStatistics":
                    //  sendMeStatistics
                    break;
                case "share":
                    //  share
                    break;
                case "showMeSharing":
                    //  showMeSharing
                    break;
                case "logout":
                    //  logout
                    if (memory.logout(options[1])){
                        try {
                            Utils.write("Uscita dal gioco effettuato con successo", client);
                        } catch (IOException e) {
                            throw new RuntimeException(e);
                        }
                    } else {
                        try {
                            Utils.write("Username errato", client);
                        } catch (IOException e) {
                            throw new RuntimeException(e);
                        }
                        //  Nel caso che logout restituisce false, mando la stringa vuota
                    }
                    break;
            }
    }
}
