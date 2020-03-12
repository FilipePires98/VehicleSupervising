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
public class UnsynchronizedSocketServer implements Runnable{
    
    private int port;
    private MessageProcessor messageProcessor;

    public UnsynchronizedSocketServer(int port, MessageProcessor messageProcessor) {
        this.port=port;
        this.messageProcessor = messageProcessor;
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
                System.out.println(receivedMessage);
                this.messageProcessor.defineMessage(receivedMessage);
                new Thread(messageProcessor).start();
            }
            socket.close();
        } catch (IOException ex) {
            Logger.getLogger(UnsynchronizedSocketServer.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
    
}
