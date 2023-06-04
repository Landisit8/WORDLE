package server;

import com.google.gson.Gson;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.concurrent.TimeUnit;

public class WorkerBackup implements Runnable {
    Memory memory;
    Configuration configuration;
    Gson gson;

    private boolean stop = false;

    public WorkerBackup(Memory memory, Configuration configuration, Gson gson) {
        this.memory = memory;
        this.configuration = configuration;
        this.gson = gson;
    }

    @Override
    public void run() {
        //  variabili
        long checkTime = System.currentTimeMillis();
        //  file separator
        String fileName;
        File memoryFile;
        String absolutePath;

        while (true) {
            long endTime = System.currentTimeMillis();
            long timeElapsed = endTime - checkTime;
            if (timeElapsed >= TimeUnit.SECONDS.toMillis(15)) {   //  Parsing del backup
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
