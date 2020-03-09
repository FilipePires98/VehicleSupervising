package fi.monitors;

import fi.MonitorMetadata;
import fi.ccInterfaces.GranaryCCInt;
import fi.farmerInterfaces.GranaryFarmerInt;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.ReentrantLock;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Class for the Granary Sector of the farm.
 * @author Filipe Pires (85122) and João Alegria (85048)
 */
public class Granary implements GranaryFarmerInt, GranaryCCInt{
    
    /*
        Monitor variables
    */
    
    private MonitorMetadata metadata;
    
    private ReentrantLock rl = new ReentrantLock();
    private Condition allInGranary = rl.newCondition();
    private Condition allCollected = rl.newCondition();
    private Condition waitCollectOrder = rl.newCondition();
    private Condition waitReturnOrder = rl.newCondition();
    
    private int farmersInGranary=0;
    private int farmersCollected=0;
    private boolean readyToCollect=false;
    private boolean readyToReturn=false;
    
    /*
        Constructors
    */
    
    /**
     * Granary monitor constructor.
     * @param metadata 
     */
    public Granary(MonitorMetadata metadata) {
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
    public void farmerEnter(int farmerId) {
        rl.lock();
        try {
            farmersInGranary++;
            if(farmersInGranary==metadata.NUMBERFARMERS){
                allInGranary.signalAll();
            }
            while(farmersInGranary<metadata.NUMBERFARMERS){
                allInGranary.await();
            }
        } catch (InterruptedException ex) {
            Logger.getLogger(Granary.class.getName()).log(Level.SEVERE, null, ex);
        }finally{
            rl.unlock();
        }
    }

    /**
     * 
     * @param farmerId 
     */
    @Override
    public void farmerWaitCollectOrder(int farmerId) {
        rl.lock();
        try{
            while(!this.readyToCollect){
                this.waitCollectOrder.await();
            }
        }
        catch (InterruptedException ex) {
            Logger.getLogger(Granary.class.getName()).log(Level.SEVERE, null, ex);
        }
        finally{
            rl.unlock();
        }
    }

    /**
     * 
     * @param farmerId 
     */
    @Override
    public void farmerCollect(int farmerId) {
        rl.lock();
        try{
            this.farmersCollected++;
            while(this.farmersCollected<metadata.NUMBERFARMERS){
                this.allCollected.await();
            }
            this.allCollected.signalAll();
            this.readyToCollect=false;
        }
        catch (InterruptedException ex) {
            Logger.getLogger(Granary.class.getName()).log(Level.SEVERE, null, ex);
        }
        finally{
            rl.unlock();
        }
    }

    /**
     * 
     * @param farmerId 
     */
    @Override
    public void farmerWaitReturnOrder(int farmerId) {
        rl.lock();
        try{
            this.farmersCollected--;
            while(!this.readyToReturn){
                this.waitReturnOrder.await();
            }
            this.farmersInGranary--;
            if(this.farmersInGranary==0){
                this.readyToReturn=false;
            }
        }
        catch (InterruptedException ex) {
            Logger.getLogger(Granary.class.getName()).log(Level.SEVERE, null, ex);
        }
        finally{
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
    public void waitAllFarmersReadyToCollect() {
        rl.lock();
        try{
            while(this.farmersInGranary<metadata.NUMBERFARMERS){
                this.allCollected.await();
            }
        }
        catch (InterruptedException ex) {
            Logger.getLogger(Granary.class.getName()).log(Level.SEVERE, null, ex);
        }
        finally{
            rl.unlock();
        }
    }

    /**
     * 
     */
    @Override
    public void sendCollectOrder() {
        this.readyToCollect=true;
        this.waitCollectOrder.signalAll();
    }

    /**
     * 
     */
    @Override
    public void waitAllFarmersCollect() {
        rl.lock();
        try{
            while(this.farmersCollected<metadata.NUMBERFARMERS){
                this.allCollected.await();
            }
        }
        catch (InterruptedException ex) {
            Logger.getLogger(Granary.class.getName()).log(Level.SEVERE, null, ex);
        }
        finally{
            rl.unlock();
        }
    }

    /**
     * 
     */
    @Override
    public void sendReturnOrder() {
        this.readyToReturn=true;
        this.waitReturnOrder.signalAll();
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