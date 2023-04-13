
//  RegiLog -> fase di registrazione e login con annedoti controlli
public class Register {
    /*
    l'utente deve fornire un username e una password. Il server risponde con un codice che può identificare l'avvenuta
    registrazione, oppure, se lo usernape è già presente, o se la password è vuota, restituisce un codice di errore.
    lo username dell'utente deve essere univoco.
     */
    String username;    // Univoco
    String password;    // Non vuoto
    //  classe memory x controllo username

    public Register(String username, String password) {
        this.username = username;
        this.password = password;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    /*
    METODO: controllo nella memory se username è già presente oppure se la password è vuota.
     */
}
