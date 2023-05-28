public class User {
    private final String username;
    private final Password password;

    //Variabile per le statistiche
    private String lastWord = " ";
    private int numWin = 0;
    private int numGame = 0;
    private float avgAttempt = 0;
    private int percentWin = 0;
    private float valueClassified = 0;

    public User(String username, String password) {
        this.username = username;
        this.password = new Password(password);
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

    public int getPercentWin() {
        return percentWin;
    }

    public float getValueClassified() {
        return valueClassified;
    }

    // METODI SETTER
    public void setLastWord(String lastWord) {
        this.lastWord = lastWord;
    }

    public void setAvgAttempt(float avgAttempt) {
        this.avgAttempt = avgAttempt;
    }

    public void setPercentWin(int percentWin) {
        this.percentWin = percentWin;
    }

    public void setValueClassified(float valueClassified) {
        this.valueClassified = valueClassified;
    }

    // METODI COUNTERS
    public void incrementNumWin() {
        this.numWin++;
    }

    public void incrementNumGame() {
        this.numGame++;
    }



}
