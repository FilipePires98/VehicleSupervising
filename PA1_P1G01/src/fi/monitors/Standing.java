package fi.monitors;

import fi.EndSimulationException;
import fi.FarmInfrastructure;
import fi.MonitorMetadata;
import fi.StopHarvestException;
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
    
    private Map positions;
    private List availablePosition;
    
    
    private boolean stopHarvest=false;
    private boolean endSimulation=false;


    
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
    
    
    private void selectSpot(int farmerId){
        int randomPosition=(int)(Math.random()*(this.availablePosition.size()-1));
        this.positions.put(farmerId, availablePosition.get(randomPosition));
        this.availablePosition.remove(randomPosition);
    }

    /**
     * 
     * @param farmerId 
     */
    @Override
    public synchronized void farmerEnter(int farmerId) throws StopHarvestException, EndSimulationException{
        try {

            farmersInStanding++;
            this.selectSpot(farmerId);
            while(farmersInStanding<this.metadata.NUMBERFARMERS){
                wait();
                
                if(this.stopHarvest){
                    throw new StopHarvestException();
                }
                if(this.endSimulation){
                    throw new EndSimulationException();
                }
            }
            notifyAll();

        } catch (InterruptedException ex) {
            Logger.getLogger(Standing.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    /**
     * 
     * @param farmerId 
     */
    @Override
    public synchronized void farmerWaitStartOrder(int farmerId) throws StopHarvestException, EndSimulationException{
        try{
            while(!startOrderGiven){
                wait();
                
                if(this.stopHarvest){
                    throw new StopHarvestException();
                }
                if(this.endSimulation){
                    throw new EndSimulationException();
                }
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
                this.stopHarvest=true;
                break;
            case "endSimulation":
                this.endSimulation=true;
                break;
        }

        notifyAll();

    }
    
    
    
    
    
}