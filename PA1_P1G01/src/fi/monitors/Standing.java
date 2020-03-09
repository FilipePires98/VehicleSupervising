package fi.monitors;

import fi.MonitorMetadata;
import fi.ccInterfaces.StandingCCInt;
import fi.farmerInterfaces.StandingFarmerInt;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Class for the Standing Sector of the farm.
 * @author Filipe Pires (85122) and João Alegria (85048)
 */
public class Standing implements StandingFarmerInt, StandingCCInt {
    
    /*
        Monitor variables
    */

    private MonitorMetadata metadata;
    private int farmersInStanding=0;
    private boolean startOrderGiven=false;
    
    /*
        Constructors
    */
    
    /**
     * Standing Area monitor constructor.
     * @param metadata 
     */
    public Standing(MonitorMetadata metadata) {
        this.metadata=metadata;
    }
    
    /*
        Methods executed by farmers
    */

    /**
     * 
     * @param farmerId 
     */
    @Override
    public synchronized void farmerEnter(int farmerId) {
        try {
            farmersInStanding++;
            if(farmersInStanding==this.metadata.NUMBERFARMERS) {
                notifyAll();
            }
            while(farmersInStanding<this.metadata.NUMBERFARMERS){
                wait();
            }
        } catch (InterruptedException ex) {
            Logger.getLogger(Standing.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    /**
     * 
     * @param farmerId 
     */
    @Override
    public synchronized void farmerWaitStartOrder(int farmerId) {
        try{
            while(!startOrderGiven){
                wait();
            }
            farmersInStanding--;
            if(farmersInStanding==0){
                startOrderGiven=false;
            }
        } catch (InterruptedException ex) {
            Logger.getLogger(Standing.class.getName()).log(Level.SEVERE, null, ex);
        } 
    }
    
    /*
        Methods executed by Message Processor
    */
    
    /**
     * 
     */
    @Override
    public synchronized void sendStartOrder() {
        startOrderGiven = true;
        notifyAll();
    }

    /*
    @Override
    public synchronized void waitForAllFarmers() {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }
    */

    /**
     * 
     * @param action 
     */
    @Override
    public synchronized void control(String action) {
        switch(action){
            case "stopHarvest":
                break;
            case "endSimulation":
                break;
        }
    }
    
    
    
    
    
}