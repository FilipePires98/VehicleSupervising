/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package LoadBalancerTacticManager;

import common.SocketClient;
import entities.UiController;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.locks.Condition;
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
    private final List<String> processing;
    private final List<String> processed;
    
    private final ReentrantLock rl;
    private Condition serversUp;
    private final HealthCheck healthChecker;
    private final Thread healthCheckerThread;
    
    private UiController uc;
    
    private SocketClient loadBalancer;
    private String balancerIp;
    private int balancerPort;

    public ClusterInfo(String balancerIp, int balancerPort, UiController uc) {
        this.uc=uc;
        this.serverInfo=new HashMap();
        this.clientInfo=new HashMap();
        this.processing=new ArrayList();
        this.processed=new ArrayList();
        this.rl = new ReentrantLock();
        this.serversUp = rl.newCondition();
        this.healthChecker=new HealthCheck();
        this.healthCheckerThread=new Thread(this.healthChecker);
        this.healthCheckerThread.start();
        this.balancerIp=balancerIp;
        this.balancerPort=balancerPort;
        this.loadBalancer=new SocketClient(balancerIp, balancerPort);
    }
    
    public void updateLoadBalancer(String ip, int port){
        this.balancerIp=ip;
        this.balancerPort=port;
        this.loadBalancer.close();
        this.loadBalancer=new SocketClient(ip, port);
    }

    public ServerInfo leastOccupiedServer() {
        rl.lock();
        List<ServerInfo> servers = new ArrayList(serverInfo.values());
        Collections.sort(servers);
        rl.unlock();
        if(servers.size()>0){
            return servers.get(0);
        }
        else{
            return new ServerInfo(-1,"none",-1);
        }
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
        updateClients();
        rl.unlock();
    }
    
    public int getnewServerID() {
        int id=0;
        while(serverInfo.keySet().contains(id)){
            id++;
        }
        return id;
    }
    
    public int getNewClientID() {
        int id=0;
        while(clientInfo.keySet().contains(id)){
            id++;
        }
        return id;
    }
    
    public void removeClient(int id){
        rl.lock();
        clientInfo.remove(id);
        updateClients();
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
        updateServers();
        rl.unlock();
    }
    
    public void removeServer(int id){
        rl.lock();
        ServerInfo si=serverInfo.get(id);
        List<String> messages=si.getRequests();
        serverInfo.remove(id);
        String messageToLoadBalancer="sendMessages";
        for(String msg : messages){
            messageToLoadBalancer+="-"+msg;
        }
        try {
            this.loadBalancer.send(messageToLoadBalancer);
        } catch (IOException ex) {
            Logger.getLogger(ClusterInfo.class.getName()).log(Level.SEVERE, null, ex);
        }
        updateServers();
        rl.unlock();
    }
    
    public void addRequest(int id, String request){
        rl.lock();
        serverInfo.get(id).addRequest(request);
        processing.add(request+"  |  Server: "+id);
        updateProcessingRequests();
        updateServers();
        rl.unlock();
    }
    
    public void removeRequest(int id, String request){
        rl.lock();
        serverInfo.get(id).removeRequest(request);
        processing.remove(request+"  |  Server: "+id);
        processed.add(request+"  |  Server: "+id);
        updateProcessingRequests();
        updateProcessedRequests();
        updateServers();
        rl.unlock();
    }
    
    public ClientInfo getClient(int id){
        return clientInfo.get(id);
    }
    
    private void updateServers(){
        List<String> servers=new ArrayList();
        for(ServerInfo si : serverInfo.values()){
            servers.add("Server: "+si.getId()+"  |  Occupation: "+si.getRequests().size());
        }
        String[] tmp=new String[servers.size()];
        servers.toArray(tmp);
        uc.defineUpServers(tmp);
    }
    
    private void updateClients(){
        List<String> clients=new ArrayList();
        for(ClientInfo ci : clientInfo.values()){
            clients.add("Client: "+ci.getId());
        }
        String[] tmp=new String[clients.size()];
        clients.toArray(tmp);
        uc.defineClients(tmp);
    }
    
    private void updateProcessingRequests(){
        String[] tmp=new String[processing.size()];
        processing.toArray(tmp);
        uc.addProcessingMessage(tmp);
    }
    
    private void updateProcessedRequests(){
        List<String> clients=new ArrayList();
        for(ClientInfo ci : clientInfo.values()){
            clients.add("Client: "+ci.getId());
        }
        String[] tmp=new String[processed.size()];
        processed.toArray(tmp);
        uc.addProcessedMessage(tmp);
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
                    try {
                        SocketClient client=new SocketClient(si.getHost(), si.getPort());
                        client.send("healthcheck");
                        client.close();
                    } catch (IOException ex) {
                        removeServer(si.getId());
                    }
                }
            }
        }
        
    }
}
