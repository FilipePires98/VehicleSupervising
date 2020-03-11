package cc;

import java.awt.FlowLayout;
import java.awt.GridLayout;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.PrintStream;
import javax.swing.JFrame;
import javax.swing.JLabel;

/**
 * Main class for the simulation of an harvest in an agricultural farm.
 * @author Filipe Pires (85122) and João Alegria (85048)
 */
public class Main {

    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) {
 
        /*Launch Control Center UI*/
        System.out.println("[CC] Initializing Control Center... ");
        ControlCenter mainFrame = new ControlCenter();
        mainFrame.setVisible(true);
        
        /* Launch Farm Infrastructure */

        try {
            runProcess("java -cp " + System.getProperty("user.dir") + "/build/classes fi.FarmInfrastructure");
        } catch (Exception e) {
            e.printStackTrace();
        }
        

    }

    private static void runProcess(String command) throws Exception {
        Process proc = Runtime.getRuntime().exec(command);
        printLines("[FI]", proc.getInputStream());
        printLines("[FI]", proc.getErrorStream());
        proc.waitFor();
        System.out.println(command + " exitValue() " + proc.exitValue());
    }
    
    private static void printLines(String cmd, InputStream ins) throws Exception {
        String line = null;
        BufferedReader in = new BufferedReader(new InputStreamReader(ins));
        while ((line = in.readLine()) != null) {
            System.out.println(cmd + " " + line);
        }
    }
    
}
