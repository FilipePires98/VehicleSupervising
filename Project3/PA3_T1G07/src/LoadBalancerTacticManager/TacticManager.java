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
import java.util.Collections;
import java.util.HashMap;
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
    private LoadBalancer lb;
    
    private List<ServerInfo> serversInfo;

    public TacticManager() {
        this.server = new SocketServer(6001, this);
        this.serverThread = new Thread(server);
        this.serverThread.start();
        this.serversInfo=new ArrayList();
    }
    
    public TacticManager(LoadBalancer lb) {
        this.server = new SocketServer(6001, this);
        this.serverThread = new Thread(server);
        this.serverThread.start();
        this.serversInfo=new ArrayList();
        this.lb=lb;
    }

    public void setLoadBalancer(LoadBalancer lb) {
        this.lb = lb;
    }

    
    @Override
    public void processMessage(String message) {
        String[] processed = message.trim().split("\\|");
        for(int i=0; i<processed.length;i++){
            processed[i]=processed[i].trim();
        }
        ServerInfo si=null;
        switch(processed[0]){
            case "newServer": // | newServer | serverId | serverPort |
                this.serversInfo.add(new ServerInfo(Integer.valueOf(processed[1]), Integer.valueOf(processed[2])));
                break;
            case "serverDown": // | serverDown | serverId |
                si=null;
                for(ServerInfo tmp:serversInfo){
                    if(tmp.getId()==Integer.valueOf(processed[1])){
                        si=tmp;
                        break;
                    }
                }
                removeAndRedistributeMessages(si);
                break;
            case "newRequest": // | newRequest | serverId | request |
                si=null;
                for(ServerInfo tmp:serversInfo){
                    if(tmp.getId()==Integer.valueOf(processed[1])){
                        si=tmp;
                        break;
                    }
                }
                si.addRequest(processed[2]);
                break;
            case "processedRequest": // | processedRequest | serverId | request |
                si=null;
                for(ServerInfo tmp:serversInfo){
                    if(tmp.getId()==Integer.valueOf(processed[1])){
                        si=tmp;
                        break;
                    }
                }
                si.removeRequest(processed[2]);
                break;
        }
    }
    
    public void removeAndRedistributeMessages(ServerInfo si){
        List<String> messages=new ArrayList();
        if(si.getRequests().size()>0){
            for(RequestInfo ri : si.getRequests()){
                messages.add(ri.getRequest());
            }
        }
        serversInfo.remove(si);
        lb.distributeLoad(messages);
    }
    
    public ServerInfo leastOccupiedServer(){
        Collections.sort(serversInfo);
        return serversInfo.get(0);
    }
    
    
    private class HealthCheck implements Runnable{

        @Override
        public void run() {
            try {
                Thread.sleep(30000);
            } catch (InterruptedException ex) {
                Logger.getLogger(TacticManager.class.getName()).log(Level.SEVERE, null, ex);
            }
            for(ServerInfo si : serversInfo){
                SocketClient client=new SocketClient("localhost", si.getPort());
                try {
                    client.send("| healthcheck |");
                } catch (IOException ex) {
                    removeAndRedistributeMessages(si);
                }
                client.close();
            }
            
            
        }
        
    }
    
}
