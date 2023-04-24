public class User {
    private String username;
    private Password password;
    private Statistics statistics;

    public User(String username, String password) {
        this.username = username;
        this.password = new Password(password);
    }
}
