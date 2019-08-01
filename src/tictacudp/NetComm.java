package tictacudp;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetSocketAddress;
import java.net.UnknownHostException;
import javax.swing.JOptionPane;

public class NetComm {

    public static byte[] recvBuffer;
    public static DatagramPacket recvPacket;
    public static DatagramSocket dataSocket;
    public static String receiveString;

    private static void setUp() throws IOException {
        recvBuffer = new byte[1024];
        recvPacket = new DatagramPacket(recvBuffer, recvBuffer.length);
    }

    public static void setUpServer() throws IOException {
        setUp();
        dataSocket = new DatagramSocket(50000);
        dataSocket.receive(recvPacket);
        dataSocket.connect(recvPacket.getSocketAddress());

        GameGlobals.isServer = true;
        System.out.println("Server Is Connected");
	
        
    }

    public static void setUpClient() throws UnknownHostException, IOException {
        setUp();
        dataSocket = new DatagramSocket();
    String ip = JOptionPane.showInputDialog("Please enter a server "
            + "IP address", "127.0.0.1");
        InetSocketAddress serverSocket = new InetSocketAddress(ip,
                50000);
        dataSocket.connect(serverSocket);
        String connectString = "hello";
        byte[] sendBuffer = connectString.getBytes();
        DatagramPacket sendPacket = new DatagramPacket(sendBuffer,
                sendBuffer.length);
        dataSocket.send(sendPacket);

        GameGlobals.isServer = false;
        System.out.println("Client Is Connected");
        //GameGlobals.gameStatusLabel.setText("Client Is Connected");
    }
}