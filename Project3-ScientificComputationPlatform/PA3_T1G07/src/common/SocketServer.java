package common;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Wrapper developed to ease the implementation of socket server.
 * @author Filipe Pires (85122) and João Alegria (85048)
 */
public class SocketServer implements Runnable{
    
    /**
     * Port assigned to the server.
     */
    private int port;
    
    /**
     * Instance of the message processor assigned to the server.
     */
    private MessageProcessor mp;
    
    /**
     * Auxiliary flag signaling if the server must stop or not.
     */
    private boolean continueRunning;

    /**
     * Class constructor for the server definition.
     * @param port IP address assigned to the server.
     * @param mp Port assigned to the server.
     */
    public SocketServer(int port, MessageProcessor mp) {
        this.port = port;
        this.mp = mp;
        this.continueRunning = true;
    }
    
    /**
     * Executes the life-cycle of the socket server.
     * When receiving a new message the server passes the message to the respective message processor.
     */
    @Override
    public void run() {
        ServerSocket socket=null;
            
        try {
            socket = new ServerSocket(this.port);
            mp.setSocketStatus(1);
            while(continueRunning){
                Socket inSocket = socket.accept();
                Thread t=new Thread(new AttendClient(inSocket));
                t.start();
            }
            socket.close();

        } catch (IOException ex) {
            mp.setSocketStatus(-1);
            Logger.getLogger(SocketServer.class.getName()).log(Level.SEVERE, null, ex);
        }
            
    }
    
    /**
     * Auxiliary class with the objective of parralelizing the repsonses to the several clients. Work thread created for each client that connect to the server.
     */
    private class AttendClient implements Runnable{

        private Socket inSocket;

        public AttendClient(Socket inSocket){
            this.inSocket=inSocket;
        }
        
        /**
         * Lifecycle of the client responding worker thread.
         */
        @Override
        public void run() {
            try {
                DataInputStream socketInputStream = new DataInputStream(inSocket.getInputStream());
                DataOutputStream socketOutputStream = new DataOutputStream(inSocket.getOutputStream());
                String receivedMessage="a";
                while(!receivedMessage.equals("exit")){
                    receivedMessage=socketInputStream.readUTF();
                    System.out.println("Transmitted Message: "+receivedMessage);
                    String response = mp.processMessage(receivedMessage);
                    socketOutputStream.writeUTF(response);
                }
                inSocket.close();
                continueRunning=false;
            } catch (IOException ex) {
//                Logger.getLogger(SocketServer.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
        
    }
    
}
