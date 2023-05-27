import java.io.IOException;
import java.nio.channels.SocketChannel;

public class Worker implements Runnable{
    private String stringa;
    private Memory memory;
    private SocketChannel client;
    private static int attempts = 0;   //  Tentativi effettuati
    private static final int MAX_ATTEMPTS = 3;   //  Numero massimo di tentativi

    public Worker(String stringa, Memory memory, SocketChannel client) {
        this.stringa = stringa;
        this.memory = memory;
        this.client = client;
    }

    @Override
    public void run() {
        System.out.println("Worker: " + stringa);
        String[] options = stringa.split(" ");
        switch (options[0]) {
                case "login":
                    handleLogin(options[1], options[2]);
                    break;
                case "playWordle":
                    playWordle();
                    break;
                case "sendWord":
                    handleSendWord(options[1]);
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
                    handleLogout(options[1]);
                    break;
        }
    }

    //  Metodo che gestisce il login
    private void handleLogin(String username, String password){
        if (memory.login(username, password)){
            try {
                memory.insertUserSocketChannel(username, client);
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
    }

    //  Metodo che gestisce il playWordle
    public void playWordle(){
        String word = WorkerWord.wordGuess;
        memory.stampa();
        String lastWordGuessed = memory.getOnlineUsers().get(memory.getUserSocketChannel().get(client)).getLastWord();
        String word2 = word;

        if (word.isEmpty()) {
            try {
                Utils.write("Errore nel sistema, ", client);
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        } else {
            //  da togliere perché si manda una notifica a tutti gli utenti
            if (!word2.equals(WorkerWord.wordGuess)) {
                try {
                    word = WorkerWord.wordGuess;
                    Utils.write("Inizio Partita ed è cambiata la parola ", client);
                } catch (IOException e) {
                    throw new RuntimeException(e);
                }
            }
            if (lastWordGuessed.equals(word)) {
                try {
                    Utils.write("Hai già giocato questa parola", client);
                } catch (IOException e) {
                    throw new RuntimeException(e);
                }
            } else {
                try {
                    Utils.write("INIZIO PARTITA", client);
                } catch (IOException e) {
                    throw new RuntimeException(e);
                }
            }
        }
    }

    //  Metodo che controlla la parola inserita dall'utente
    public void handleSendWord(String word){
        if (attempts >= MAX_ATTEMPTS) {
            try {
                attempts = 0;
                memory.getOnlineUsers().get(memory.getUserSocketChannel().get(client)).setLastWord(null);
                Utils.write("Hai superato i tentativi massimi", client);
                return;
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        }
        if (WorkerWord.wordGuess.length() != word.length()) {
            try {
                Utils.write("La parola deve essere lunga 10 caratteri", client);
                return;
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        }

        if (word.equals(WorkerWord.wordGuess)){
            try {
                attempts = 0;
                memory.getOnlineUsers().get(memory.getUserSocketChannel().get(client)).setLastWord(word);
                Utils.write("Congratulazioni! Hai indovinato la parola!", client);
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        } else {
            attempts++;
            StringBuilder response = getWordController(word);
            try {
                Utils.write("Parola non indovinata, " + response + ". Tentativi rimasti: " + (MAX_ATTEMPTS - attempts), client);
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        }
    }

    //  Metodo che costruisce la stringa di risposta
    public StringBuilder getWordController(String word){
        StringBuilder response = new StringBuilder();
        char[] secretChar = WorkerWord.wordGuess.toCharArray();
        char[] guessChar = word.toLowerCase().toCharArray();

        for (int i = 0; i < word.length(); i++) {
            if (guessChar[i] == secretChar[i]) {
                //  caso che l'utente ha indovinato la lettera nella posizione corretta
                response.append('+');
            } else if (WorkerWord.wordGuess.contains(String.valueOf(guessChar[i]))) {
                //  caso che l'utente ha indovinato la lettera ma nella posizione sbagliata
                response.append('?');
            } else{
                //  caso che l'utente non ha indovinato la lettera e non presente nella parola
                response.append('X');
            }
        }

        return response;
    }

    //  Metodo che gestisce il logout
    private void handleLogout(String username) {
        if (memory.logout(username)) {
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
        }
    }
}
