package fi.utils;

/**
 * Auxiliary object created to centralize some information about the simulated harvest.
 * @author Filipe Pires (85122) and João Alegria (85048)
 */
public class MonitorMetadata {
    
    public int MAXNUMBERFARMERS;
    public int MAXDELAY;
    public int NUMBERFARMERS;
    public int NUMBERCORNCOBS;
    public int NUMBERSTEPS;
    public int TIMEOUT;

    public MonitorMetadata(int numFarmers, int delay) {
        this.MAXNUMBERFARMERS=numFarmers;
        this.MAXDELAY=delay;
    }
    
}
