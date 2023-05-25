import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.channels.SocketChannel;

public class Utils {
    //  classe Utils che comprende le operazioni che sia il server che il client possono fare

    public static void write(String message, SocketChannel canale) throws IOException {
        ByteBuffer output = ByteBuffer.allocate(4 + message.getBytes().length);
        output.putInt(message.getBytes().length);   //  ci metto n+1
        output.put(message.getBytes());
        output.flip();   //  modalità scrittura
        while (output.hasRemaining())   canale.write(output);
    }

    public static String read(SocketChannel canale) throws IOException {
        ByteBuffer input = ByteBuffer.allocate(4);
        canale.read(input);
        input.flip();
        int length = input.getInt();
        input = ByteBuffer.allocate(length);
        while (input.hasRemaining())    canale.read(input);
        input.flip();
        return new String(input.array());
    }

    //  metodo per settare il file separator
    public static String setFileSeparator(String fileName){
        String os = System.getProperty("os.name").toLowerCase();
        String workingDir = System.getProperty("user.dir");
        String absolutePath;
        if (os.contains("win")) {
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
        return absolutePath;
    }
}
