package LoadBalancerTacticManager;

import java.util.ArrayList;
import java.util.List;

/**
 * Entity created to represent a server. It contains all the necessary metadata of the server, such as host, port, id and request being processed.
 * @author Filipe Pires (85122) and João Alegria (85048)
 */
public class ServerInfo implements Comparable<ServerInfo>{
    
    private int id;
    private String host;
    private int port;
    private List<String> requests;

    /**
     * Creates instance of server information.
     * @param id server unique identifier
     * @param host server host IP address
     * @param port server port
     */
    public ServerInfo(int id, String host, int port) {
        this.id=id;
        this.host = host;
        this.port = port;
        this.requests=new ArrayList();
    }
    
    /**
     * Adds a new request to the in being processed requests list.
     * @param request String containing the request itself.
     */
    public void addRequest(String request){
        this.requests.add(request);
    }
    
    /**
     * Removes a certain request from the being processed requests list.
     * @param request SString containing the request itself.
     */
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
    
    /**
     * Compares the current server to another one in terms of number of requests being processed.
     * @param t ServerInfo representing the other server to compare with
     * @return int representing if the current server has more, less or the same number of request being processed.
     */
    @Override
    public int compareTo(ServerInfo t) {
        return this.requests.size()-t.getRequests().size();
    }

    @Override
    public String toString() {
        return "Server: " + id + " | Host: " + host + " | Port: " + port;
    }
    
}
