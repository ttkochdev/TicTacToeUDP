package tictacudp;

import java.io.IOException;
import java.net.DatagramSocket;
import java.util.concurrent.BlockingQueue;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author tyler
 */
public class SocketThreads extends Thread {

  DatagramSocket sock;
  BlockingQueue <String> protQueue;


  public SocketThreads(DatagramSocket sock, BlockingQueue<String> protQueue) {
    this.protQueue = protQueue;
    this.sock = sock;
  }

  @Override
  public void run() {
    while (true) {
          try {
              NetComm.dataSocket.receive(NetComm.recvPacket);
          } catch (IOException ex) {
              Logger.getLogger(SocketThreads.class.getName()).log(Level.SEVERE, null, ex);
          }
           NetComm.receiveString = new String(NetComm.recvPacket.getData(), 0,
                NetComm.recvPacket.getLength());
           //System.out.println("NetComm.receiveString: "+NetComm.receiveString);
          try {
              protQueue.put(NetComm.receiveString);
              
              //System.out.println();
          } catch (InterruptedException ex) {
              Logger.getLogger(SocketThreads.class.getName()).log(Level.SEVERE, null, ex);
          }
      
    }
  }
}

