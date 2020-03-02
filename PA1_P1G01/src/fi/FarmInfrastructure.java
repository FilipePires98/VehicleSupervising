package fi;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.PrintStream;
import java.net.Socket;

/**
 * Class for the Farm Infrastructure for the agricultural harvest.
 * @author Filipe Pires (85122) and João Alegria (85048)
 */
public class FarmInfrastructure {
    public static void main(String[] args) {
        
        System.out.println("[FI]: Initializing Farm Infrastructure... ");
        
        try {
            // https://www.javaworld.com/article/2853780/socket-programming-for-scalable-systems.html
            
            // Connect to the server
            Socket socket = new Socket(args[0], Integer.parseInt(args[1]));

            // Create input and output streams to read from and write to the server
            PrintStream out = new PrintStream( socket.getOutputStream() );
            BufferedReader in = new BufferedReader( new InputStreamReader(socket.getInputStream()) );

            // Read data from the server until we finish reading the document
            String line = in.readLine();
            while( line != null ) {
                System.out.println( line );
                line = in.readLine();
            }

            // Close streams
            in.close();
            out.close();
            socket.close();
            
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
