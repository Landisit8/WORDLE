import com.google.gson.Gson;

import java.io.*;
import java.util.Random;
import java.util.concurrent.TimeUnit;

public class WorkerTime implements Runnable {
    Memory memory;
    Configuration configuration;
    Gson gson;

    private boolean stop = false;

    public WorkerTime(Memory memory, Configuration configuration, Gson gson) {
        this.memory = memory;
        this.configuration = configuration;
        this.gson = gson;
    }

    @Override
    public void run() {
        //  controllo delle metodi che devono essere eseguiti ogni tot tempo
        //  variabili
        long startTime = System.currentTimeMillis();
        long checkTime = startTime;
        //  file separator
        String fileName;
        String os = System.getProperty("os.name").toLowerCase();
        String workingDir = System.getProperty("user.dir");
        String absolutePath;
        File memoryFile;

        //  Capire quando generare questo tempo
        while (true) {
            long endTime = System.currentTimeMillis();
            long timeElapsed = endTime - checkTime;
            if (timeElapsed >= TimeUnit.SECONDS.toMillis(10)) {   //  Parsing del backup
                System.out.println("Sono passati 60 secondi");
                fileName = "backup.json";
                //  trasformare in uno switch
                if (os.contains("win")) {
                    // se windows
                    absolutePath = workingDir + "\\" + fileName;
                } else if (os.contains("mac")) {
                    absolutePath = workingDir + "/" + fileName;
                } else if (os.contains("nix") || os.contains("nux") || os.contains("aix")) {
                    absolutePath = workingDir + "/" + fileName;
                } else if (os.contains("sunos")) {
                    absolutePath = workingDir + "/" + fileName;
                } else {
                    System.out.println("Sistema operativo non riconosciuto");
                    absolutePath = workingDir + "/" + fileName;
                }
                memoryFile = new File(absolutePath);
                String backupGson = gson.toJson(memory);
                try {
                    if (memoryFile.createNewFile()) {
                        System.out.println("File backup creato");
                    } else {
                        System.out.println("File backup già esistente");
                    }
                    try (FileWriter fileWriter = new FileWriter(memoryFile)) {
                        System.out.println("Salvataggio del backup: " + backupGson);
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
            } if (timeElapsed >= TimeUnit.SECONDS.toMillis(5)) {   //  Parsing del dizionario
                System.out.println("Sono passati 2 minuti");
                //  Parsing del dizionario
                fileName = "words.txt";
                if (os.contains("win")) {
                    // se windows
                    absolutePath = workingDir + "\\" + fileName;
                } else if (os.contains("mac")) {
                    // se mac
                    absolutePath = workingDir + "/" + fileName;
                } else if (os.contains("nix") || os.contains("nux") || os.contains("aix")) {
                    // se unix
                    absolutePath = workingDir + "/" + fileName;
                } else if (os.contains("sunos")) {
                    // se solaris
                    absolutePath = workingDir + "/" + fileName;
                } else {
                    // se non riconosciuto
                    System.out.println("Sistema operativo non riconosciuto");
                    absolutePath = workingDir + "/" + fileName;
                }
                System.out.println("Il percorso del file è: " + absolutePath);
                String word = extractWord(absolutePath);
                System.out.println("La parola da indovinare è: " + word);
                checkTime = System.currentTimeMillis();
            }
            try {
                Thread.sleep(1000); // attendo un secondo prima di verificare nuovamente
            } catch (InterruptedException e) {
                e.printStackTrace();
            }

            if (stop) {
                break;
            }
        }
    }

    public void setStop(boolean stop) {
        this.stop = stop;
    }

    private String extractWord(String absolutePath) {
        try (BufferedReader reader = new BufferedReader(new FileReader(absolutePath))) {
            Random random = new Random();
            String word;
            int lines = 0;
            while ((word = reader.readLine()) != null) {
                lines++;
            }
            if (lines > 0) {
                int randomLine = random.nextInt(lines);
                reader.reset();
                for (int i = 0; i < randomLine; i++) {
                    reader.readLine();
                }
                return reader.readLine();
            } else {
                System.out.println("Il file è vuoto");
            }
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        return null;
    }
}