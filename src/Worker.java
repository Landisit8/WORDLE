import com.google.gson.Gson;
import com.google.gson.JsonObject;

import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;
import java.nio.channels.SocketChannel;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;

public class Worker implements Runnable{
    private String stringa;
    private Memory memory;
    private SocketChannel client;
    private Gson gson;
    private static int attempts = 0;   //  Tentativi effettuati
    private static final int MAX_ATTEMPTS = 3;   //  Numero massimo di tentativi
    private ArrayList<String> dictionary;

    public Worker(String stringa, Memory memory, SocketChannel client, Gson gson) {
        this.stringa = stringa;
        this.memory = memory;
        this.client = client;
        this.gson = gson;
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
                    memory.getUsers().get(memory.getUserSocketChannel().get(client)).setFlag(true);
                    break;
                case "sendWord":
                    if (memory.getUsers().get(memory.getUserSocketChannel().get(client)).getFlag())
                        handleSendWord(options[1]);
                    else {
                        try {
                            Utils.write("Codice 101, Esegui prima playwordle", client);
                        } catch (IOException e) {
                            throw new RuntimeException(e);
                        }
                    }
                    break;
                case "sendMeStatistics":
                    handleStats();
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
        int tmp = memory.login(username, password);
        //  fare uno switch case
        if (tmp == 0) {
            try {
                Utils.write("Codice 010, Login effettuato", client);
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        } else if (tmp == 1) {
            try {
                Utils.write("Codice 011, Utente già online", client);
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        } else if (tmp == 2) {
            try {
                Utils.write("Codice 012, Password errata", client);
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        } else if (tmp == 3) {
            try {
                Utils.write("Codice 013, Utente non registrato", client);
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        }
    }

    //  Metodo che gestisce il playWordle
    public void playWordle(){
        String word = WorkerWord.wordGuess;
        String lastWordGuessed = memory.getUsers().get(memory.getUserSocketChannel().get(client)).getLastWord();
        String word2 = word;

        if (word.isEmpty()) {
            try {
                Utils.write("Codice 032, Non è stata generata la parola da indovinare", client);
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        } else {
            //  da togliere perché si manda una notifica a tutti gli utenti
            if (!word2.equals(WorkerWord.wordGuess)) {
                try {
                    word = WorkerWord.wordGuess;
                    Utils.write("Codice 033, Inizio Partita ed è cambiata la parola ", client);
                } catch (IOException e) {
                    throw new RuntimeException(e);
                }
            }
            if (lastWordGuessed.equals(word)) {
                try {
                    Utils.write("Codice 031, Hai già giocato questa parola", client);
                } catch (IOException e) {
                    throw new RuntimeException(e);
                }
            } else {
                try {
                    Utils.write("Codice 030, INIZIO PARTITA", client);
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
                if (memory.isOnline(memory.getUsers().get(memory.getUserSocketChannel().get(client)).getUsername())) {

                    memory.getUsers().get(memory.getUserSocketChannel().get(client)).setLastWord("default");
                    memory.getUsers().get(memory.getUserSocketChannel().get(client)).incrementNumGame();

                    //  setto la striscia di vittorie
                    memory.getUsers().get(memory.getUserSocketChannel().get(client)).setStreakWin(0);

                    //  Calcolo della media dei tentativi, tentativi rimasti + tentativi effettuati / numero di partite
                    memory.getUsers().get(memory.getUserSocketChannel().get(client))
                            .setAvgAttempt(((attempts + 1) + memory.getUsers()
                                    .get(memory.getUserSocketChannel().get(client)).getAvgAttempt())
                                    / memory.getUsers().get(memory.getUserSocketChannel().get(client)).getNumGame());

                    //  Calcolo della percentuale di vittorie, numero di vittorie / numero di partite * 100
                    memory.getUsers().get(memory.getUserSocketChannel().get(client)).setPercentWin((memory.getUsers()
                            .get(memory.getUserSocketChannel().get(client)).getNumWin()) / memory.getUsers()
                            .get(memory.getUserSocketChannel().get(client)).getNumGame() * 100);

                    //  Calcolo del punteggio, numero di vittorie * media dei tentativi
                    memory.getUsers().get(memory.getUserSocketChannel().get(client))
                            .setValueClassified(memory.getUsers().get(memory.getUserSocketChannel().get(client)).getNumWin()
                                    * memory.getUsers().get(memory.getUserSocketChannel().get(client)).getAvgAttempt());

                    //  traduzione della parola
                    String traslation = getItalianTranslation(WorkerWord.wordGuess);

                    attempts = 0;
                    Utils.write("Codice 041, Hai superato i tentativi massimi" + " La parola tradotta è: " + traslation, client);
                } else {
                    Utils.write("Codice 100, Errore di connessione", client);
                }
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
                if (memory.isOnline(memory.getUsers().get(memory.getUserSocketChannel().get(client)).getUsername())) {
                    memory.getUsers().get(memory.getUserSocketChannel().get(client)).setLastWord(word);
                    memory.getUsers().get(memory.getUserSocketChannel().get(client)).incrementNumWin();
                    memory.getUsers().get(memory.getUserSocketChannel().get(client)).incrementNumGame();

                    //  setto la striscia di vittorie
                    memory.getUsers().get(memory.getUserSocketChannel().get(client)).setStreakWin(memory.getUsers()
                            .get(memory.getUserSocketChannel().get(client)).getStreakWin() + 1);

                    //  Calcolo la massima striscia di vittorie
                    memory.getUsers().get(memory.getUserSocketChannel().get(client)).setMaxStreakWin(memory.getUsers()
                                    .get(memory.getUserSocketChannel().get(client)).getStreakWin());

                    //  Calcolo della media dei tentativi, tentativi rimasti + tentativi effettuati / numero di partite
                    memory.getUsers().get(memory.getUserSocketChannel().get(client))
                            .setAvgAttempt(((attempts + 1) + memory.getUsers()
                                    .get(memory.getUserSocketChannel().get(client)).getAvgAttempt())
                                    / memory.getUsers().get(memory.getUserSocketChannel().get(client)).getNumGame());

                    //  Calcolo della percentuale di vittorie, numero di vittorie / numero di partite * 100
                    memory.getUsers().get(memory.getUserSocketChannel().get(client)).setPercentWin((memory.getUsers()
                            .get(memory.getUserSocketChannel().get(client)).getNumWin()) / memory.getUsers()
                            .get(memory.getUserSocketChannel().get(client)).getNumGame() * 100);

                    //  Calcolo del punteggio, numero di vittorie * media dei tentativi
                    memory.getUsers().get(memory.getUserSocketChannel().get(client))
                            .setValueClassified(memory.getUsers().get(memory.getUserSocketChannel().get(client)).getNumWin()
                                    * memory.getUsers().get(memory.getUserSocketChannel().get(client)).getAvgAttempt());


                    //  traduzione della parola
                    String traslation = getItalianTranslation(word);

                    attempts = 0;
                    Utils.write("Codice 040, Congratulazioni! Hai indovinato la parola! " + " La parola tradotta è: " + traslation, client);
                } else {
                    Utils.write("Codice 100, Errore di connessione", client);
                }
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        } else {
            if(binarySearchDictionary(word)){
                attempts++;
                StringBuilder response = getWordController(word);
                try {
                    Utils.write("Codice 042, Parola non indovinata, " + response + ". Tentativi rimasti: " + (MAX_ATTEMPTS - attempts), client);
                } catch (IOException e) {
                    throw new RuntimeException(e);
                }
            } else {
                try {
                    Utils.write("Codice 043, Parola non appartenente nel dizionario", client);
                } catch (IOException e) {
                    throw new RuntimeException(e);
                }
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

    //  controllo se il dizionario è stato inizializzato
    private boolean binarySearchDictionary(String word){
        if (dictionary == null)
            initializeDictionary(word);
        return binarySearch(dictionary, word);
    }

    //  Metodo per inizializzare il dizionario
    private void initializeDictionary(String word) {
        dictionary = new ArrayList<>();
        try {
            //  gestire questo dettaglio
            String fileName = "words.txt";
            String absolutePath = Utils.setFileSeparator(fileName);
            FileInputStream file = new FileInputStream(absolutePath);
            BufferedReader br = new BufferedReader(new InputStreamReader(file));

            String line;
            while ((line = br.readLine()) != null) {
                dictionary.add(line);
            }
            br.close();
            file.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    //  Metodo che esegue la ricerca binaria
    private boolean binarySearch(ArrayList<String> dictionary, String word) {
        int left = 0;
        int right = dictionary.size() - 1;

        while (left <= right) {
            int middle = left + (right - left) / 2;
            int result = word.compareTo(dictionary.get(middle));

            if (result == 0) {
                return true;
            } else if (result > 0) {
                left = middle + 1;  // cerca nella parte destra
            } else {
                right = middle - 1; // cerca nella parte sinistra
            }
        }
        return false;
    }

    //  Metodo che gestisce le statistiche
    private void handleStats() {
        try {
            Utils.write("Codice 050, Numero partite: " + memory.getUsers().get(memory.getUserSocketChannel().get(client)).getNumGame()
                    + "; Numero vittorie: " + memory.getUsers().get(memory.getUserSocketChannel().get(client)).getNumWin()
                    + "; Media tentativi: " + memory.getUsers().get(memory.getUserSocketChannel().get(client)).getAvgAttempt()
                    + "; Striscia vittorie: " + memory.getUsers().get(memory.getUserSocketChannel().get(client)).getStreakWin()
                    + "; Massimo striscia vittorie: " + memory.getUsers().get(memory.getUserSocketChannel().get(client)).getMaxStreakWin()
                    + "; Vittorie: " + memory.getUsers().get(memory.getUserSocketChannel().get(client)).getPercentWin() + "%"
                    + "; Punteggio: " + memory.getUsers().get(memory.getUserSocketChannel().get(client)).getValueClassified(), client);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    //  Metodo che gestisce il logout
    private void handleLogout(String username) {
        if (memory.getUsers().get(memory.getUserSocketChannel().get(client)).getUsername().equals(username)) {
            if (memory.logout(username)) {
                try {
                    Utils.write("Codice 020, Uscita dal gioco effettuato con successo", client);
                } catch (IOException e) {
                    throw new RuntimeException(e);
                }
            } else {
                try {
                    Utils.write("Codice 021, L'utente inserito non è online", client);
                } catch (IOException e) {
                    throw new RuntimeException(e);
                }
            }
        } else {
            try {
                Utils.write("Codice 022, Username sbagliata", client);
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        }
    }

    //  Metodo che gestisce la traduzione della parola
    private String getItalianTranslation(String word) throws IOException{
        String url = "https://api.mymemory.translated.net/get";
        String lang = "en|it";
        String encodedWord = URLEncoder.encode(word, StandardCharsets.UTF_8);

        String query = String.format("q=%s&langpair=%s", encodedWord, lang);
        String fullUrl = url + "?" + query;

        HttpURLConnection connection = (HttpURLConnection) new URL(fullUrl).openConnection();
        connection.setRequestMethod("GET");

        int respondeCode = connection.getResponseCode();
        if (respondeCode == HttpURLConnection.HTTP_OK) {
            BufferedReader in = new BufferedReader(new InputStreamReader(connection.getInputStream()));
            String inputLine;
            StringBuilder response = new StringBuilder();

            while ((inputLine = in.readLine()) != null){
                response.append(inputLine);
            }
            in.close();

            //  parsing della risposta JSON per ottenere la traduzione
            return extractTranslation(response.toString());
        } else {
            throw new RuntimeException("Errore nella traduzione della parola");
        }
    }

    //  Metodo che estrae la traduzione dalla risposta JSON
    private String extractTranslation(String response) {
        JsonObject jsonObject = gson.fromJson(response, JsonObject.class);
        return jsonObject.getAsJsonObject("responseData").get("translatedText").getAsString();
    }
}