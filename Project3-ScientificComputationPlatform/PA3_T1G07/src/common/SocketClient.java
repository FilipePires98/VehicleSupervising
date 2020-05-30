package common;

import java.io.DataInputStream;
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
    
    /**
     * Output stream of the socket.
     */
    private DataOutputStream out;
    private DataInputStream in;
    
    /**
     * Instance of the communication socket to be used.
     */
    private Socket socket=null;
    private final String ip;
    private final int port;

    /**
     * Class constructor for the client definition.
     * @param ip IP address assigned to the client.
     * @param port Port assigned to the client.
     */
    public SocketClient(String ip, int port) {
        this.ip=ip;
        this.port=port;
    }
    
    /**
     * Sends a text message to the subscribed socket server. 
     * @param message string containing the message to send.
     * @return response message (acknowledge).
     * @throws IOException socket-related error (most likely failure in socket creation).
     */
    public String send(String message) throws IOException{
        if(this.socket==null){
            this.socket = new Socket(ip, port);
            this.out = new DataOutputStream( socket.getOutputStream() );
            this.in = new DataInputStream( socket.getInputStream() );
        }
        this.out.writeUTF(message);
        this.out.flush();
        return this.in.readUTF();
    }
    
    /**
     * Closes the socket client.
     */
    public void close(){
        try {
            this.out.close();
            this.socket.close();
        } catch (IOException ex) {
//            Logger.getLogger(SocketClient.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
    
    
}
