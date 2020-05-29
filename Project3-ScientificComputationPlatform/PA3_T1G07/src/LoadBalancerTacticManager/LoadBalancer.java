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
 * Entity containing all the Load Balancer logic. The objective of the Load Balancer is simply to distribute in a balanced way all the incoming requests.
 * @author Filipe Pires (85122) and João Alegria (85048)
 */
public class LoadBalancer implements MessageProcessor{
    
    private SocketServer server;
    private Thread serverThread;
    private String monitorIp;
    private int monitorPort;
    private SocketClient monitorClient;
    
    public LoadBalancer(int port, String monitorIp, int monitorPort) {
        this.monitorIp=monitorIp;
        this.monitorPort=monitorPort;
        this.server = new SocketServer(port, this);
        this.serverThread = new Thread(server);
        this.serverThread.start();
        this.monitorClient=new SocketClient(monitorIp, monitorPort);
    }
    
    /**
     * Updates the internal Tactic Manager host ip and port.
     * @param endpoint String representing the host ip of the machine running TM.
     * @param port int representing the port of the process running the TM.
     */
    public void updateTacticMonitor(String endpoint, int port){
        this.monitorIp=endpoint;
        this.monitorPort=port;
        this.monitorClient.close();
        this.monitorClient=new SocketClient(endpoint, port);
    }

    /**
     * Establishes the logic to use when processing incoming messages.
     * @param message String containing the incoming message
     * @return String containing the acknowledge message intended to be returned to the message sender 
     */
    @Override
    public String processMessage(String message) {
        String[] processed = message.split("-");
        
        if(processed.length==1){
            LoadDistributor ld = new LoadDistributor((List<String>)Arrays.asList(message));
            Thread ldt = new Thread(ld);
            ldt.start();
        }else{
            switch(processed[0]){
                case "newClient":// newClient-clientHost-clientPort
                    try {
                        monitorClient.send("newClient-"+processed[1]+"-"+processed[2]);
                    } catch (IOException ex) {
                        Logger.getLogger(LoadBalancer.class.getName()).log(Level.SEVERE, null, ex);
                    }
                    break;

                case "clientDown": // clientDown-clientId
                    try {
                        monitorClient.send("clientDown-"+processed[1]);
                    } catch (IOException ex) {
                        Logger.getLogger(LoadBalancer.class.getName()).log(Level.SEVERE, null, ex);
                    }
                    break;

                case "sendMessages": //sendMessages-messages(separated by '-')
                    LoadDistributor ld = new LoadDistributor((List<String>)Arrays.asList(Arrays.copyOfRange(processed, 1, processed.length)));
                    Thread ldt = new Thread(ld);
                    ldt.start();
                    break;
            }
        }
        return "Message processed with success.";
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
     * Auxiliary class created to process  every incoming message(s) in a parallel way. A worker thread is created for each incoming message(s). 
     */
    public class LoadDistributor implements Runnable{
        
        private List<String> messages;
        private SocketClient myClient;

        public LoadDistributor(List<String> messages) {
            this.messages = messages;
            this.myClient=new SocketClient(monitorIp, monitorPort);
        }

        /**
         * Lifecycle of the distributing worker thread.
         */
        @Override
        public void run() {
            try {
                for(String msg:messages){
                    String[] server = this.myClient.send("leastOccupiedServer").split("-"); //serverInfo-serverId-serverIp-serverPort
                    if(server[1].equals("none")){

                            String[] tmp=msg.replaceAll("\\s+","").split("\\|");
                            String[] processed = Arrays.copyOfRange(tmp, 1, tmp.length);
                            String[] client = this.myClient.send("clientInfo-"+processed[0]).split("-");//clientInfo-clientId-clientIp-clientPort
                            SocketClient clientSocketClient=new SocketClient(client[2], Integer.valueOf(client[3]));
                            clientSocketClient.send("| None | "+processed[0]+" | "+processed[1]+" | Server unavailable.");
                            clientSocketClient.close();
                    }
                    else{
                        SocketClient clientSocket=new SocketClient(server[2], Integer.valueOf(server[3]));
                        try {
                                String[] tmp=msg.replaceAll("\\s+","").split("\\|");
                                String[] processed = Arrays.copyOfRange(tmp, 1, tmp.length);
                                String[] client = this.myClient.send("clientInfo-"+processed[0]).split("-");//clientInfo-clientId-clientIp-clientPort
                                clientSocket.send("request-"+client[2]+"-"+client[3]+"-"+msg);
                        } catch (IOException ex) {
                            this.myClient.send("serverDown-"+server[1]);
                        }
                        clientSocket.close();
                    }
                }
            } catch (IOException ex) {
                Logger.getLogger(LoadBalancer.class.getName()).log(Level.SEVERE, null, ex);
            }
        }

    }
    
}
