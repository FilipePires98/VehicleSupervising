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

/**
 *
 * @author joaoalegria
 */
public class LoadBalancer implements MessageProcessor{
    
    private SocketServer server;
    private Thread serverThread;
    private ClusterInfo ci;
    
    public LoadBalancer(ClusterInfo ci) {
        this.server = new SocketServer(6000, this);
        this.serverThread = new Thread(server);
        this.serverThread.start();
        this.ci=ci;
    }

    public void setClusterInfo(ClusterInfo ci) {
        this.ci=ci;
    }

    @Override
    public void processMessage(String message) {
        LoadDistributor ld = new LoadDistributor(ci,(List<String>)Arrays.asList(message));
        Thread ldt = new Thread(ld);
        ldt.start();
    }
    
}
