package LoadBalancerTacticManager;

import common.SocketClient;
import entities.UiController;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.locks.ReentrantLock;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Monitor storing all the critical cluster info.
 * @author Filipe Pires (85122) and João Alegria (85048)
 */
public class ClusterInfo {
    
    private final Map<Integer,ServerInfo> serverInfo;
    private final Map<Integer,ClientInfo> clientInfo;
    private final List<String> processing;
    private final List<String> processed;
    
    private final ReentrantLock rl;
    private final HealthCheck healthChecker;
    private final Thread healthCheckerThread;
    
    private UiController uc;
    
    private SocketClient loadBalancer;
    private String balancerIp;
    private int balancerPort;

    /**
     * Creates instance of the cluster information.
     * @param balancerIp LoadBalancer's host IP address
     * @param balancerPort LoadBalancer's port
     * @param uc reference to the UI controller (the GUI process)
     */
    public ClusterInfo(String balancerIp, int balancerPort, UiController uc) {
        this.uc=uc;
        this.serverInfo=new HashMap();
        this.clientInfo=new HashMap();
        this.processing=new ArrayList();
        this.processed=new ArrayList();
        this.rl = new ReentrantLock();
        this.healthChecker=new HealthCheck();
        this.healthCheckerThread=new Thread(this.healthChecker);
        this.healthCheckerThread.start();
        this.balancerIp=balancerIp;
        this.balancerPort=balancerPort;
        this.loadBalancer=new SocketClient(balancerIp, balancerPort);
    }
    
    /**
     * Updates the internal Load Balancing host ip and port.
     * @param ip String representing the host ip of the machine running LB
     * @param port int representing the port of the process running the LB.
     */
    public void updateLoadBalancer(String ip, int port){
        this.balancerIp=ip;
        this.balancerPort=port;
        this.loadBalancer.close();
        this.loadBalancer=new SocketClient(ip, port);
    }

    /**
     * Calculates and return the least occupied server at the moment.
     * @return ServerInfo containing all the metadata of the server in question.
     */
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
    
    /**
     * Adds a new client to the centered cluster information database.
     * @param host String representing the host ip of the machine running the client.
     * @param port int representing the port of the process running the client.
     */
    public void addClient(String host, int port){
        rl.lock();
        int id=getNewClientId();
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
    
    /**
     * Returns a server id not existing in the current database.
     * @return int with the new server id.
     */
    public int getNewServerId() {
        if(serverInfo.keySet().isEmpty()){
            return 0;
        }
        List<Integer> serverIds=new ArrayList(serverInfo.keySet());
        Collections.sort(serverIds);
        return serverIds.get(serverIds.size()-1)+1;
    }
    
    /**
     * Returns a client id not existing in the current database.
     * @return int with the new client id.
     */
    public int getNewClientId() {
        if(clientInfo.keySet().isEmpty()){
            return 0;
        }
        List<Integer> clientIds=new ArrayList(clientInfo.keySet());
        Collections.sort(clientIds);
        return clientIds.get(clientIds.size()-1)+1;
    }
    
    /**
     * Removes a certain client from the current database.
     * @param id int representing the id of the client to be removed.
     */
    public void removeClient(int id){
        rl.lock();
        clientInfo.remove(id);
        updateClients();
        rl.unlock();
    }
    
    /**
     * Adds a new server to the centered cluster information database.
     * @param host String representing the host ip of the machine running the server.
     * @param port int representing the port of the process running the server.
     */
    public void addServer(String host, int port){
        rl.lock();
        int id=getNewServerId();
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
    
    /**
     * Removes a certain server from the current database.
     * @param id int representing the id of the server to be removed.
     */
    public void removeServer(int id){
        rl.lock();
        ServerInfo si=serverInfo.get(id);
        List<String> messages=si.getRequests();
        serverInfo.remove(id);
        if(messages.size()>0){
            String messageToLoadBalancer="sendMessages";
            for(String msg : messages){
                messageToLoadBalancer+="-"+msg;
            }
            try {
                this.loadBalancer.send(messageToLoadBalancer);
            } catch (IOException ex) {
                Logger.getLogger(ClusterInfo.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
        updateServers();
        rl.unlock();
    }
    
    /**
     * Registers a new request to be processed in the centered cluster information database.
     * @param id int representing the id of the server in which the request will be processed.
     * @param request String containing the request itself.
     */
    public void addRequest(int id, String request){
        rl.lock();
        serverInfo.get(id).addRequest(request);
        processing.add(request+"  |  Server: "+id);
        updateProcessingRequests();
        updateServers();
        rl.unlock();
    }
    
    /**
     * Removes the processed request from cluster information database.
     * @param id int representing the id of the server in which the request was processed.
     * @param request String containing the request itself.
     */
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
    
    /**
     * Returns the client associated with the given id.
     * @param id int representing the id of the intended client
     * @return ClientInfo containing all the metadata associated with the client
     */
    public ClientInfo getClient(int id){
        return clientInfo.get(id);
    }
    
    /**
     * Updates the server list of the associated GUI.
     */
    private void updateServers(){
        List<String> servers=new ArrayList();
        for(ServerInfo si : serverInfo.values()){
            servers.add("Occupation: "+si.getRequests().size() +" | "+si.toString());
        }
        String[] tmp=new String[servers.size()];
        servers.toArray(tmp);
        uc.defineUpServers(tmp);
    }
    
    /**
     * Updates the client list of the associated GUI.
     */
    private void updateClients(){
        List<String> clients=new ArrayList();
        for(ClientInfo ci : clientInfo.values()){
            clients.add(ci.toString());
        }
        String[] tmp=new String[clients.size()];
        clients.toArray(tmp);
        uc.defineClients(tmp);
    }
    
    /**
     * Updates the processing requests list of the associated GUI.
     */
    private void updateProcessingRequests(){
        String[] tmp=new String[processing.size()];
        processing.toArray(tmp);
        uc.addProcessingMessage(tmp);
    }
    
    /**
     * Updates the processed requests list of the associated GUI.
     */
    private void updateProcessedRequests(){
        List<String> clients=new ArrayList();
        for(ClientInfo ci : clientInfo.values()){
            clients.add("Client: "+ci.getId());
        }
        String[] tmp=new String[processed.size()];
        processed.toArray(tmp);
        uc.addProcessedMessage(tmp);
    }
    
    /**
     * Auxiliary class with the objective of running the server's healthcheck background process.
     */
    private class HealthCheck implements Runnable{

        /**
         * Lifecycle of healthcheck worker.
         */
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
