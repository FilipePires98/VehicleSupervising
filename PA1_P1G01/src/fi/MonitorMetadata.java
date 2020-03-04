/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package fi;

/**
 *
 * @author joaoalegria
 */
public class MonitorMetadata {
    
    private int NUMBERFARMERS;
    private int NUMBERSTEPS;
    private int TIMEOUT;

    public MonitorMetadata(int numFarmers, int numSteps, int timeout) {
        this.NUMBERFARMERS=numFarmers;
        this.NUMBERSTEPS=numSteps;
        this.TIMEOUT=timeout;
    }

    public int getNUMBERFARMERS() {
        return NUMBERFARMERS;
    }

    public int getNUMBERSTEPS() {
        return NUMBERSTEPS;
    }

    public int getTIMEOUT() {
        return TIMEOUT;
    }
    
    
    
}
