package server.user;

public class User {
    private final String username;
    private final Password password;

    //Variabile per le statistiche dividere le statistiche da stampare e no
    private String lastWord;
    private int numWin;
    private int numGame;
    private float avgAttempt;
    private int streakWin;
    private int maxStreakWin;
    private float valueClassified;
    private boolean flag;

    public User(String username, String password) {
        this.username = username;
        this.password = new Password(password);
        this.lastWord = "default";
        this.numWin = 0;
        this.numGame = 0;
        this.avgAttempt = 0;
        this.streakWin = 0;
        this.maxStreakWin = 0;
        this.valueClassified = 0;
        this.flag = false;
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

    // METODI COUNTERS
    public void incrementNumWin() {
        this.numWin++;
    }

    public void incrementNumGame() {
        this.numGame++;
    }

}
