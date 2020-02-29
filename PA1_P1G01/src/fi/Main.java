package fi;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.Arrays;

/**
 * Main class for the simulation of an harvest in an agricultural farm.
 * @author Filipe Pires (85122) and João Alegria (85048)
 */
public class Main {

    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) throws IOException {
        
        //System.out.println("Working Directory = " + System.getProperty("user.dir") + "/src/fi/FarmInfrastructure.java");
        try {
            runProcess("javac -cp src src/fi/FarmInfrastructure.java");
            runProcess("java -cp src fi/FarmInfrastructure");
            while(true) {
                
            }
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
        //printLines(command + " stdout:", pro.getInputStream());
        //printLines(command + " stderr:", pro.getErrorStream());
        //pro.waitFor();
        //System.out.println(command + " exitValue() " + pro.exitValue());
    }
    
}
