/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package LoadBalancerTacticManager;

import common.MessageProcessor;
import common.SocketClient;
import common.SocketServer;
import java.io.IOException;
import java.util.Arrays;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author joaoalegria
 */
public class LoadBalancer implements MessageProcessor{
    
    private SocketServer server;
    private Thread serverThread;
    private TacticManager tm;

    public LoadBalancer() {
        this.server = new SocketServer(6000, this);
        this.serverThread = new Thread(server);
        this.serverThread.start();
    }
    
    public LoadBalancer(TacticManager tm) {
        this.server = new SocketServer(6000, this);
        this.serverThread = new Thread(server);
        this.serverThread.start();
        this.tm=tm;
    }

    public void setTacticManager(TacticManager tm) {
        this.tm = tm;
    }

    @Override
    public void processMessage(String message) {
        distributeLoad((List<String>)Arrays.asList(message));
    }

    public void distributeLoad(List<String> messages){
        ServerInfo si=tm.leastOccupiedServer();
        SocketClient client=new SocketClient("localhost", si.getPort());
        try {
            for(String msg:messages){
                client.send(msg);
            }
        } catch (IOException ex) {
            tm.removeAndRedistributeMessages(si);
        }
        client.close();
    }
    
}
