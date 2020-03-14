package common;

import java.io.DataInputStream;
import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * 
 * @author Filipe Pires (85122) and João Alegria (85048)
 */
public class SocketServer implements Runnable{
    
    private int port;
    private MessageProcessor mp;

    public SocketServer(int port, MessageProcessor mp) {
        this.port=port;
        this.mp = mp;
    }
    
    @Override
    public void run() {
        try {
            ServerSocket socket = new ServerSocket(this.port);
            Socket inSocket = socket.accept();
            DataInputStream socketInputStream = new DataInputStream(inSocket.getInputStream());
            String receivedMessage="a";
            while(!receivedMessage.equals("endSimulationOrder")){
                receivedMessage=socketInputStream.readUTF();
                System.out.println("Transmitted Message: "+receivedMessage);
                this.mp.processMessage(receivedMessage);
            }
            socket.close();
        } catch (IOException ex) {
            Logger.getLogger(SocketServer.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
    
}
