package server;

import com.google.gson.Gson;

import java.io.File;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.util.Random;

public class WorkerWord implements Runnable{
    Memory memory;
    Configuration configuration;
    Gson gson;

    private boolean stop = false;
    static volatile String wordGuess;

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

            if (wordGuess == null || timeElapsed >= (configuration.getTimeoutWord()* 60000)) {   //  Parsing del dizionario
                //  Parsing del dizionario
                fileName = "words.txt";
                absolutePath = configuration.setFileSeparator(fileName);
                memoryFile = new File(absolutePath);

                try {
                    wordGuess = extractWord(memoryFile);
                    memory.setFlag();
                    try(DatagramSocket socket = new DatagramSocket()){
                        InetAddress group = InetAddress.getByName("226.226.226.226");
                        String message = "Codice 103, Una nuova parola è stata generata ";

                        byte[] buffer = message.getBytes();

                        //  Dalla lunghezza del messaggio lo trasformo in una stringa e infine in un array di byte
                        String lengthMessage = String.valueOf(message.length());
                        byte[] size = lengthMessage.getBytes();

                        DatagramPacket sizePacket = new DatagramPacket(size, size.length, group, 5001);
                        socket.send(sizePacket);

                        DatagramPacket packet = new DatagramPacket(buffer, buffer.length, group, 5001);
                        socket.send(packet);

                    } catch (IOException e) {
                        throw new RuntimeException(e);
                    }
                } catch (IOException e) {
                    throw new RuntimeException(e);
                }
                System.out.println("La parola da indovinare è: " + wordGuess);

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