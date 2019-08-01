package tictacudp;

import java.io.IOException;
import java.net.DatagramPacket;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author tyler
 */
public class Packet {

    String data;

    public Packet(String data) {
        this.data = data;
    }

    public static void sendPacket(String data) {
        byte[] sendBuffer = data.getBytes();
        DatagramPacket outPacket = new DatagramPacket(sendBuffer, sendBuffer.length);
        //System.out.println("NetComm.recvPacket(in CreatePacket): "+outPacket);
        try {
            NetComm.dataSocket.send(outPacket);
            //System.out.println("send data in CreatePacket class");
        } catch (IOException ex) {
            Logger.getLogger(Packet.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
}