/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package common;

import java.io.DataOutputStream;
import java.io.IOException;
import java.net.Socket;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author joaoalegria
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
    
    public void send(String message){
        try {
            this.out.writeUTF(message);
            this.out.flush();
        } catch (IOException ex) {
            Logger.getLogger(SocketClient.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
    
    public void close(){
        try {
            this.out.close();
            this.socket.close();
        } catch (IOException ex) {
            Logger.getLogger(SocketClient.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
    
    
}
