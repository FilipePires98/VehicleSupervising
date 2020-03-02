import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.PrintStream;

/**
 * Main class for the simulation of an harvest in an agricultural farm.
 * @author Filipe Pires (85122) and João Alegria (85048)
 */
public class Main {

    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) {
        
        /* Parse Program Arguments */
        
        // java Main [-cc <server> <path>] [-fi <server> <path>] [-s <windowSizeReduction>]
        
        args = parseArgs(args);
        String cc_server = args[0];
        String cc_path = args[1];
        String fi_server = args[2];
        String fi_path = args[3];
        int windowSizeReduction = Integer.parseInt(args[4]);
        
        /* Run Simulation */

        try {
            runProcess("java -cp " + System.getProperty("user.dir") + "/build/classes fi.FarmInfrastructure " + fi_server + " " + fi_path + " " + windowSizeReduction);
            runProcess("java -cp " + System.getProperty("user.dir") + "/build/classes cc.ControlCenter " + cc_server + " " + cc_path + " " + windowSizeReduction);
        } catch (Exception e) {
            e.printStackTrace();
        }
        
    }
    
    private static void printLines(String cmd, InputStream ins) throws Exception {
        String line = null;
        BufferedReader in = new BufferedReader(new InputStreamReader(ins));
        while ((line = in.readLine()) != null) {
            System.out.println(cmd + " " + line);
        }
    }

    private static void runProcess(String command) throws Exception {
        Process pro = Runtime.getRuntime().exec(command);
        //printLines("[FI] stdout:", pro.getInputStream());
        //printLines("[FI] stderr:", pro.getErrorStream());
        //pro.waitFor();
        //System.out.println(command + " exitValue() " + pro.exitValue());
    }
    
    private static String[] parseArgs(String args[]) {
        String cc_server = "localhost";
        int cc_path = 8001;
        
        String fi_server = "localhost";
        int fi_path = 8002;
        
        int windowSizeReduction = 1;
        
        for(int i=0; i<args.length; i++) {
            if(args[i].equals("-cc")) {
                if(i==args.length-1 || i==args.length-2 || args[i+1].equals("-fi") || args[i+1].equals("-s") || args[i+2].equals("-fi") || args[i+2].equals("-s")) {
                    System.err.println("[CC] Error: CC socket values must be set right after '-cc'. Values were set to default (server='" + cc_server + "', path=" + cc_path + ").");
                    continue;
                    //System.exit(1);
                }
                try { 
                    Integer.parseInt(args[i+2]); 
                } catch(Exception e) { 
                    System.err.println("[CC] Error: Path values must be integers. CC Path value was set to default (" + cc_path + ").");
                    continue;
                }
                cc_server = args[i+1];
                cc_path = Integer.parseInt(args[i+2]);
                i = i+2;
            }
            if(args[i].equals("-fi")) {
                if(i==args.length-1 || i==args.length-2 || args[i+1].equals("-cc") || args[i+1].equals("-s") || args[i+2].equals("-cc") || args[i+2].equals("-s")) {
                    System.err.println("[CC] Error: FI socket values must be set right after '-fi'. Values were set to default (server='" + fi_server + "', path=" + fi_path + ").");
                    continue;
                    //System.exit(1);
                }
                try { 
                    Integer.parseInt(args[i+2]); 
                } catch(Exception e) { 
                    System.err.println("[CC] Error: Path values must be integers. FI Path value was set to default (" + fi_path + ").");
                    continue;
                }
                fi_server = args[i+1];
                fi_path = Integer.parseInt(args[i+2]);
                i = i+2;
            }
            if(args[i].equals("-s")) {
                if(i==args.length-1 || args[i+1].equals("-cc") || args[i+1].equals("-fi")) {
                    System.err.println("[CC] Error: Window size reduction value must be set right after '-s'. Value was set to default (" + windowSizeReduction + ").");
                    continue;
                    //System.exit(1);
                }
                try { 
                    Integer.parseInt(args[i+1]); 
                } catch(Exception e) { 
                    System.err.println("[CC] Error: Window size reduction value must be an integer. Value was set to default (" + windowSizeReduction + ").");
                    continue;
                }
                int s = Integer.parseInt(args[i+1]);
                if(s<1 || s>5) {
                    System.err.println("[CC] Error: Window size reduction value must be between 1 and 5. Value was set to default (" + windowSizeReduction + ").");
                    continue;
                }
                windowSizeReduction = s;
                i = i+1;
            }
        }
        
        String[] retval = {cc_server, cc_path+"", fi_server, fi_path+"", windowSizeReduction+""};
        return retval;
    }
    
}
