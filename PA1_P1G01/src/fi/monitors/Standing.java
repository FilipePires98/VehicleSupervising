package fi.monitors;

import fi.FarmInfrastructure;
import fi.MonitorMetadata;
import fi.ccInterfaces.StandingCCInt;
import fi.farmerInterfaces.StandingFarmerInt;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
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

    private FarmInfrastructure fi;
    private MonitorMetadata metadata;
    
    private int farmersInStanding=0;
    private boolean startOrderGiven=false;
    
    private Map<Integer, Integer> positions;
    private List<Integer> availablePosition;

    
    /*
        Constructors
    */
    
    /**
     * Standing Area monitor constructor.
     * @param fi
     * @param metadata 
     */
    public Standing(FarmInfrastructure fi, MonitorMetadata metadata) {
        this.fi = fi;
        this.metadata=metadata;
        positions=new HashMap<Integer, Integer>();
        availablePosition=new ArrayList<Integer>();
        for(int i=0; i<this.metadata.MAXNUMBERFARMERS;i++){
            availablePosition.add(i);
        }
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
            this.selectSpot(farmerId);
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
            this.availablePosition.add(this.positions.get(farmerId));
            this.positions.remove(farmerId);
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

    
    @Override
    public synchronized void waitForAllFarmers() {
        try {
            while(farmersInStanding<this.metadata.NUMBERFARMERS){
                wait();
            }
        } catch (InterruptedException ex) {
            Logger.getLogger(Standing.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
    

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
    
    /*
        Aux Methods
    */
    
    private void selectSpot(int farmerId){
        int randomPosition=(int)Math.random()*(this.availablePosition.size()-1);
        this.positions.put(farmerId, availablePosition.get(randomPosition));
        this.availablePosition.remove(randomPosition);
        switch(positions.get(farmerId)) {
            case 0:
                fi.getSa1().setText(""+farmerId);
                break;
            case 1:
                fi.getSa2().setText(""+farmerId);
                break;
            case 2:
                fi.getSa3().setText(""+farmerId);
                break;
            case 3:
                fi.getSa4().setText(""+farmerId);
                break;
            case 4:
                fi.getSa5().setText(""+farmerId);
                break;
        }
        fi.getSa1().setText(""+farmerId);
        
    }
    
    
}