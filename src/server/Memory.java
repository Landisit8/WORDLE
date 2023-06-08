package server;

import server.user.User;

import java.nio.channels.SocketChannel;
import java.util.concurrent.ConcurrentHashMap;

// Classe server.Memory -> Memorizzare tutti gli utenti, gestione delle statistiche, classifica ordinata, traduzione italiana
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
            System.out.println(username + " -> " + this.UserSocketChannel.get(username));
        }
    }

    //  Metodo per inserire un utente
    public synchronized void insertUser(String username, String password) {
        this.users.put(username, new User(username, password));
    }

    //  Metodo per inserire un utente online
    public synchronized void insertOnlineUser(String username, String password) {
        this.onlineUsers.put(username, new User(username, password));
    }

    //  Metodo per inserire un userSocketChannel
    public synchronized void insertUserSocketChannel(String username, SocketChannel socketChannel) {
        this.UserSocketChannel.putIfAbsent(socketChannel, username);
    }

    //  Metodo per rimuovere un utente online
    public synchronized void removeOnlineUser(String username) {
        this.onlineUsers.remove(username);
    }

    //  Metodo per controllare se un utente è online
    public synchronized boolean isOnline(String username) {
        return this.onlineUsers.containsKey(username);
    }

    //  Metodo per controllare se un utente è registrato
    public synchronized boolean isRegistered(String username) {
        return this.users.containsKey(username);
    }

    //  Metodo di login,
    public synchronized int login(String username, String password) {
        if (this.isRegistered(username)){
            if (this.users.get(username).getPassword().equals(password)){
                if (!this.isOnline(username)){
                    System.out.println("Utente online: " + username + " è stato connesso");
                    this.insertOnlineUser(username, password);
                    return 0; //  Login effettuato
                } else {
                    return 1; //  Utente già online
                }
            } else {
                return 2; //  server.user.Password errata
            }
        } else {
            return 3; //  Utente non registrato
        }
    }

    //  Metodo di logout
    public synchronized int logout(String username) {
        System.out.println("-1");
        if (this.isRegistered(username)) {
            if (this.isOnline(username)) {
                System.out.println("Utente online: " + username + " è stato disconnesso");
                this.removeOnlineUser(username);
                System.out.println("0");
                return 0; //  Logout effettuato
            } else {
                System.out.println("1");
                return 1; //  Utente non online
            }
        } else {
            System.out.println("2");
            return 2; //  Username sbagliata
        }
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
    public synchronized void setUsers(ConcurrentHashMap<String, User> users) {
    this.users = users;
}

    //  METODO CHE SETTA IL FLAG DEGLI UTENTI
    public synchronized void setFlag() {
        this.users.forEach((key, value) -> value.setFlag(false));
    }
}