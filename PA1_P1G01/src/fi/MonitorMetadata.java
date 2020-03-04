package fi;

/**
 * 
 * @author Filipe Pires (85122) and João Alegria (85048)
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
