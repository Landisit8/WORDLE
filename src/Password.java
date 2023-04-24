public class Password {
    //  Scrivere metodi per la gestione della password, forse mettere i metodi per
    //  Controllare l'utente che vuole fare quella operazione sia lo stesso che deve fare quella operazione
    private String password;

    public Password(String password) {
        this.password = password;
    }

    public String getPassword() {
        return this.password;
    }

    public void setPassword(String password) {
        this.password = password;
    }
}
