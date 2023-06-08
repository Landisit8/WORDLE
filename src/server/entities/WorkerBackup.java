package server.entities;

import com.google.gson.Gson;
import server.Configuration;
import server.Memory;
import server.user.RankingGenerator;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;

public class WorkerBackup implements Runnable {
    private Memory memory;
    private Configuration configuration;
    private Gson gson;

    private RankingGenerator rankingGenerator;

    private boolean stop = false;

    public WorkerBackup(Memory memory, Configuration configuration, Gson gson, RankingGenerator rankingGenerator) {
        this.memory = memory;
        this.configuration = configuration;
        this.gson = gson;
        this.rankingGenerator = rankingGenerator;
    }

    @Override
    public void run() {
        //  variabili
        long checkTime = System.currentTimeMillis();
        //  file separator
        //  backup degli utenti
        String fileName;
        File memoryFile;
        String absolutePath;
        //  backup della classifica sui punteggi
        String rankingScoresFileName;
        File rankingScoreMemoryFile;
        String rankingScoresAbsolutePath;
        //  backup della classifica sugli utenti
        String rankingUsersFileName;
        File rankingUsersMemoryFile;
        String rankingUsersAbsolutePath;

        while (true) {
            long endTime = System.currentTimeMillis();
            long timeElapsed = endTime - checkTime;

            if (timeElapsed >= (configuration.getTimeBackup() * 3000)) {   //  Parsing del backup
                fileName = "backup.json";
                absolutePath = configuration.setFileSeparator(fileName);
                memoryFile = new File(absolutePath);
                //  faccio io backup solo agli utenti salvati
                String backupGson = gson.toJson(memory.getUsers());
                try {
                    if (memoryFile.createNewFile()) {
                        System.out.println("File backup creato");
                    }
                    try (FileWriter fileWriter = new FileWriter(memoryFile)) {
                        fileWriter.write(backupGson);
                        fileWriter.flush();
                        System.out.println("Backup salvato");
                    } catch (IOException e) {
                        System.out.println("Errore di scrittura");
                    }
                } catch (IOException e) {
                    throw new RuntimeException(e);
                }
                rankingScoresFileName = "rankingScore.json";
                rankingScoresAbsolutePath = configuration.setFileSeparator(rankingScoresFileName);
                rankingScoreMemoryFile = new File(rankingScoresAbsolutePath);
                //  faccio io backup solo agli utenti salvati
                String rankingScoresGson = gson.toJson(rankingGenerator.getScores());
                try {
                    if (rankingScoreMemoryFile.createNewFile()) {
                        System.out.println("File backupScores della classifica è stato creato");
                    }
                    try (FileWriter fileWriter = new FileWriter(rankingScoreMemoryFile)) {
                        fileWriter.write(rankingScoresGson);
                        fileWriter.flush();
                        System.out.println("BackupScores classifica salvato");
                    } catch (IOException e) {
                        System.out.println("Errore di scrittura");
                    }
                } catch (IOException e) {
                    throw new RuntimeException(e);
                }
                //  usernames Gson
                rankingUsersFileName = "rankingUsers.json";
                rankingUsersAbsolutePath = configuration.setFileSeparator(rankingUsersFileName);
                rankingUsersMemoryFile = new File(rankingUsersAbsolutePath);
                //  faccio io backup solo agli utenti salvati
                String rankingUsersGson = gson.toJson(rankingGenerator.getUsernames());
                try {
                    if (rankingUsersMemoryFile.createNewFile()) {
                        System.out.println("File backupUsers della classifica è stato creato");
                    }
                    try (FileWriter fileWriter = new FileWriter(rankingUsersMemoryFile)) {
                        fileWriter.write(rankingUsersGson);
                        fileWriter.flush();
                        System.out.println("BackupUsers classifica salvato");
                    } catch (IOException e) {
                        System.out.println("Errore di scrittura");
                    }
                } catch (IOException e) {
                    throw new RuntimeException(e);
                }
                checkTime = System.currentTimeMillis();
            }

            if (stop) {
                break;
            }
        }
    }

    public void setStop(boolean stop) {
        this.stop = stop;
    }

}
