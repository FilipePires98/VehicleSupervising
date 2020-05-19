/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package LoadBalancerTacticManager;

import common.MessageProcessor;
import common.SocketServer;
import entities.UiController;
import java.util.Arrays;

/**
 *
 * @author joaoalegria
 */
public class TacticManager implements MessageProcessor{
    
    private SocketServer server;
    private Thread serverThread;
    private ClusterInfo ci;
    
    public TacticManager(int port, String loadIp, int loadPort, UiController uc) {
        this.server = new SocketServer(port, this);
        this.serverThread = new Thread(server);
        this.serverThread.start();
        this.ci=new ClusterInfo(loadIp, loadPort, uc);
    }

    public void updateLoadBalancer(String ip, int port){
        this.ci.updateLoadBalancer(ip, port);
    }
    
    @Override
    public String processMessage(String message) {
        String[] p=message.split("-");
        if(p[0].equals("leastOccupiedServer")){//leastOcuppiedServer
            ServerInfo si = ci.leastOccupiedServer();
            return "serverInfo-"+si.getId()+"-"+si.getHost()+"-"+si.getPort();
        }
        else if(p[0].equals("clientInfo")){//clientInfo-clientId
            ClientInfo clientInfo=ci.getClient(Integer.valueOf(p[1]));
            return "clientInfo-"+clientInfo.getId()+"-"+clientInfo.getHost()+"-"+clientInfo.getPort();
        }
        else{
            UpdateStatus us = new UpdateStatus(message);
            Thread ust = new Thread(us);
            ust.start();
            return "Message porcessed with success.";
        }
    }
    
    public int getNewServerID() {
        return ci.getnewServerID();
    }
    
    public int getNewClientID() {
        return ci.getNewClientID();
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
