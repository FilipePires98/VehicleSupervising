package fi.monitors;

import fi.EndSimulationException;
import fi.FarmInfrastructure;
import fi.MonitorMetadata;
import fi.StopHarvestException;
import fi.ccInterfaces.StorehouseCCInt;
import fi.farmerInterfaces.StorehouseFarmerInt;
import fi.workers.Farmer;
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
    
    private Map<Integer, Integer> positions;
    private List<Integer> availablePosition;
    
    private int farmersInStorehouse;
    private int farmersSelected;
    private boolean prepareOrderGiven;
    private boolean stopHarvest=false;
    private boolean endSimulation=false;
    private boolean proxyInMonitor=false;
    private int entitiesToStop=0;
    private int cornCobs=0;
    
    /*
        Constructors
    */
    
    /**
     * Storehouse monitor constructor.
     * @param fi
     * @param metadata
     */
    public Storehouse(FarmInfrastructure fi, MonitorMetadata metadata) {
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
     * @throws fi.StopHarvestException 
     * @throws fi.EndSimulationException 
     */
    @Override
    public void farmerEnter(int farmerId) throws StopHarvestException, EndSimulationException{
        rl.lock();
        try {
            this.waitRandomDelay();
            farmersInStorehouse++;
            this.selectSpot(farmerId);
            int cobs = ((Farmer)Thread.currentThread()).getCornCobs();
            this.cornCobs+=cobs;
            ((Farmer)Thread.currentThread()).setCornCobs(0);
            this.fi.updateStorehouseCornCobs(this.cornCobs);
            this.fi.sendMessage("updateStorehouseCobs;"+this.cornCobs);
            System.out.println("[Storehouse] Farmer " + farmerId + " entered.");
            while(farmersInStorehouse<this.metadata.MAXNUMBERFARMERS){
                allInStorehouse.await();
                
                if(this.stopHarvest){
                    entitiesToStop--;
                    farmersInStorehouse--;
                    this.availablePosition.add(this.positions.get(farmerId));
                    this.positions.remove(farmerId);
                    if(entitiesToStop==0){
                        prepareOrderGiven=false;
                        stopHarvest=false;
                    }
                    throw new StopHarvestException();
                }
                if(this.endSimulation){
                    throw new EndSimulationException();
                }
            }
            allInStorehouse.signalAll();
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
    public void farmerWaitPrepareOrder(int farmerId) throws StopHarvestException, EndSimulationException {
        rl.lock();
        try {
            this.waitRandomDelay();
            while(!prepareOrderGiven){
                prepareOrder.await();
                if(this.stopHarvest){
                    entitiesToStop--;
                    farmersInStorehouse--;
                    this.availablePosition.add(this.positions.get(farmerId));
                    this.positions.remove(farmerId);
                    if(entitiesToStop==0){
                        prepareOrderGiven=false;
                        stopHarvest=false;
                    }
                    throw new StopHarvestException();
                }
                if(this.endSimulation){
                    throw new EndSimulationException();
                }
            }
            farmersSelected++;
            farmersInStorehouse--;
            this.availablePosition.add(this.positions.get(farmerId));
            this.positions.remove(farmerId);
            
            if(farmersSelected==metadata.NUMBERFARMERS){
                farmersSelected = 0;
                prepareOrderGiven = false;
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
    public void waitAllFarmersReady() throws StopHarvestException, EndSimulationException{
        rl.lock();
        try {
            this.waitRandomDelay();
            this.proxyInMonitor=true;
            while(farmersInStorehouse<metadata.MAXNUMBERFARMERS){
                allInStorehouse.await();
                
                if(this.stopHarvest){
                    this.entitiesToStop--;
                    this.proxyInMonitor=false;
                    if(entitiesToStop==0){
                        prepareOrderGiven=false;
                        stopHarvest=false;
                    }
                    throw new StopHarvestException();
                }
                if(this.endSimulation){
                    throw new EndSimulationException();
                }
            }
            this.proxyInMonitor=false;
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
            this.waitRandomDelay();
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
        rl.lock();
        try{
            this.waitRandomDelay();
            switch(action){
                case "stopHarvest":
                    if(this.farmersInStorehouse!=0 || proxyInMonitor){
                        this.stopHarvest=true;
                        this.entitiesToStop=this.farmersInStorehouse;
                        if(proxyInMonitor){
                            this.entitiesToStop++;
                        }
                    }
                    break;
                case "endSimulation":
                    this.endSimulation=true;
                    break;
            }

            this.allInStorehouse.signalAll();
            this.prepareOrder.signalAll();
        }
        finally{
            rl.unlock();
        }
    }
    
    /*
        Aux Methods
    */
    
    private void selectSpot(int farmerId){
        int randomPosition=(int)(Math.random()*(this.availablePosition.size()-1));
        this.positions.put(farmerId, availablePosition.get(randomPosition));
        this.availablePosition.remove(randomPosition);
        this.fi.presentFarmerInStorehouse(farmerId,positions.get(farmerId));
        this.fi.sendMessage("presentInStorehouse;"+farmerId+";"+positions.get(farmerId));
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