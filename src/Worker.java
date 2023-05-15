import java.nio.channels.SocketChannel;

public class Worker implements Runnable{
    private String stringa;
    public Worker(String stringa, Memory memory, SocketChannel client) {
        this.stringa = stringa;
    }

    @Override
    public void run() {
        // metto tutto quello che thread deve fare
        System.out.println("Sono il thread " + Thread.currentThread().getName() + " e ho ricevuto la stringa " + stringa);
    }
}
