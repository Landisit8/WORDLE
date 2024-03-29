package server.entities;

import com.google.gson.Gson;
import com.google.gson.JsonObject;
import server.Configuration;
import server.Memory;
import server.rmi.RankingServerImpl;
import server.user.RankingGenerator;
import shared.Utils;

import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.*;
import java.nio.channels.SocketChannel;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Vector;

public class Worker implements Runnable{
    private String mStringa;
    private Memory memory;
    private SocketChannel client;
    private Gson gson;
    private Configuration configuration;

    //  Variabili per il gioco
    private static int attempts = 0;   //  Tentativi effettuati
    private static final int MAX_ATTEMPTS = 3;   //  Numero massimo di tentativi
    private ArrayList<String> dictionary;

    //  Variabili per la gestione della classifica
    private Vector<String> ranking;
    private RankingGenerator rankingGenerator;
    private Vector<String> oldRanking;
    private RankingServerImpl rankingServer;


    public Worker(String stringa, Memory memory, SocketChannel client, Gson gson, Configuration configuration,
                  RankingGenerator rankingGenerator, RankingServerImpl rankingServer){
        this.mStringa = stringa;
        this.memory = memory;
        this.client = client;
        this.gson = gson;
        this.configuration = configuration;
        this.rankingGenerator = rankingGenerator;
        this.rankingServer = rankingServer;
        this.oldRanking = new Vector<>();
        }

    @Override
    public void run() {
        System.out.println("server.entities.Worker: " + mStringa);
        String[] options = mStringa.split(" ");
        switch (options[0]) {
            case "login":
                handleLogin(options[1], options[2]);
                break;
            case "playWordle":
                playWordle();
                memory.getUsers().get(memory.getUserSocketChannel().get(client)).setFlag(true);
                break;
            case "sendWord":
                if (memory.getUsers().get(memory.getUserSocketChannel().get(client)).getLastWord().equals(WorkerWord.wordGuess)
                ||).get(memory.getUserSocketChannel().get(client)).getLastWord().equals("loser")){
                    try {
                        Utils.write("Codice 031, Hai già giocato questa parola", client);
                    } catch (IOException e) {
                        throw new RuntimeException(e);
                    }
                } else {
                    if (memory.getUsers().get(memory.getUserSocketChannel().get(client)).getFlag()) {
                        handleSendWord(options[1]);
                    } else{
                        try {
                            Utils.write("Codice 101, Esegui prima playwordle", client);
                        } catch (IOException e) {
                            throw new RuntimeException(e);
                        }
                    }
                }
                    break;
                case "sendMeStatistics":
                    handleStats();
                    break;
                case "share":
                    //  share
                    try(DatagramSocket socket = new DatagramSocket()){
                        InetAddress group = InetAddress.getByName(configuration.getMulticastAddress());

                        String message = null;
                        String numberWordGuess = String.valueOf(WorkerWord.numberWordGuess);
                        String vectorAttempts = "";
                        switch (memory.getUsers().get(memory.getUserSocketChannel().get(client)).getLastWord()){
                            case "default":
                                try {
                                    Utils.write("Codice 061, Non hai ancora giocato", client);
                                } catch (IOException e) {
                                    throw new RuntimeException(e);
                                }
                                break;
                            case "loser":
                                for(String word : memory.getUsers().get(memory.getUserSocketChannel().get(client)).getGameCurrent())
                                    vectorAttempts += word + " ";

                                message = numberWordGuess + " partita, "
                                        + memory.getUsers().get(memory.getUserSocketChannel().get(client)).getUsername()
                                        + ": " + vectorAttempts;
                                try {
                                    Utils.write("Codice 060, Condiviso con successo", client);
                                } catch (IOException e) {
                                    throw new RuntimeException(e);
                                }
                                break;
                            default:
                                for(String word : memory.getUsers().get(memory.getUserSocketChannel().get(client)).getGameCurrent())
                                    vectorAttempts += word + " ";

                                message = numberWordGuess + " partita, "
                                        + memory.getUsers().get(memory.getUserSocketChannel().get(client)).getUsername()
                                        + ": " + vectorAttempts;
                                try {
                                    Utils.write("Codice 060, Condiviso con successo", client);
                                } catch (IOException e) {
                                    throw new RuntimeException(e);
                                }
                                try {
                                    Utils.write("Codice 060, Condiviso con successo", client);
                                } catch (IOException e) {
                                    throw new RuntimeException(e);
                                }
                                break;
                        }

                        if (message == null)
                            break;

                        byte[] buffer = message.getBytes();

                        //  Dalla lunghezza del messaggio lo trasformo in una stringa e infine in un array di byte
                        String lengthMessage = String.valueOf(message.length());
                        byte[] size = lengthMessage.getBytes();

                        DatagramPacket sizePacket = new DatagramPacket(size, size.length, group, configuration.getUDP_PORT());
                        socket.send(sizePacket);

                        DatagramPacket packet = new DatagramPacket(buffer, buffer.length, group, configuration.getUDP_PORT());
                        socket.send(packet);

                    } catch (IOException e) {
                        throw new RuntimeException(e);
                    }
                    break;
                case "showMeRanking":
                    //  showMeRanking
                    try {
                        ranking = rankingGenerator.generateRanking();
                        Utils.write(gson.toJson(ranking), client);
                    } catch (IOException e) {
                        throw new RuntimeException(e);
                    }
                    break;
                case "logout":
                    //  logout
                    System.out.println("Logout");
                    handleLogout(options[1]);
                    break;
        }
    }

    //  Metodo che gestisce il login
    private synchronized void handleLogin(String username, String password){
        //  fare uno switch case
        switch (memory.login(username, password)){
            case 0:
                try {
                    memory.insertUserSocketChannel(username, client);
                    Utils.write("Codice 010, Login effettuato", client);
                } catch (IOException e) {
                    throw new RuntimeException(e);
                }
                break;
            case 1:
                try {
                    Utils.write("Codice 011, Utente già online", client);
                } catch (IOException e) {
                    throw new RuntimeException(e);
                }
                break;
            case 2:
                try {
                    Utils.write("Codice 012, Password errata", client);
                } catch (IOException e) {
                    throw new RuntimeException(e);
                }
                break;
            case 3:
                try {
                    Utils.write("Codice 013, Utente non registrato", client);
                } catch (IOException e) {
                    throw new RuntimeException(e);
                }
                break;
            default:
                try {
                    Utils.write("Codice 100, errore di connessione", client);
                } catch (IOException e) {
                    throw new RuntimeException(e);
                }
                break;
        }
    }

    //  Metodo che gestisce il playWordle
    public synchronized void playWordle(){
        String word = WorkerWord.wordGuess;
        String lastWordGuessed = memory.getUsers().get(memory.getUserSocketChannel().get(client)).getLastWord();

        if (word.isEmpty()) {
            try {
                Utils.write("Codice 032, Non è stata generata la parola da indovinare", client);
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        } else {
            if (lastWordGuessed.equals(word)) {
                try {
                    Utils.write("Codice 031, Hai già giocato questa parola", client);
                } catch (IOException e) {
                    throw new RuntimeException(e);
                }
            } else {
                try {
                    Utils.write("Codice 030, INIZIO PARTITA", client);
                    memory.getUsers().get(memory.getUserSocketChannel().get(client)).resetAttempts();
                } catch (IOException e) {
                    throw new RuntimeException(e);
                }
            }
        }
    }

    //  Metodo che controlla la parola inserita dall'utente
    public synchronized void handleSendWord(String word){
        //  caso che l'utente ha finito i tentativi
        if (attempts >= MAX_ATTEMPTS) {
            try {
                if (memory.isOnline(memory.getUsers().get(memory.getUserSocketChannel().get(client)).getUsername())) {

                    memory.getUsers().get(memory.getUserSocketChannel().get(client)).setLastWord("loser");
                    memory.getUsers().get(memory.getUserSocketChannel().get(client)).incrementNumGame();
                    memory.getUsers().get(memory.getUserSocketChannel().get(client)).setStreakWin(0);

                    //  Calcolo della media dei tentativi, tentativi rimasti + tentativi effettuati / numero di partite
                    memory.getUsers().get(memory.getUserSocketChannel().get(client))
                            .setAvgAttempt(((attempts + 1) + memory.getUsers()
                                    .get(memory.getUserSocketChannel().get(client)).getAvgAttempt())
                                    / memory.getUsers().get(memory.getUserSocketChannel().get(client)).getNumGame());

                    //  Calcolo del punteggio, numero di vittorie * media dei tentativi
                    memory.getUsers().get(memory.getUserSocketChannel().get(client))
                            .setValueClassified(memory.getUsers().get(memory.getUserSocketChannel().get(client)).getNumWin()
                                    * memory.getUsers().get(memory.getUserSocketChannel().get(client)).getAvgAttempt());

                    //  gestione della classifica
                    rankingGenerator.updateRanking(memory.getUsers().get(memory.getUserSocketChannel().get(client)).getUsername()
                    , memory.getUsers().get(memory.getUserSocketChannel().get(client)).getValueClassified());
                    ranking = rankingGenerator.generateRanking();
                    if (rankingGenerator.checkTopThree(oldRanking,ranking)){
                        rankingServer.updateRanking("Codice 105, Le prime 3 posizioni della classifica sono state modificate");
                    }
                    oldRanking = ranking;

                    //  traduzione della parola
                    String traslation = getItalianTranslation(WorkerWord.wordGuess);

                    memory.getUsers().get(memory.getUserSocketChannel().get(client)).setGuessDistribution(attempts);

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
                    memory.getUsers().get(memory.getUserSocketChannel().get(client)).incrementStreakWin();

                    //  Calcolo la massima striscia di vittorie
                    memory.getUsers().get(memory.getUserSocketChannel().get(client)).setMaxStreakWin(memory.getUsers()
                                    .get(memory.getUserSocketChannel().get(client)).getStreakWin());

                    //  Calcolo della media dei tentativi, tentativi rimasti + tentativi effettuati / numero di partite
                    memory.getUsers().get(memory.getUserSocketChannel().get(client))
                            .setAvgAttempt(((attempts + 1) + memory.getUsers()
                                    .get(memory.getUserSocketChannel().get(client)).getAvgAttempt())
                                    / memory.getUsers().get(memory.getUserSocketChannel().get(client)).getNumGame());

                    //  Calcolo del punteggio, numero di vittorie * media dei tentativi
                    memory.getUsers().get(memory.getUserSocketChannel().get(client))
                            .setValueClassified(memory.getUsers().get(memory.getUserSocketChannel().get(client)).getNumWin()
                                    * memory.getUsers().get(memory.getUserSocketChannel().get(client)).getAvgAttempt());

                    //  gestione della classifica
                    rankingGenerator.updateRanking(memory.getUsers().get(memory.getUserSocketChannel().get(client)).getUsername()
                            , memory.getUsers().get(memory.getUserSocketChannel().get(client)).getValueClassified());
                    ranking = rankingGenerator.generateRanking();


                    //  traduzione della parola
                    String traslation = getItalianTranslation(word);

                    //  aggiungo nella lista current word la stringa ceh ha indovinato
                    memory.getUsers().get(memory.getUserSocketChannel().get(client)).addAttempts("++++++++++");

                    memory.getUsers().get(memory.getUserSocketChannel().get(client)).setGuessDistribution(attempts+1);

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
                memory.getUsers().get(memory.getUserSocketChannel().get(client)).addAttempts(response.toString());
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
    public synchronized StringBuilder getWordController(String word){
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
    private synchronized boolean binarySearchDictionary(String word){
        if (dictionary == null)
            initializeDictionary(word);
        return binarySearch(dictionary, word);
    }

    //  Metodo per inizializzare il dizionario
    private synchronized void initializeDictionary(String word) {
        dictionary = new ArrayList<>();
        try {
            //  gestire questo dettaglio
            String fileName = "words.txt";
            String absolutePath = configuration.setFileSeparator(fileName);
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
    private synchronized boolean binarySearch(ArrayList<String> dictionary, String word) {
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
    private synchronized void handleStats() {
        try {
            Utils.write("Codice 050 STATISTICHE"
                    + "; Numero partite: " + memory.getUsers().get(memory.getUserSocketChannel().get(client)).getNumGame()
                    + "; Numero vittorie: " + memory.getUsers().get(memory.getUserSocketChannel().get(client)).getNumWin()
                    + "; tentativi effettuati: " + memory.getUsers().get(memory.getUserSocketChannel().get(client)).getGuessDistribution()
                    + "; Striscia vittorie: " + memory.getUsers().get(memory.getUserSocketChannel().get(client)).getStreakWin()
                    + "; Massimo striscia vittorie: " + memory.getUsers().get(memory.getUserSocketChannel().get(client)).getMaxStreakWin()
                    + "; Punteggio: " + memory.getUsers().get(memory.getUserSocketChannel().get(client)).getValueClassified(), client);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    //  Metodo che gestisce il logout
    private synchronized void handleLogout(String username) {
            switch (memory.logout(username)){
                case 0:
                    System.out.println("Codice 020, Logout effettuato con successo");
                    try {
                        Utils.write("Codice 020, Logout effettuato con successo", client);
                    } catch (IOException e) {
                        throw new RuntimeException(e);
                    }
                    break;
                case 1:
                    System.out.println("Codice 021, Utente non è online");
                    try {
                        Utils.write("Codice 021, Utente non è online", client);
                    } catch (IOException e) {
                        throw new RuntimeException(e);
                    }
                    break;
                case 2:
                    System.out.println("Codice 022, Username sbagliata");
                    try {
                        Utils.write("Codice 022, Username sbagliata", client);
                    } catch (IOException e) {
                        throw new RuntimeException(e);
                    }
                    break;
            }
    }

    //  Metodo che gestisce la traduzione della parola
    private synchronized String getItalianTranslation(String word) throws IOException{
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
    private synchronized String extractTranslation(String response) {
        JsonObject jsonObject = gson.fromJson(response, JsonObject.class);
        return jsonObject.getAsJsonObject("responseData").get("translatedText").getAsString();
    }
}