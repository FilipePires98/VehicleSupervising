
import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;


/**
 * Main class for the vehicle supervising system.
 * 
 * @author Filipe Pires (85122) and João Alegria (85048)
 */
public class Main {

   /**
     * Function responsible for initializing all system entities.
     * @param args the command line arguments
     */
    public static void main(String[] args) {
        
        System.out.println("Deploying system processes...");
        
        /* Instantiate required variables */
        
        String[] entities = {"Collect", "Batch", "Alarm", "Report"};
        String[] topics = {"BatchTopic", "ReportTopic", "AlarmTopic"};
        
        String[] commands = new String[entities.length-1];
        int i = 0;
        
        /* Execute kafka Initialization Script */
        
        
        
        /* Launch Collect Entity */
        
        System.out.println("[" + entities[i] + "] Initializing " + entities[i] + " Entity...");
        CollectEntity collect = new CollectEntity(topics);
        collect.setVisible(true);
        
        /* Launch Remaining Entities */
        
        String topicNames = "";
        for(i=0; i<topics.length; i++) {
            topicNames += topics[i] + " ";
        }
        for(i=1; i<entities.length; i++) {
            commands[i-1] = "java -cp " + System.getProperty("user.dir") + "/build/classes " + entities[i] + "Entity " + topicNames;
        }
        try {
            runProcess(commands);
        } catch (Exception e) {
            System.err.println("Error: unable to assign process to an entity.");
            e.printStackTrace();
            System.exit(1);
        }
        
    }
    
    /**
     * Executes other independent processes. 
     * @param commands array of strings with each containing the command line to execute
     * @throws Exception in case of some error occurs during the process execution phase and stream fetching
     */
    private static void runProcess(String[] commands) throws Exception {
        Process[] processes = new Process[commands.length];
        Thread[] ioThreads = new Thread[commands.length*2];
        for(int i=0; i<commands.length; i++) {
            Process proc = Runtime.getRuntime().exec(commands[i]);
            processes[i] = proc;
            ioThreads[i] = new Thread() {
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
        }
    }
    
}
