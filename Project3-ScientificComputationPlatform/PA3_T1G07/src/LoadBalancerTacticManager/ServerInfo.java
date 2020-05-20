/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package LoadBalancerTacticManager;

import java.util.ArrayList;
import java.util.List;

/**
 *
 * @author joaoalegria
 */
public class ServerInfo implements Comparable<ServerInfo>{
    
    private int id;
    private String host;
    private int port;
    private List<String> requests;

    public ServerInfo(int id, String host, int port) {
        this.id=id;
        this.host = host;
        this.port = port;
        this.requests=new ArrayList();
    }
    
    public void addRequest(String request){
        this.requests.add(request);
    }
    
    public void removeRequest(String request){
        this.requests.remove(request);
    }

    public List<String> getRequests() {
        return requests;
    }
    
    public String getHost() {
        return host;
    }

    public int getPort() {
        return port;
    }

    public int getId() {
        return id;
    }
    
    @Override
    public int compareTo(ServerInfo t) {
        return this.requests.size()-t.getRequests().size();
    }
    
    
}
