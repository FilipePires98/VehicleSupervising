package fi;

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
            
            Storehouse storeHouse = new Storehouse();
            Standing standing = new Standing();
            Path path = new Path();
            Granary granary = new Granary();
            
            FIMessageProcessor messageProcessor = new FIMessageProcessor(storeHouse, standing, path, granary);
            
            SocketClient client = new SocketClient("localhost", 6666);
            
            path.walkThroughPath();

            
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
