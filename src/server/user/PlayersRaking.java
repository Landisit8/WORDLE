package server.user;

public class PlayersRaking {

    private String username;
    private float valueClassified;


    public PlayersRaking(String username, float valueClassified) {
        this.username = username;
        this.valueClassified = valueClassified;

    }

    public String getUsername() {
        return username;
    }

    public float getValueClassified() {
        return valueClassified;
    }

}
