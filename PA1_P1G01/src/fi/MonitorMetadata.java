package fi;

/**
 * 
 * @author Filipe Pires (85122) and João Alegria (85048)
 */
public class MonitorMetadata {
    
    public int MAXNUMBERFARMERS;
    public int NUMBERFARMERS;
    public int NUMBERCORNCOBS;
    public int NUMBERSTEPS;
    public int TIMEOUT;

    public MonitorMetadata(int numFarmers) {
        this.MAXNUMBERFARMERS=numFarmers;
    }
    
}
