import com.google.gson.Gson;

import java.io.File;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.util.Random;
import java.util.concurrent.TimeUnit;

public class WorkerWord implements Runnable{
    Memory memory;
    Configuration configuration;
    Gson gson;

    private boolean stop = false;

    public WorkerWord(Memory memory, Configuration configuration, Gson gson) {
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
        String absolutePath;
        File memoryFile;

        //  Capire quando generare questo tempo
        while (true) {
            long endTime = System.currentTimeMillis();
            long timeElapsed = endTime - checkTime;
            if (timeElapsed >= TimeUnit.SECONDS.toMillis(20)) {   //  Parsing del dizionario
                //  Parsing del dizionario
                fileName = "words.txt";
                absolutePath = Utils.setFileSeparator(fileName);
                memoryFile = new File(absolutePath);
                String word;
                try {
                    word = extractWord(memoryFile);
                } catch (IOException e) {
                    throw new RuntimeException(e);
                }
                //  System.out.println("La parola da indovinare è: " + word);

                //  ogni volta che viene generata una nuova parola, viene generato una nuova partita
                //  di wordle
                WordleGame wordleGame = new WordleGame();
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

    private String extractWord(File file) throws IOException{
        Random random = new Random();
        int fileSize = (int)file.length();
        int randomPosition = random.nextInt(fileSize);

        RandomAccessFile randomAccessFile = new RandomAccessFile(file, "r");
        randomAccessFile.seek(randomPosition);

        //  scorri all'indietro fino all'inizio del file
        while (randomAccessFile.readByte() > '\n'){
            randomAccessFile.seek(randomAccessFile.getFilePointer() - 1);
            if (randomAccessFile.readByte() == '\n')    break;
        }
        String word = randomAccessFile.readLine();
        randomAccessFile.close();
        return word;
    }
}
