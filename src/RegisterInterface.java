import java.rmi.Remote;
import java.rmi.RemoteException;
/*
    l'utente deve fornire un username e una password. Il server risponde con un codice che può identificare l'avvenuta
    registrazione, oppure, se lo usernape è già presente, o se la password è vuota, restituisce un codice di errore.
    lo username dell'utente deve essere univoco.
*/
public interface RegisterInterface extends Remote{
    boolean register(String username, String password)   throws RemoteException;
}
