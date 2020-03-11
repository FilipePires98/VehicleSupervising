package fi.monitors;

import fi.EndSimulationException;
import fi.FarmInfrastructure;
import fi.MonitorMetadata;
import fi.StopHarvestException;
import fi.ccInterfaces.PathCCInt;
import fi.farmerInterfaces.PathFarmerInt;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.ReentrantLock;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Class for the Path Sector of the farm.
 * @author Filipe Pires (85122) and João Alegria (85048)
 */

public class Path implements PathFarmerInt, PathCCInt {
    
    /**
     * Private inner class for holding useful information for tracking farmers while moving inside the Path. 
     */
    private class ConditionAndPathDepth{
        public Condition condition;
        public int position;
        public int depth;
        
        public ConditionAndPathDepth(Condition condition, int depth, int position){
            this.condition=condition;
            this.position=position;
            this.depth=depth;
        }
    } 
    
    /*
        Monitor variables
    */
    
    private FarmInfrastructure fi;
    private MonitorMetadata metadata;
    
    private ReentrantLock rl = new ReentrantLock();
    private Condition allInPath = rl.newCondition();
    
    private List<Integer> farmersOrder;
    private Integer currentFarmerToMove=null;
    private Integer path[][];
    private Map<Integer, ConditionAndPathDepth> farmersMetadata;
    private List<List<Integer>> availablePositions;
    
    private int farmersInPath;
    private boolean stopHarvest=false;
    private boolean endSimulation=false;

    /*
        Constructors
    */

    /**
     * Path monitor constructor.
     * @param fi
     * @param metadata
     */
    public Path(FarmInfrastructure fi, MonitorMetadata metadata) {
        this.fi = fi;
        this.metadata=metadata;
        this.farmersInPath = 0;
        this.farmersOrder=new ArrayList();
        this.path=new Integer[fi.pathSize][metadata.MAXNUMBERFARMERS];
        this.farmersMetadata = new HashMap();
        this.availablePositions = new ArrayList();
        
        for(int i=0; i< fi.pathSize; i++){
            this.availablePositions.add(new ArrayList());
            for(int j=0; j<metadata.MAXNUMBERFARMERS; j++){
                this.availablePositions.get(i).add(j);
            }
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
    public void farmerEnter(int farmerId) throws StopHarvestException, EndSimulationException{
        rl.lock();
        try {
            this.waitRandomDelay();
            farmersInPath++;
            farmersOrder.add(farmerId);
            farmersMetadata.put(farmerId, new ConditionAndPathDepth(rl.newCondition(), -1, -1));
            this.selectSpot(farmerId, false);
            this.fi.presentFarmerInPath(farmerId,farmersMetadata.get(farmerId).position, farmersMetadata.get(farmerId).depth);
            System.out.println("[Path] Farmer " + farmerId + " entered.");
            this.waitTimeout();
            if(farmersInPath==metadata.NUMBERFARMERS){
                currentFarmerToMove=farmersOrder.get(0);
            }
            while(farmersInPath<metadata.NUMBERFARMERS){
                allInPath.await();
                
                if(this.stopHarvest){
                    farmersInPath--;
                    path[farmersMetadata.get(farmerId).depth][farmersMetadata.get(farmerId).position]=null;
                    this.availablePositions.get(this.farmersMetadata.get(farmerId).depth).add(farmersMetadata.get(farmerId).position);
                    this.farmersOrder.remove((Integer)farmerId);
                    this.farmersMetadata.remove(farmerId);
                    if(farmersInPath==0){
                        stopHarvest=false;
                    }
                    throw new StopHarvestException();
                }
                if(this.endSimulation){
                    throw new EndSimulationException();
                }
            }
            allInPath.signalAll();
        } catch (InterruptedException ex) {
            Logger.getLogger(Path.class.getName()).log(Level.SEVERE, null, ex);
        }finally{
            rl.unlock();
        }
    }

    /**
     * 
     * @param farmerId 
     */
    @Override
    public void farmerGoToGranary(int farmerId) throws StopHarvestException, EndSimulationException{
        rl.lock();
        try {
            this.waitRandomDelay();
            while(this.farmersMetadata.get(farmerId).depth<fi.pathSize){

                while(this.currentFarmerToMove!=farmerId){

                    this.farmersMetadata.get(farmerId).condition.await();
                    
                    if(this.stopHarvest){
                        farmersInPath--;
                        path[farmersMetadata.get(farmerId).depth][farmersMetadata.get(farmerId).position]=null;
                        this.availablePositions.get(this.farmersMetadata.get(farmerId).depth).add(farmersMetadata.get(farmerId).position);
                        this.farmersOrder.remove((Integer)farmerId);
                        this.farmersMetadata.remove(farmerId);
                        if(farmersInPath==0){
                            stopHarvest=false;
                        }
                        throw new StopHarvestException();
                    }
                    if(this.endSimulation){
                        throw new EndSimulationException();
                    }
                }
                this.selectSpot(farmerId, false);
                this.waitTimeout();
                
                if(this.farmersInPath>1){
                    this.currentFarmerToMove=this.farmersOrder.get((this.farmersOrder.indexOf(farmerId)+1)%this.metadata.NUMBERFARMERS);
                    this.farmersMetadata.get(this.currentFarmerToMove).condition.signalAll();
                }

                
            }
            this.farmersInPath--;
            this.farmersOrder.remove((Integer)farmerId);
            this.farmersMetadata.remove(farmerId);

        } catch (InterruptedException ex) {
            Logger.getLogger(Path.class.getName()).log(Level.SEVERE, null, ex);
        }finally{
            rl.unlock();
        }
    }
    
    /**
     * 
     * @param farmerId 
     */
    @Override
    public void farmerReturn(int farmerId) throws StopHarvestException, EndSimulationException{
        rl.lock();
        try {
            this.waitRandomDelay();
            farmersInPath++;
            farmersOrder.add(farmerId);
            farmersMetadata.put(farmerId, new ConditionAndPathDepth(rl.newCondition(), fi.pathSize, -1));
            this.selectSpot(farmerId, true);
            this.waitTimeout();
            if(farmersInPath==metadata.NUMBERFARMERS){
                currentFarmerToMove=farmersOrder.get(0);
            }
            
            while(farmersInPath<metadata.NUMBERFARMERS){
                allInPath.await();
                
                if(this.stopHarvest){
                    farmersInPath--;
                    path[farmersMetadata.get(farmerId).depth][farmersMetadata.get(farmerId).position]=null;
                    this.availablePositions.get(this.farmersMetadata.get(farmerId).depth).add(farmersMetadata.get(farmerId).position);
                    this.farmersOrder.remove((Integer)farmerId);
                    this.farmersMetadata.remove(farmerId);
                    if(farmersInPath==0){
                        stopHarvest=false;
                    }
                    throw new StopHarvestException();
                }
                if(this.endSimulation){
                    throw new EndSimulationException();
                }
            }
            allInPath.signalAll();
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
    public void farmerGoToStorehouse(int farmerId) throws StopHarvestException, EndSimulationException{
        rl.lock();
        try {
            this.waitRandomDelay();
            while(this.farmersMetadata.get(farmerId).depth>=0){
                while(this.currentFarmerToMove!=farmerId){
                    this.farmersMetadata.get(farmerId).condition.await();
                    
                    if(this.stopHarvest){
                        farmersInPath--;
                        path[farmersMetadata.get(farmerId).depth][farmersMetadata.get(farmerId).position]=null;
                        this.availablePositions.get(this.farmersMetadata.get(farmerId).depth).add(farmersMetadata.get(farmerId).position);
                        this.farmersOrder.remove((Integer)farmerId);
                        this.farmersMetadata.remove(farmerId);
                        if(farmersInPath==0){
                            stopHarvest=false;
                        }
                        throw new StopHarvestException();
                    }
                    if(this.endSimulation){
                        throw new EndSimulationException();
                    }
                }
                this.selectSpot(farmerId, true);
                this.waitTimeout();
                if(this.farmersInPath>1){
                    this.currentFarmerToMove=this.farmersOrder.get((this.farmersOrder.indexOf(farmerId)+1)%this.metadata.NUMBERFARMERS);
                    this.farmersMetadata.get(this.currentFarmerToMove).condition.signalAll();
                }
            }
            this.farmersInPath--;
            this.farmersOrder.remove((Integer)farmerId);
            this.farmersMetadata.remove(farmerId);
        } catch (InterruptedException ex) {
            Logger.getLogger(Path.class.getName()).log(Level.SEVERE, null, ex);
        }finally{
            rl.unlock();
        }
    }
   

    /*
        Methods executed by Message Processor
    */

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
                    if(this.farmersInPath!=0){
                        this.stopHarvest=true;
                    }
                    break;
                case "endSimulation":
                    this.endSimulation=true;
                    break;
            }

            this.allInPath.signalAll();
            for(Integer key : this.farmersMetadata.keySet()){
                this.farmersMetadata.get(key).condition.signalAll();
            }
        }
        finally{
            rl.unlock();
        }
    }

    
    /*
        Aux Methods
    */
    
    /**
     * 
     * @param farmerId
     * @param reverse 
     */
    private void selectSpot(int farmerId, boolean reverse){
        int numberOfSteps=(int)((Math.random()*(metadata.NUMBERSTEPS-1))+1);
        int newDepth;
        if(reverse){
            newDepth=this.farmersMetadata.get(farmerId).depth-numberOfSteps;
        }else{
            newDepth=this.farmersMetadata.get(farmerId).depth+numberOfSteps;
        }
        if(newDepth>=fi.pathSize || newDepth<0){
            path[farmersMetadata.get(farmerId).depth][farmersMetadata.get(farmerId).position]=null;
            this.availablePositions.get(this.farmersMetadata.get(farmerId).depth).add(farmersMetadata.get(farmerId).position);
            farmersMetadata.get(farmerId).depth=newDepth;
            return;
        }
        int randomIndex=(int)(Math.random()*(this.availablePositions.get(newDepth).size()-1));
        int randomPosition=this.availablePositions.get(newDepth).get(randomIndex);
        if((this.farmersMetadata.get(farmerId).depth!=-1 && !reverse) || (this.farmersMetadata.get(farmerId).depth!=10 && reverse)){
            path[farmersMetadata.get(farmerId).depth][farmersMetadata.get(farmerId).position]=null;
            this.availablePositions.get(this.farmersMetadata.get(farmerId).depth).add(farmersMetadata.get(farmerId).position);
        }
        this.availablePositions.get(newDepth).remove(randomIndex);
        path[newDepth][randomPosition]=farmerId;
        farmersMetadata.get(farmerId).position=randomPosition;
        farmersMetadata.get(farmerId).depth=newDepth;

        this.fi.presentFarmerInPath(farmerId,farmersMetadata.get(farmerId).position, farmersMetadata.get(farmerId).depth);

    }
    
    private void waitRandomDelay(){
        try {
            int randomDelay=(int)(Math.random()*(this.metadata.MAXDELAY));
            Thread.sleep(randomDelay);
        } catch (InterruptedException ex) {
            Logger.getLogger(Storehouse.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
    
    private void waitTimeout(){
        try {
            Thread.sleep(this.metadata.TIMEOUT);
        } catch (InterruptedException ex) {
            Logger.getLogger(Path.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
    
}
