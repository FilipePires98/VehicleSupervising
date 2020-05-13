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
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author joaoalegria
 */
public class TacticManager implements MessageProcessor{
    
    private SocketServer server;
    private Thread serverThread;
    private ClusterInfo ci;
    
    public TacticManager(ClusterInfo ci) {
        this.server = new SocketServer(6001, this);
        this.serverThread = new Thread(server);
        this.serverThread.start();
        this.ci=ci;
    }

    
    @Override
    public void processMessage(String message) {
        UpdateStatus us = new UpdateStatus(message);
        Thread ust = new Thread(us);
        ust.start();
        
    }
    
    private class UpdateStatus implements Runnable{
        
        private String[] processed;
        private String message;

        public UpdateStatus(String message) {
            this.message=message;
            this.processed = message.trim().split("-");
        }

        @Override
        public void run() {
            ServerInfo si=null;
            switch(processed[0]){
                case "newClient": // newClient-clientHost-clientPort
                    ci.addClient(processed[1], Integer.valueOf(processed[2]));
                    break;
                case "clientDown": // clientDown-clientId
                    ci.removeClient(Integer.valueOf(processed[1]));
                    break;
                case "newServer": // newServer-serverHost-serverPort
                    ci.addServer(processed[1], Integer.valueOf(processed[2]));
                    break;
                case "serverDown": // serverDown-serverId
                    ci.removeServer(Integer.valueOf(processed[1]));
                    break;
                case "newRequest": // newRequest-serverId-request
                    ci.addRequest(Integer.valueOf(processed[1]),processed[2]);
                    break;
                case "processedRequest": // processedRequest-serverId-request
                    ci.removeRequest(Integer.valueOf(processed[1]),processed[2]);
                    break;
            }
        }
        
    }
}
