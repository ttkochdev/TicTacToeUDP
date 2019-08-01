package tictacudp;

import java.io.IOException;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.BlockingQueue;
import javax.swing.JFrame;
import javax.swing.JOptionPane;
import javax.swing.Timer;

/**
 * @author Tyler 
 * Received some assistance per conversation from Ryan Hilsabeck, Jason Kryst, 
 * and Travis Williams. Project turned in 2/19/2013
 * CSC 569 Professor Muganda 
 * Project Built in NetBeans IDE 7.2.1
 */
public class TicTacUDP {

    public static void main(String[] args) throws IOException {
        JFrame frame = new TTTFrame();
        // Find out if should be a server or  a client
        String[] possibleValues = {
            "Tic Tac Toe Server", "Tic Tac Toe Client"
        };
        String selectedValue = (String) JOptionPane.showInputDialog(frame,
                "Select a Tic Tac Toe Role", "CSC 469/569 Tic Tac Toe",
                JOptionPane.INFORMATION_MESSAGE, null,
                possibleValues, possibleValues[0]);
        System.out.println("You selected " + selectedValue);
        //System.out.println(selectedValue.contains("Server"));

        if (selectedValue.contains("Server")) {
            frame.setTitle(frame.getTitle() + " " + "Server");
            NetComm.setUpServer();
        } else {
            frame.setTitle(frame.getTitle() + " " + "Client");
            NetComm.setUpClient();
        }
        // This timer is used to periodically check for network input
        //static Timer netInputMonitorTimer = new Timer(100, new NetInputListener());

        BlockingQueue<String> protQueue = new ArrayBlockingQueue<>(100);
        SocketThreads th1 = new SocketThreads(NetComm.dataSocket, protQueue);
        th1.start();

        Timer netInputMonitorTimer = new Timer(100, new NetInputListener(protQueue));
        netInputMonitorTimer.start();
        //System.out.println(netInputMonitorTimer);
        //stop when game is  over
        GameGlobals.init();
    }
}
