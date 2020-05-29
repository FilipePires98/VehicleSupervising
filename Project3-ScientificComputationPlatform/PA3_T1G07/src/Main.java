
import org.apache.commons.cli.*;
import common.Utilities;

/**
 * Main class responsible for instantiating the Load Balancer, 2 Servers and 2 Clients. This instantiation is meant to facilitate the deployment phase and speed up testing.
 * @author Filipe Pires (85122) and João Alegria (85048)
 */
public class Main {
    
    private static final String monitorServerHost="localhost";
    private static final int monitorServerPort=6001;
    
    private static final String loadServerHost="localhost";
    private static final int loadServerPort=6000;

    public static void main(String[] args) {
        
        /* Parse arguments */
        
        Options options = new Options();
        
        Option nServers = new Option("s", "servers", true, "number of servers");
        //nServers.setRequired(true);
        options.addOption(nServers);

        Option nClients = new Option("c", "clients", true, "number of clients");
        //nClients.setRequired(true);
        options.addOption(nClients);

        CommandLineParser parser = new DefaultParser();
        HelpFormatter formatter = new HelpFormatter();
        CommandLine cmd = null;
        
        try {
            cmd = parser.parse(options, args);
        } catch (ParseException e) {
            System.out.println(e.getMessage());
            formatter.printHelp("Main", options);
            System.exit(1);
        }
        
        int ns = 1;
        int nc = 1;
        if(cmd.getOptions().length > 0) {
            try {
                ns = Integer.parseInt(cmd.getOptionValue("servers"));
                nc = Integer.parseInt(cmd.getOptionValue("clients"));
            } catch(Exception e){
                System.err.println("Error: program arguments 'number of servers' and 'number of clients' must have integer values.");
                e.printStackTrace();
                System.exit(1);
            }
        }
        
        /* Instantiate required variables */
        
        String[] entities = {"LoadBalancerTacticManager", "Server", "Client"};
        String[] commands = new String[1+ns+nc];
        
        String userDir = System.getProperty("user.dir");
        String jars = userDir + "/dist/lib/*:";
        
        /* Launch Entities */
        
        int serverID = 1;
        int clientID = 1;
        commands[0] = "java -cp " + jars + userDir + "/build/classes entities." + entities[0]; // launch LB/M
        for(int i=1; i<commands.length; i++) {
            if(i>=1+ns) { 
                commands[i] = "java -cp " + jars + userDir + "/build/classes entities." + entities[2] + " " + (6200+clientID) + " " + loadServerHost + " " + loadServerPort; // launch Clients
                clientID++;
            } else { 
                commands[i] = "java -cp " + jars + userDir + "/build/classes entities." + entities[1] + " " + (6100+serverID) + " " + monitorServerHost + " " + monitorServerPort; // launch Servers
                serverID++;
            }
        }
        
        try {
            Thread[] ioThreads = Utilities.runProcess(commands);
            
            System.out.println("System deployed.");
            
            for(Thread t: ioThreads) {
                t.join();
            }
        } catch (Exception e) {
            System.err.println("Error: unable to assign process to an entity.");
            e.printStackTrace();
            System.exit(1);
        }
        
    }
    
    
    
}
