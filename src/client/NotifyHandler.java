package client;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.InetAddress;
import java.net.MulticastSocket;
import java.net.UnknownHostException;
import java.util.Vector;

public class NotifyHandler implements Runnable{
    private Vector<String> games;
    MulticastSocket multicastSocket;

    public NotifyHandler(int UDPPort, String multicastAddress, Vector<String> games) {
        this.games = games;
        InetAddress group;
        try {
            group = InetAddress.getByName(multicastAddress);
        } catch (UnknownHostException e) {
            throw new RuntimeException(e);
        }
        try {
            this.multicastSocket = new MulticastSocket(UDPPort);
            multicastSocket.joinGroup(group);
            multicastSocket.setReuseAddress(true);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

    }
    @Override
    public void run() {
        while (true) {
            try {
                byte[] bufferLenght = new byte[4];
                DatagramPacket datagramPacketLenght = new DatagramPacket(bufferLenght, bufferLenght.length);
                multicastSocket.receive(datagramPacketLenght);    // ricevo la lunghezza del messaggio
                String lengthMessage = new String(datagramPacketLenght.getData(), datagramPacketLenght.getOffset(), datagramPacketLenght.getLength());
                int length = Integer.parseInt(lengthMessage);

                byte[] buffer = new byte[length];
                DatagramPacket datagramPacket = new DatagramPacket(buffer, buffer.length);
                multicastSocket.receive(datagramPacket);    // ricevo il messaggio
                String message = new String(datagramPacket.getData(), datagramPacketLenght.getOffset(), datagramPacket.getLength());
                games.add(message);

                if (games.size() >= 100) {
                    games.remove(0);
                }
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        }
    }
}
