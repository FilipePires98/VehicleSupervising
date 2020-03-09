package fi.monitors;

import fi.MonitorMetadata;
import fi.ccInterfaces.StorehouseCCInt;
import fi.farmerInterfaces.StorehouseFarmerInt;
import java.util.HashMap;
import java.util.Map;
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

    private MonitorMetadata metadata;
    
    private ReentrantLock rl = new ReentrantLock();
    private Condition allInStorehouse = rl.newCondition();
    private Condition prepareOrder = rl.newCondition();
    
    private int farmersInStorehouse;
    private int farmersSelected;
    //private Map<Integer, Integer> farmersMetadata; // <farmerID,numCornCobs>
    private boolean prepareOrderGiven;
    
    /*
        Constructors
    */
    
    /**
     * Storehouse monitor constructor.
     * @param metadata
     * @param totalNumberOfFarmers 
     */
    public Storehouse(MonitorMetadata metadata, int totalNumberOfFarmers) {
        this.metadata=metadata;
        farmersInStorehouse = 0;
        farmersSelected = 0;
        //farmersMetadata = new HashMap();
        prepareOrderGiven = false;
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
            if(farmersInStorehouse==this.metadata.NUMBERFARMERS) {
                allInStorehouse.signalAll();
            }
            while(farmersInStorehouse<this.metadata.NUMBERFARMERS){
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
            while(farmersInStorehouse<metadata.NUMBERFARMERS){
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
        metadata.NUMBERFARMERS = numberOfFarmers;
        metadata.NUMBERCORNCOBS = numberOfCornCobs;
        metadata.NUMBERSTEPS = maxNumberOfSteps;
        metadata.TIMEOUT = timeout;
        prepareOrderGiven = true;
        prepareOrder.signalAll();
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

}