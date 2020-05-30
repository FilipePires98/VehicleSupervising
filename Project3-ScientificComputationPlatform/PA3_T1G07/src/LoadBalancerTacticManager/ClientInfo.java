package LoadBalancerTacticManager;

/**
 * Entity created to represent a client. It contains all the necessary metadata of the client, such as host, port and id.
 * @author Filipe Pires (85122) and João Alegria (85048)
 */
public class ClientInfo{
    
    private int id;
    private String host;
    private int port;

    /**
     * Creates instance of client information.
     * @param id client unique identifier
     * @param host client host IP address
     * @param port client port
     */
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

    @Override
    public String toString() {
        return "Client: " + id + " | Host: " + host + " | Port: " + port;
    }
    
    
}
