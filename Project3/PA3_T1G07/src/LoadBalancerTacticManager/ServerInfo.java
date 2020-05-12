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
public class ServerInfo implements Comparable{
    
    private int id;
    private int port;
    private List<RequestInfo> requests;

    public ServerInfo(int id, int port) {
        this.id = id;
        this.port = port;
        this.requests=new ArrayList();
    }
    
    public void addRequest(String request){
        this.requests.add(new RequestInfo(request));
    }
    
    public void removeRequest(String request){
        RequestInfo ri=null;
        for(RequestInfo tmp : this.requests){
            if(tmp.getRequest().equals(request)){
                ri=tmp;
                break;
            }
        }
        this.requests.remove(ri);
    }

    public List<RequestInfo> getRequests() {
        return requests;
    }

    public int getPort() {
        return port;
    }

    public int getId() {
        return id;
    }
    
    @Override
    public int compareTo(Object t) {
        ServerInfo other=(ServerInfo)t;
        if(this.requests.size()==other.getRequests().size()){
            return 0;
        }else if(this.requests.size()>other.getRequests().size()){
            return 1;
        }else{
            return -1;
        }
    }
    
    
}
