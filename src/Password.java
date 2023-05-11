public class Password {
    //  Scrivere metodi per la gestione della password, forse mettere i metodi per
    //  Controllare l'utente che vuole fare quella operazione sia lo stesso che deve fare quella operazione
    private String password;

    public Password(String password) {
        this.password = encode(password);
    }

    public String getPassword() {
        return this.password;
    }


    //  Metodo per la codifica della password
    public static String encode(String password) {
        int j;
        StringBuilder encodedPassword = new StringBuilder();
        for (int i = 0; i < password.length(); i++) {
            j = (int) password.charAt(i) + 3;   //  nuovo codice ASCII della lettera attuale
            System.out.println(j);
            if (j > 126)    j -= 93; //  se vado oltre la ~ decremento j di 93 posizioni
            encodedPassword.append((char) j);   //  converto il codice ASCII nella lettera corrispondente
        }
        System.out.println(encodedPassword);
        return encodedPassword.toString();
    }

    //  Metodo per la decodifica della password
    public static String decode(String password) {
        int j;
        StringBuilder decodedPassword = new StringBuilder();
        for (int i = 0; i < password.length(); i++) {
            j = (int) password.charAt(i) - 3;   //  nuovo codice ASCII della lettera attuale
            System.out.println(j);
            if (j < 33)    j += 93; //  se vado sotto la ! incremento j di 93 posizioni
            decodedPassword.append((char) j);   //  converto il codice ASCII nella lettera corrispondente
        }
        System.out.println(decodedPassword);
        return decodedPassword.toString();
    }
}
