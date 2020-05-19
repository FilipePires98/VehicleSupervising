
import java.io.BufferedReader;
import java.io.File;
import java.io.InputStream;
import java.io.InputStreamReader;
import org.apache.commons.cli.*;


public class Main {
    
    private static final String monitorServerHost="localhost";
    private static final int monitorServerPort=6001;
    
    private static final String loadServerHost="localhost";
    private static final int loadServerPort=6000;

    public static void main(String[] args) {
        
        /* Parse arguments */
        
        Options options = new Options();
        
        Option nServers = new Option("s", "servers", true, "number of servers");
        nServers.setRequired(true);
        options.addOption(nServers);

        Option nClients = new Option("c", "clients", true, "number of clients");
        nClients.setRequired(true);
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
        try {
            ns = Integer.parseInt(cmd.getOptionValue("servers"));
            nc = Integer.parseInt(cmd.getOptionValue("clients"));
        } catch(Exception e){
            System.err.println("Error: program arguments 'number of servers' and 'number of clients' must have integer values.");
            e.printStackTrace();
            System.exit(1);
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
            Thread[] ioThreads = runProcess(commands);
            
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
    
    /**
     * Executes other independent processes. 
     * 
     * @param commands array of strings with each containing the command line to execute
     * @return array of threads responsible for listening to the outputs of each process created
     * @throws Exception in case of some error occurs during the process execution phase and stream fetching
     */
    private static Thread[] runProcess(String[] commands) throws Exception {
        Process[] processes = new Process[commands.length];
        Thread[] ioThreads = new Thread[commands.length*2];
        for(int i=0; i<commands.length; i++) {
            Process proc = Runtime.getRuntime().exec(commands[i]);
            processes[i] = proc;
            ioThreads[i] = new Thread() { // https://stackoverflow.com/questions/15801069/printing-a-java-inputstream-from-a-process
                @Override
                public void run() {
                    try {
                        final BufferedReader reader = new BufferedReader(new InputStreamReader(proc.getInputStream()));
                        String line = null;
                        while ((line = reader.readLine()) != null) {
                            System.out.println(line);
                        }
                        reader.close();
                    } catch (final Exception e) {
                        e.printStackTrace();
                    }
                }
            };
            ioThreads[i+commands.length] = new Thread() {
                @Override
                public void run() {
                    try {
                        final BufferedReader reader = new BufferedReader(new InputStreamReader(proc.getErrorStream()));
                        String line = null;
                        while ((line = reader.readLine()) != null) {
                            System.out.println(line);
                        }
                        reader.close();
                    } catch (final Exception e) {
                        e.printStackTrace();
                    }
                }
            };
            ioThreads[i].start();
            ioThreads[i+commands.length].start();
            //processes[i].waitFor();
            Thread.sleep(1000);
        }
        return ioThreads;
    }
    
}
