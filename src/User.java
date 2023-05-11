public class User {
    private final String username;
    private final Password password;
    private Statistics statistics;

    public User(String username, String password) {
        this.username = username;
        this.password = new Password(password);
    }

    public String getPassword() {
        return Password.decode(password.getPassword());
    }

    public String getUsername() {
        return username;
    }
}
