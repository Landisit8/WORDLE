
//  RegiLog -> fase di registrazione e login con annedoti controlli
public class Login {
    //  Classe memory per controllo
    private String username;
    private String password;

    public Login(String username, String password) {
        this.username = username;
        this.password = password;
    }
    //  Controllo username e password, Sistema il tipo di ritorno.
    public void controllo (String username, String password) {
        //  Controllo username
        //  Controllo password
        if ( username == null || password == null ) {
            System.out.println("Username o password non validi");
        } else {
            //  Qui controllo se username e password sono presenti nel memory, se si allora login
            System.out.println("Username e password validi");
        }
    }
}
