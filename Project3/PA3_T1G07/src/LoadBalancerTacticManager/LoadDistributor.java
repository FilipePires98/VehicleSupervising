/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package LoadBalancerTacticManager;

import common.SocketClient;
import java.io.IOException;
import java.util.List;

/**
 *
 * @author joaoalegria
 */
public class LoadDistributor implements Runnable{
        
        private List<String> messages;
        private ClusterInfo ci;

        public LoadDistributor(ClusterInfo ci, List<String> messages) {
            this.messages = messages;
            this.ci=ci;
        }
        

        @Override
        public void run() {
            ServerInfo si=ci.leastOccupiedServer();
            SocketClient client=new SocketClient(si.getHost(), si.getPort());
            try {
                for(String msg:messages){
                    String[] processed = msg.split("\\|");
                    ClientInfo clientInfo = ci.getClient(Integer.valueOf(processed[0]));
                    client.send("request-"+clientInfo.getHost()+"-"+clientInfo.getPort()+"-"+msg);
                }
            } catch (IOException ex) {
                ci.removeServer(si.getId());
            }
            client.close();
        }
        
    }