import java.rmi.RemoteException;
public class RegisterServiceImpl implements RegisterInterface{
    String username = "admin";
    String password = "admin123";

    public int register(String username, String password) throws RemoteException{
        if ( username == null || password == null ) {
            System.out.println("Username o password non validi");
            return 1;
        } else {
            //  Qui controllo se username e password sono presenti nel memory, se si allora login
            if ( username.equals(this.username) && password.equals(this.password) ) {
                System.out.println("Username e password validi");
                return 0;
            } else {
                System.out.println("Username o password non validi");
                return 1;
            }
        }
    }
}
