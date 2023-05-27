import java.nio.channels.SocketChannel;
import java.util.concurrent.ConcurrentHashMap;

// Classe Memory -> Memorizzare tutti gli utenti, gestione delle statistiche, classifica ordinata, traduzione italiana
public class Memory {
    //  gestione degli utenti
    private ConcurrentHashMap<String, User> users;
    //  gestione delle utenti online
    private final ConcurrentHashMap<String, User> onlineUsers;
    //  Gestione degli utenti con il loro socketChannel
    private final ConcurrentHashMap<SocketChannel, String> UserSocketChannel;

    public Memory() {
        this.users = new ConcurrentHashMap<>();
        this.onlineUsers = new ConcurrentHashMap<>();
        this.UserSocketChannel = new ConcurrentHashMap<>();
    }

    public void stampa() {
        System.out.println("Utenti registrati:");
        for (SocketChannel username : this.UserSocketChannel.keySet()) {
            System.out.println(username + " " + this.UserSocketChannel.containsValue("fede"));
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

    //  Metodo per inserire un userSocketChannel
    public void insertUserSocketChannel(String username, SocketChannel socketChannel) {
        this.UserSocketChannel.putIfAbsent(socketChannel, username);
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

    //  METODI GET
    //  Metodo che ritorna la lista degli utenti online
    public ConcurrentHashMap<String, User> getOnlineUsers() {
        return onlineUsers;
    }
    //  Metodo che ritorna la lista degli utenti, per il backup
    public ConcurrentHashMap<String, User> getUsers() {
        return users;
    }
    //  Metodo che ritorna gli utenti con il proprio socketChannel
    public ConcurrentHashMap<SocketChannel, String> getUserSocketChannel() {
        return UserSocketChannel;
    }

    //  METODI SET
    //  metodo che setta la lista degli utenti, per il caricamento della memoria
    public void setUsers(ConcurrentHashMap<String, User> users) {
    this.users = users;
}

}