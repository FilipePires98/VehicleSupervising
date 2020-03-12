package common;

import cc.CCMessageProcessor;
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
public class SynchronizedSocketServer implements Runnable{
    
    private int port;
    private CCMessageProcessor messageProcessor;

    public SynchronizedSocketServer(int port, CCMessageProcessor messageProcessor) {
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
                this.messageProcessor.run();
            }
            socket.close();
        } catch (IOException ex) {
            Logger.getLogger(SynchronizedSocketServer.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
    
}
