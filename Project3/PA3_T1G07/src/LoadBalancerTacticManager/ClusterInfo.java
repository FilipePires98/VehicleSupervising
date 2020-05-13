/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package LoadBalancerTacticManager;

import common.SocketClient;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.locks.ReentrantLock;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author joaoalegria
 */
public class ClusterInfo {
    
    private final Map<Integer,ServerInfo> serverInfo;
    private final Map<Integer,ClientInfo> clientInfo;
    
    private final ReentrantLock rl;
    private final HealthCheck healthChecker;
    private final Thread healthCheckerThread;

    public ClusterInfo() {
        this.serverInfo=new HashMap();
        this.clientInfo=new HashMap();
        this.rl = new ReentrantLock();
        this.healthChecker=new HealthCheck();
        this.healthCheckerThread=new Thread(this.healthChecker);
        this.healthCheckerThread.start();
    }
    
    

    public ServerInfo leastOccupiedServer() {
        return null;
    }
    
    public void addClient(String host, int port){
        rl.lock();
        int id=0;
        while(clientInfo.keySet().contains(id)){
            id++;
        }
        ClientInfo ci = new ClientInfo(id, host, port);
        clientInfo.put(id, ci);
        SocketClient client=new SocketClient(host, port);
        try {
            client.send("clientId-"+id);
        } catch (IOException ex) {
            removeClient(id);
        }
        client.close();
        rl.unlock();
    }
    
    public void removeClient(int id){
        rl.lock();
        clientInfo.remove(id);
        rl.unlock();
    }
    
    public void addServer(String host, int port){
        rl.lock();

        int id=0;
        while(serverInfo.keySet().contains(id)){
            id++;
        }
        ServerInfo si = new ServerInfo(id, host, port);
        serverInfo.put(id, si);
        SocketClient client=new SocketClient(host, port);
        try {
            client.send("serverId-"+id);
        } catch (IOException ex) {
            removeServer(id);
        }
        client.close();
        rl.unlock();
    }
    
    public void removeServer(int id){
        rl.lock();
        ServerInfo si=serverInfo.get(id);
        List<String> messages=si.getRequests();
        serverInfo.remove(id);
        LoadDistributor ld = new LoadDistributor(this,messages);
        Thread ldt = new Thread(ld);
        ldt.start();
        rl.unlock();
    }
    
    public void addRequest(int id, String request){
        rl.lock();
        serverInfo.get(id).addRequest(request);
        rl.unlock();
    }
    
    public void removeRequest(int id, String request){
        rl.lock();
        serverInfo.get(id).removeRequest(request);
        rl.unlock();
    }
    
    public ClientInfo getClient(int id){
        return clientInfo.get(id);
    }
    
    
    private class HealthCheck implements Runnable{

        @Override
        public void run() {
            while(true){
                try {
                    Thread.sleep(30000);
                } catch (InterruptedException ex) {
                    Logger.getLogger(TacticManager.class.getName()).log(Level.SEVERE, null, ex);
                }
                for(ServerInfo si : serverInfo.values()){
                    SocketClient client=new SocketClient(si.getHost(), si.getPort());
                    try {
                        client.send("healthcheck");
                    } catch (IOException ex) {
                        removeServer(si.getId());
                    }
                    client.close();
                }
            }
        }
        
    }
}
