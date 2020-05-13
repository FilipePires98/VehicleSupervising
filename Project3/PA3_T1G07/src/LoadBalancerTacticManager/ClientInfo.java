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
public class ClientInfo{
    
    private int id;
    private String host;
    private int port;

    public ClientInfo(int id, String host, int port) {
        this.id=id;
        this.host = host;
        this.port = port;
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
    
}
