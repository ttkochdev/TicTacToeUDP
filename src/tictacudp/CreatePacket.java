package tictacudp;

import java.io.IOException;
import java.net.DatagramPacket;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author tyler
 */

public class CreatePacket{
	String rawData;
	public CreatePacket(String rawData){
		this.rawData = rawData;
	}
	public static void sendPacket(String rawData){
		byte[] sendBuffer = rawData.getBytes();
		NetComm.recvPacket = new DatagramPacket (sendBuffer, sendBuffer.length);
                System.out.println("NetComm.recvPacket(in CreatePacket): "+NetComm.recvPacket);
        try {
            NetComm.dataSocket.send(NetComm.recvPacket);
            System.out.println("send data in CreatePacket class");
        } catch (IOException ex) {
            Logger.getLogger(CreatePacket.class.getName()).log(Level.SEVERE, null, ex);
        }
		
	}
}