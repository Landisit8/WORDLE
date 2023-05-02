import java.rmi.RemoteException;
public class RegisterServiceImpl implements RegisterInterface{
    Memory memory = new Memory();

    public boolean register(String username, String password) throws RemoteException{
        if ( username.isEmpty() || password.isEmpty() ) {
            System.out.println("Username o password non validi");
            return false;
        } else {
            //  Qui controllo se username e password sono presenti nel memory, se si allora registro l'utente
            if (memory.isRegistered(username)) {
                System.out.println("Utente già registrato");
                return false;
            } else {
                memory.insertUser(username, password);
                System.out.println("Utente registrato");
                return true;
            }
        }
    }
}
