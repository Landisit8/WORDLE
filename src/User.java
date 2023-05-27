public class User {
    private final String username;
    private final Password password;
    //Variabile per le statistiche
    private String lastWord = " ";

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

    // METODI SETTER
    public void setLastWord(String lastWord) {
        this.lastWord = lastWord;
    }

}
