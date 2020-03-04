package fi;

import fi.monitors.Standing;
import fi.monitors.Storehouse;
import fi.monitors.Path;
import fi.monitors.Granary;
import fi.workers.CCProxy;
import common.SocketClient;
import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.InputStreamReader;
import java.io.PrintStream;
import java.net.Socket;

/**
 * Class for the Farm Infrastructure for the agricultural harvest.
 * @author Filipe Pires (85122) and João Alegria (85048)
 */
public class FarmInfrastructure {
    public static void main(String[] args) {
        
        System.out.println("Initializing Farm Infrastructure... ");
        
        try {
            // https://www.javaworld.com/article/2853780/socket-programming-for-scalable-systems.html
            
            // Connect to the server
            
            MonitorMetadata metadata = new MonitorMetadata(5,1,1);
            
            Storehouse storeHouse = new Storehouse(metadata);
            Standing standing = new Standing(metadata);
            Path path = new Path(metadata);
            Granary granary = new Granary(metadata);
            
            CCProxy messageProcessor = new CCProxy(storeHouse, standing, path, granary);
            
            SocketClient client = new SocketClient("localhost", 6666);

            
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
