package common;

import java.io.BufferedReader;
import java.io.InputStreamReader;

/**
 * Auxiliary static that centralized the necessary process to deploy new server and clients.
 * @author Filipe Pires (85122) and João Alegria (85048)
 */
public class Utilities {
    /**
     * Executes other independent processes. 
     * 
     * @param commands array of strings with each containing the command line to execute
     * @return array of threads responsible for listening to the outputs of each process created
     * @throws Exception in case of some error occurs during the process execution phase and stream fetching
     */
    public static Thread[] runProcess(String[] commands) throws Exception {
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
