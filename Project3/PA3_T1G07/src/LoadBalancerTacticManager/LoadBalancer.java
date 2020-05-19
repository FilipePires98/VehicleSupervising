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
    private String monitorIp;
    private int monitorPort;
    private SocketClient monitorClient;
    
    public LoadBalancer(String monitorIp, int monitorPort) {
        this.monitorIp=monitorIp;
        this.monitorPort=monitorPort;
        this.server = new SocketServer(6000, this);
        this.serverThread = new Thread(server);
        this.serverThread.start();
        this.monitorClient=new SocketClient(monitorIp, monitorPort);
    }
    
    public void updateTacticMonitor(String endpoint, int port){
        this.monitorIp=endpoint;
        this.monitorPort=port;
        this.monitorClient.close();
        this.monitorClient=new SocketClient(endpoint, port);
    }

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
    
    public class LoadDistributor implements Runnable{
        
        private List<String> messages;
        private SocketClient myClient;

        public LoadDistributor(List<String> messages) {
            this.messages = messages;
            this.myClient=new SocketClient(monitorIp, monitorPort);
        }

        @Override
        public void run() {
            try {
                String[] server = this.myClient.send("leastOccupiedServer").split("-"); //serverInfo-serverId-serverIp-serverPort
                System.out.println(Arrays.deepToString(server));
                if(Integer.valueOf(server[1])==-1){
                    for(String msg:messages){
                        String[] tmp=msg.replaceAll("\\s+","").split("\\|");
                        String[] processed = Arrays.copyOfRange(tmp, 1, tmp.length);
                        String[] client = this.myClient.send("clientInfo-"+processed[0]).split("-");//clientInfo-clientId-clientIp-clientPort
                        SocketClient clientSocketClient=new SocketClient(client[2], Integer.valueOf(client[3]));
                        clientSocketClient.send("Server unavailable. Try later.");
                        clientSocketClient.close();
                    }
                }
                else{
                    SocketClient clientSocket=new SocketClient(server[2], Integer.valueOf(server[3]));
                    try {
                        for(String msg:messages){
                            String[] tmp=msg.replaceAll("\\s+","").split("\\|");
                            String[] processed = Arrays.copyOfRange(tmp, 1, tmp.length);
                            System.out.println(Arrays.deepToString(processed));
                            String[] client = this.myClient.send("clientInfo-"+processed[0]).split("-");//clientInfo-clientId-clientIp-clientPort
                            clientSocket.send("request-"+client[2]+"-"+client[3]+"-"+msg);
                        }
                    } catch (IOException ex) {
                        this.myClient.send("serverDown-"+server[1]);
                    }
                    clientSocket.close();
                }
            } catch (IOException ex) {
                Logger.getLogger(LoadBalancer.class.getName()).log(Level.SEVERE, null, ex);
            }
            
            
        }

    }
    
}
