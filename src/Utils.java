import java.nio.*;
import java.nio.channels.*;
import java.io.IOException;

public class Utils {
    //  classe Utils che comprende le operazioni che sia il server che il client possono fare

    public static void write(String message, SocketChannel canale) throws IOException {
        System.out.println("Sto scrivendo");
        ByteBuffer output = ByteBuffer.allocate(4 + message.getBytes().length);
        output.putInt(message.getBytes().length);   //  ci metto n+1
        output.put(message.getBytes());
        output.flip();   //  modalità scrittura
        while (output.hasRemaining())   canale.write(output);
    }

    public static String read(SocketChannel canale) throws IOException {
        System.out.println("Sto leggendo");
        ByteBuffer input = ByteBuffer.allocate(4);
        canale.read(input);
        input.flip();
        int length = input.getInt();
        input = ByteBuffer.allocate(length);
        while (input.hasRemaining())    canale.read(input);
        input.flip();
        return new String(input.array());
    }
}
