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
    
    private Map<Integer, Integer> positions;
    private List<Integer> availablePosition;

    private boolean stopHarvest=false;
    private boolean endSimulation=false;

    private boolean proxyInMonitor=false;
    
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
    public synchronized void farmerEnter(int farmerId) throws StopHarvestException, EndSimulationException{
        try {
            this.waitRandomDelay();
            farmersInStanding++;
            this.selectSpot(farmerId);
            this.fi.presentFarmerInStandingArea(farmerId,positions.get(farmerId));
            System.out.println("[Standing Area] Farmer " + farmerId + " entered.");
            while(farmersInStanding<this.metadata.NUMBERFARMERS){
                wait();
                
                if(this.stopHarvest){
                    farmersInStanding--;
                    this.availablePosition.add(this.positions.get(farmerId));
                    this.positions.remove(farmerId);
                    if(farmersInStanding==0){
                        this.stopHarvest=false;
                    }
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
            this.waitRandomDelay();
            while(!startOrderGiven){
                wait();
                
                if(this.stopHarvest){
                    farmersInStanding--;
                    this.availablePosition.add(this.positions.get(farmerId));
                    this.positions.remove(farmerId);
                    if(farmersInStanding==0){
                        startOrderGiven=false;
                        this.stopHarvest=false;
                    }
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
        this.waitRandomDelay();
        startOrderGiven = true;
        notifyAll();
    }

    
    @Override
    public synchronized void waitForAllFarmers() throws StopHarvestException, EndSimulationException{
        try {
            this.waitRandomDelay();
            this.proxyInMonitor=true;
            while(farmersInStanding<this.metadata.NUMBERFARMERS){
                wait();
                
                if(this.stopHarvest){
                    this.proxyInMonitor=false;
                    this.stopHarvest=false;
                    throw new StopHarvestException();
                }
                if(this.endSimulation){
                    throw new EndSimulationException();
                }
            }
            this.proxyInMonitor=false;
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
        this.waitRandomDelay();
        switch(action){
            case "stopHarvest":
                if(this.farmersInStanding!=0 || proxyInMonitor){
                    this.stopHarvest=true;
                }
                break;
            case "endSimulation":
                this.endSimulation=true;
                break;
        }

        notifyAll();

    }
    
    
    
    /*
        Aux Methods
    */
    
    private void selectSpot(int farmerId){
        int randomPosition=(int)(Math.random()*(this.availablePosition.size()-1));
        this.positions.put(farmerId, availablePosition.get(randomPosition));
        this.availablePosition.remove(randomPosition);
    }
    
    private void waitRandomDelay(){
        try {
            int randomDelay=(int)(Math.random()*(this.metadata.MAXDELAY));
            Thread.sleep(randomDelay);
        } catch (InterruptedException ex) {
            Logger.getLogger(Storehouse.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
    
}