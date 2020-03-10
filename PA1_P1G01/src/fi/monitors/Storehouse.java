package fi.monitors;

import fi.FarmInfrastructure;
import fi.MonitorMetadata;
import fi.ccInterfaces.StorehouseCCInt;
import fi.farmerInterfaces.StorehouseFarmerInt;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.ReentrantLock;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Class for the Storehouse Sector of the farm.
 * @author Filipe Pires (85122) and João Alegria (85048)
 */
public class Storehouse implements StorehouseFarmerInt, StorehouseCCInt{
    
    /*
        Monitor variables
    */

    private FarmInfrastructure fi;
    private MonitorMetadata metadata;
    
    private ReentrantLock rl = new ReentrantLock();
    private Condition allInStorehouse = rl.newCondition();
    private Condition prepareOrder = rl.newCondition();
    
    private int farmersInStorehouse;
    private int farmersSelected;
    //private Map<Integer, Integer> farmersMetadata; // <farmerID,numCornCobs>
    private boolean prepareOrderGiven;
    
    private Map positions;
    private List availablePosition;

    
    /*
        Constructors
    */
    
    /**
     * Storehouse monitor constructor.
     * @param fi
     * @param metadata
     * @param totalNumberOfFarmers 
     */
    public Storehouse(FarmInfrastructure fi, MonitorMetadata metadata, int totalNumberOfFarmers) {
        this.fi = fi;
        this.metadata=metadata;
        farmersInStorehouse = 0;
        farmersSelected = 0;
        //farmersMetadata = new HashMap();
        prepareOrderGiven = false;
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
    public void farmerEnter(int farmerId) {
        rl.lock();
        try {
            farmersInStorehouse++;
            this.selectSpot(farmerId);
            if(farmersInStorehouse==this.metadata.MAXNUMBERFARMERS) {
                allInStorehouse.signalAll();
            }
            while(farmersInStorehouse<this.metadata.MAXNUMBERFARMERS){
                allInStorehouse.await();
            }
        } catch (InterruptedException ex) {
            Logger.getLogger(Storehouse.class.getName()).log(Level.SEVERE, null, ex);
        } finally {
            rl.unlock();
        }
    }
    
    /**
     * 
     * @param farmerId 
     */
    @Override
    public void farmerWaitPrepareOrder(int farmerId) {
        rl.lock();
        try {
            while(!prepareOrderGiven){
                prepareOrder.await();
                if(prepareOrderGiven) {
                    if(farmersSelected<metadata.NUMBERFARMERS) {
                        farmersSelected++;
                        farmersInStorehouse--;
                        this.availablePosition.add(this.positions.get(farmerId));
                        this.positions.remove(farmerId);
                    } else {
                        prepareOrderGiven = false;
                        farmersSelected = 0;
                    }
                }
            }
        } catch (InterruptedException ex) {
            Logger.getLogger(Storehouse.class.getName()).log(Level.SEVERE, null, ex);
        }finally{
            rl.unlock();
        }
    }
    
    /*
        Methods executed by Message Processor
    */
    
    /**
     * 
     */
    @Override
    public void waitAllFarmersReady() {
        rl.lock();
        try {
            while(farmersInStorehouse<metadata.MAXNUMBERFARMERS){
                allInStorehouse.await();
            }
        } catch (InterruptedException ex) {
            Logger.getLogger(Storehouse.class.getName()).log(Level.SEVERE, null, ex);
        } finally {
            rl.unlock();
        }
    }
    
    /**
     * 
     * @param numberOfFarmers
     * @param numberOfCornCobs
     * @param maxNumberOfSteps
     * @param timeout 
     */
    @Override
    public void sendSelectionAndPrepareOrder(int numberOfFarmers, int numberOfCornCobs, int maxNumberOfSteps, int timeout) {
        rl.lock();
        try{
            this.metadata.NUMBERFARMERS = numberOfFarmers;
            this.metadata.NUMBERCORNCOBS = numberOfCornCobs;
            this.metadata.NUMBERSTEPS = maxNumberOfSteps;
            this.metadata.TIMEOUT = timeout;
            this.prepareOrderGiven = true;
            this.prepareOrder.signalAll();
        }
        finally{
            rl.unlock();
        }
        
    }
    
    /**
     * 
     * @param action 
     */
    @Override
    public void control(String action) {
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
    }

}