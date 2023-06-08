package server.user;

import java.util.Collections;
import java.util.Vector;

public class RankingGenerator {
    private Vector<String> usernames;
    private Vector<Float> scores;
    public RankingGenerator() {
        usernames = new Vector<>();
        scores = new Vector<>();
    }

    public synchronized void updateRanking(String username, float score) {
        if (!usernames.contains(username)) {
            usernames.add(username);
            scores.add(score);
        } else {
            int index = usernames.indexOf(username);
            scores.set(index, score);
        }
    }

    public synchronized Vector<String> generateRanking() {
        System.out.println("generating ranking");

        Vector<String> usernamesCopy = new Vector<>(usernames);
        Vector<Float> scoresCopy = new Vector<>(scores);
        Vector<String> ranking = new Vector<>();
        // stampo usernames e scores copy
        System.out.println("usernames: " + usernamesCopy.size());
        for (int i = 0; i < usernames.size(); i++) {
            int minIndex = scoresCopy.indexOf(Collections.min(scoresCopy));
            ranking.add(usernamesCopy.get(minIndex) + ":" + scoresCopy.get(minIndex));
            scoresCopy.remove(minIndex);
            usernamesCopy.remove(minIndex);
        }
        return ranking;
    }

    //  metodo che confronta le prime tre posizioni
    public synchronized boolean checkTopThree(Vector<String> oldRaking, Vector<String> newRanking) {
        if (oldRaking.size() < 3) {
            return false;
        }
        for (int i = 0; i < 3; i++) {
            String oldEntry = oldRaking.get(i);
            String newEntry = newRanking.get(i);

            if (!oldEntry.equals(newEntry)) {
                return true;
            }
        }
        return false;
    }

    // get dei vector
    public Vector<Float> getScores() {
        return scores;
    }
    public Vector<String> getUsernames() {
        return usernames;
    }
    // set della vector
    public synchronized void setScores(Vector<Float> scores) {
        this.scores = scores;
    }
    public synchronized void setUsernames(Vector<String> usernames) {
        this.usernames = usernames;

    }
}
