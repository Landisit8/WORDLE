package server.user;

import java.util.Vector;

public class User {
    private final String username;
    private final Password password;

    //  Variabile per le statistiche
    //  visibili per l'utente
    private int numGame;
    private int numWin;
    private int streakWin;
    private int maxStreakWin;
    private int guessDistribution;
    private float valueClassified;

    //  non visibili per l'utente
    private String lastWord;
    private float avgAttempt;
    private boolean flag;
    private Vector<String> gameCurrent;

    public User(String username, String password) {
        this.username = username;
        this.password = new Password(password);
        this.numWin = 0;
        this.numGame = 0;
        this.streakWin = 0;
        this.maxStreakWin = 0;
        this.guessDistribution = 0;
        this.valueClassified = 0;
        this.lastWord = "default";
        this.avgAttempt = 0;
        this.flag = false;
        this.gameCurrent = new Vector<>();
    }

    // METODI GETTER
    public String getPassword() {
        return Password.decode(password.getPassword());
    }

    public String getUsername() {
        return username;
    }

    public String getLastWord() {
        return lastWord;
    }

    public int getNumWin() {
        return numWin;
    }

    public int getNumGame() {
        return numGame;
    }

    public int getGuessDistribution() {
        return guessDistribution;
    }

    public float getAvgAttempt() {
        return avgAttempt;
    }

    public int getStreakWin() {
        return streakWin;
    }

    public int getMaxStreakWin() {
        return maxStreakWin;
    }

    public float getValueClassified() {
        return valueClassified;
    }

    public boolean getFlag() {
        return flag;
    }

    public Vector<String> getGameCurrent() {
        return gameCurrent;
    }

    // METODI SETTER
    public void setLastWord(String lastWord) {
        this.lastWord = lastWord;
    }

    public void setAvgAttempt(float avgAttempt) {
        this.avgAttempt = avgAttempt;
    }

    public void setValueClassified(float valueClassified) {
        this.valueClassified = valueClassified;
    }

    public void setFlag(boolean flag) {
        this.flag = flag;
    }

    public void setStreakWin(int streakWin) {
        this.streakWin = streakWin;
    }

    public void setMaxStreakWin(int streakWin) {
        //  algoritmo del massimo
        if (streakWin > this.maxStreakWin) {
            this.maxStreakWin = streakWin;
        }
    }

    public void setGuessDistribution(int guessDistribution) {
        this.guessDistribution = guessDistribution;
    }

    // METODI COUNTERS
    public void incrementNumWin() {
        this.numWin++;
    }

    public void incrementNumGame() {
        this.numGame++;
    }

    public void incrementStreakWin() {
        this.streakWin++;
    }

    // METODI PER IL GIOCO

    //  aggiungo la parola alla lista dei tentativi dell'utente
    public void addAttempts(String word) {

        gameCurrent.add(word);
    }

    //  resetto il vector dalla lista dei tentativi dell'utente
    public void resetAttempts() {
        gameCurrent.clear();
    }
}
