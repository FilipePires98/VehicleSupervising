package common;

import java.io.DataOutputStream;
import java.io.IOException;
import java.net.Socket;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Custom Socket Client created to provide a simple and easy object to use when needing a socket client.
 * @author Filipe Pires (85122) and João Alegria (85048)
 */
public class SocketClient {
    
    private DataOutputStream out;
    private Socket socket=null;

    public SocketClient(String ip, int port) {
        try {
            while(this.socket==null){
                this.socket = new Socket(ip, port);
            }
            this.out = new DataOutputStream( socket.getOutputStream() );
            
        } catch (IOException ex) {
            Logger.getLogger(SocketClient.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
    
    /**
     * Sends a text message to the subscribed socket server. 
     * @param message string containing the message to send
     */
    public void send(String message){
        try {
            this.out.writeUTF(message);
            this.out.flush();
        } catch (IOException ex) {
            Logger.getLogger(SocketClient.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
    
    /**
     * Closes the socket client.
     */
    public void close(){
        try {
            this.out.close();
            this.socket.close();
        } catch (IOException ex) {
            Logger.getLogger(SocketClient.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
    
    
}
