package LoadBalancerTacticManager;

import common.MessageProcessor;
import common.SocketServer;
import entities.UiController;

/**
 * Entity containing all the Tactic Manager logic. The objective of the Tactic Manager is gather all the cluster information and supply that crucial information if necessary. 
 * It is also this entity that enables the existence of a centralized and consistence source of the cluster status at all times.
 * @author Filipe Pires (85122) and João Alegria (85048)
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

    /**
     * Updates the internal Load Balancing host ip and port.
     * @param ip String representing the host ip of the machine running LB
     * @param port int representing the port of the process running the LB.
     */
    public void updateLoadBalancer(String ip, int port){
        this.ci.updateLoadBalancer(ip, port);
    }
    
    /**
     * Establishes the logic to use when processing incoming messages.
     * @param message String containing the incoming message
     * @return String containing the acknowledge message intended to be returned to the message sender 
     */
    @Override
    public String processMessage(String message) {
        String[] p=message.split("-");
        if(p[0].equals("leastOccupiedServer")){//leastOcuppiedServer
            ServerInfo si = ci.leastOccupiedServer();
            if(si.getId()==-1){
                return "serverInfo-none-none-none";
            }else{
                return "serverInfo-"+si.getId()+"-"+si.getHost()+"-"+si.getPort();
            }
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
    
    /**
     * Updates the current server socket status.
     * @param socketStatus int representing the status of the socket.
     */
    @Override
    public void setSocketStatus(int socketStatus) {
        // does nothing
    }
    
    /**
     * Returns a server id that doesn't exist in the database. This is so a new server can be created.
     * @return int representing the new server id.
     */
    public int getNewServerID() {
        return ci.getNewServerId();
    }
    
    /**
     * Returns a client id that doesn't exist in the database. This is so a new client can be created.
     * @return int representing the new client id.
     */
    public int getNewClientID() {
        return ci.getNewClientId();
    }
    
    /**
     * Auxiliary class created to parallelize the processing of some incoming messages.
     */
    private class UpdateStatus implements Runnable{
        
        private String[] processed;
        private String message;

        public UpdateStatus(String message) {
            this.message=message;
            this.processed = message.trim().split("-");
        }

        /**
         * Lifecycle of the message processing worker worker thread.
         */
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
