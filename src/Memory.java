import java.util.concurrent.ConcurrentHashMap;

// Classe Memory -> Memorizzare tutti gli utenti, gestione delle statistiche, classifica ordinata, traduzione italiana
public class Memory {
    //  gestione degli utenti
    private ConcurrentHashMap<String, User> users;
    //  gestione delle utenti online
    private final ConcurrentHashMap<String, User> onlineUsers;

    public Memory() {
        this.users = new ConcurrentHashMap<>();
        this.onlineUsers = new ConcurrentHashMap<>();
    }

    public void stampa() {
        System.out.println("Utenti registrati:");
        for (String username : this.users.keySet()) {
            System.out.println("Username: " + username);
        }
    }

    //  Metodo per inserire un utente
    public void insertUser(String username, String password) {
        this.users.put(username, new User(username, password));
    }

    //  Metodo per inserire un utente online
    public void insertOnlineUser(String username, String password) {
        this.onlineUsers.put(username, new User(username, password));
    }

    //  Metodo per rimuovere un utente online
    public void removeOnlineUser(String username) {
        this.onlineUsers.remove(username);
    }

    //  Metodo per controllare se un utente è online
    public boolean isOnline(String username) {
        return this.onlineUsers.containsKey(username);
    }

    //  Metodo per controllare se un utente è registrato
    public boolean isRegistered(String username) {
        return this.users.containsKey(username);
    }

    //  Metodo di login,
    public boolean login(String username, String password) {
        if (this.isRegistered(username) && this.users.get(username).getPassword().equals(password) && !this.isOnline(username)) {
            System.out.println("Utente online: " + username + " è stato connesso");
            this.insertOnlineUser(username, password);
            return true;
        }
        return false;
    }

    //  Metodo di logout
    public boolean logout(String username) {
        if (this.isOnline(username)) {
            System.out.println("Utente online: " + username + " è stato disconnesso");
            this.removeOnlineUser(username);
            return true;
        }
        return false;
    }

    //  Metodo che ritorna la lista degli utenti, per il backup
    public ConcurrentHashMap<String, User> getUsers() {
        return users;
    }

    //  metodo che setta la lista degli utenti, per il caricamento della memoria
    public void setUsers(ConcurrentHashMap<String, User> users) {
        this.users = users;
    }

}
