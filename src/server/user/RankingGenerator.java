package server.user;

import java.util.*;

public class RankingGenerator {
    List<String> usernames = new ArrayList<>();
    List<Float> values = new ArrayList<>();
    public RankingGenerator(String username, float valueClassified) {
        usernames.add(username);
        values.add(valueClassified);
    }

    public Vector<String> generateRanking() {
        //  creazione della mappa che associa username e punteggio
        Map<String, Float> rankingMap = new HashMap<>();
        for (int i = 0; i < usernames.size(); i++) {
            String username = usernames.get(i);
            Float value = values.get(i);
            rankingMap.put(username, value);
        }
        //  ordinamento della mappa in base al punteggio in ordine crescente
        List<Map.Entry<String, Float>> sortedEntries = new LinkedList<>(rankingMap.entrySet());
        sortedEntries.sort(Map.Entry.comparingByValue());

        //  creazione del vettore con i primi tre classificati
        Vector<String> topThree = new Vector<>();
        int count = 0;
        for (Map.Entry<String, Float> entry : sortedEntries) {
            if (count >= 3)
                break;
            else {
                topThree.add(entry.getKey());
                count++;
            }
        }
        return topThree;
    }
}
