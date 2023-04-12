import java.util.Scanner;

// Classe serverMain -> Parsing, Iterazione con i client, creazione della nuova parola, gestione delle statistiche.
public class ServerMain {
    public static void main(String[] args) {
        Scanner scanner = new Scanner(System.in);   //  Scanner
        Register register;  //  Registrazione
        Login login;    //  Login
        boolean inputOk = false;    //  Controllo input
        String input;
        String stringa;

        int Inserimento;

        do{
            Inserimento= scanner.nextInt();
        switch (Inserimento){
            case 0:
                System.out.println("Registrazione, Inserisci l'username");
                stringa = scanner.next();
                System.out.println("Inserisci la password");
                input = scanner.next();
                register = new Register(stringa, input);
                break;
            case 1:
                System.out.println("Login");
                System.out.println("Inserisci l'username");
                stringa = scanner.next();
                System.out.println("Inserisci la password");
                input = scanner.next();
                login = new Login(stringa, input);
                login.controllo(stringa, input);
                inputOk = true;
                break;
        }
        }while(inputOk!=true);
    }
}
