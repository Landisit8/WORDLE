import com.google.gson.Gson;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.util.Random;

public class WorkerTime implements Runnable{
    Memory memory;
    Configuration configuration;
    Gson gson;
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
        Random random = new Random();
        //  file separator
        String fileName;
        String os = System.getProperty("os.name").toLowerCase();
        String workingDir = System.getProperty("user.dir");
        String absolutePath;
        File memoryFile;

        //  Capire quando generare questo tempo
        long endTime = System.currentTimeMillis();
        System.out.println("Tempo trascorso: " + (endTime - startTime)/1000 + " secondi");

        if (((endTime - startTime)/1000) >= 60) {   //  Parsing del backup
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
                    // Deserializzazione del backup
                    memory = gson.fromJson(backupGson, Memory.class);
                }
                try(FileWriter fileWriter = new FileWriter(memoryFile)){
                    fileWriter.write(backupGson);
                    System.out.println("Backup salvato");
                }catch (IOException e){
                    System.out.println("Errore di scrittura");
                }
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        } else if (((endTime - startTime)/1000) >= 300) {   //  Parsing del dizionario
            try {
                System.out.println("Sono passati 5 minuti");
                //  Parsing del backup
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
                File file = new File(absolutePath);
                long fileSize = file.length();
                long randomPosition = random.nextLong()%fileSize;
                RandomAccessFile randomAccessFile = new RandomAccessFile(file, "r");
                randomAccessFile.seek(randomPosition);
                //  scorri all'indietro fino all'inizio della riga
                while (randomAccessFile.getFilePointer() > 0 && randomAccessFile.readByte() != '\n') {
                    randomAccessFile.seek(randomAccessFile.getFilePointer() - 1);
                }
                // capire come gestire la parola word
                String word = randomAccessFile.readLine();
                randomAccessFile.close();
                System.out.println("La parola da indovinare è: " + word);
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        }

    }
}
