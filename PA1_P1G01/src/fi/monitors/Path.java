package fi.monitors;

import fi.FarmInfrastructure;
import fi.MonitorMetadata;
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
        
        public ConditionAndPathDepth(Condition condition, int position, int depth){
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
    private int pathLength;
    
    private ReentrantLock rl = new ReentrantLock();
    private Condition allInPath = rl.newCondition();
    
    private int farmersInPath;
    private List<Integer> farmersOrder;
    private Integer currentFarmerToMove=null;
    private Integer path[][];
    private Map<Integer, ConditionAndPathDepth> farmersMetadata;
    private List<List<Integer>> availablePositions;

    /*
        Constructors
    */

    /**
     * Path monitor constructor.
     * @param fi
     * @param metadata
     * @param pathLength 
     */
    public Path(FarmInfrastructure fi, MonitorMetadata metadata, int pathLength) {
        this.fi = fi;
        this.metadata=metadata;
        this.farmersInPath = 0;
        this.farmersOrder=new ArrayList();
        this.pathLength=pathLength;
        this.path=new Integer[pathLength][metadata.MAXNUMBERFARMERS];
        this.farmersMetadata = new HashMap();
        this.availablePositions = new ArrayList();
        
        for(int i=0; i< pathLength; i++){
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
    public void farmerEnter(int farmerId) {
        rl.lock();
        try {
            farmersInPath++;
            farmersOrder.add(farmerId);
            farmersMetadata.put(farmerId, new ConditionAndPathDepth(rl.newCondition(), -1, -1));
            this.selectSpot(farmerId, false);

            if(farmersInPath==metadata.NUMBERFARMERS){
                allInPath.signalAll();
                currentFarmerToMove=farmersOrder.get(0);
            }
            while(farmersInPath<metadata.NUMBERFARMERS){
                allInPath.await();
            }
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
    public void farmerGoToGranary(int farmerId) {
        rl.lock();
        try {
            while(this.farmersMetadata.get(farmerId).depth<this.pathLength){
                while(this.currentFarmerToMove!=farmerId){
                    this.farmersMetadata.get(farmerId).condition.await();
                }
                this.selectSpot(farmerId, false);
                
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
    public void farmerReturn(int farmerId) {
        rl.lock();
        try {
            farmersInPath++;
            farmersOrder.add(farmerId);
            farmersMetadata.put(farmerId, new ConditionAndPathDepth(rl.newCondition(), this.pathLength, -1));
            this.selectSpot(farmerId, true);
            if(farmersInPath==metadata.NUMBERFARMERS){
                allInPath.signalAll();
                currentFarmerToMove=farmersOrder.get(0);
            }else{
                while(farmersInPath<metadata.NUMBERFARMERS){
                    allInPath.await();
                }
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
    public void farmerGoToStorehouse(int farmerId) {
        rl.lock();
        try {
            while(this.farmersMetadata.get(farmerId).depth>0){
                while(this.currentFarmerToMove!=farmerId){
                    this.farmersMetadata.get(farmerId).condition.await();
                }
                this.selectSpot(farmerId, true);
                if(this.farmersInPath>1){
                    this.currentFarmerToMove=this.farmersOrder.get((this.farmersOrder.indexOf(farmerId)+1)%this.metadata.NUMBERFARMERS);
                    this.farmersMetadata.get(this.currentFarmerToMove).condition.signalAll();
                }
            }
            this.farmersInPath--;
            this.farmersOrder.remove(farmerId);
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
        if(newDepth>=this.pathLength){
            path[farmersMetadata.get(farmerId).depth][farmersMetadata.get(farmerId).position]=null;
            farmersMetadata.get(farmerId).depth=newDepth;
            return;
        }
        int randomIndex=(int)Math.random()*(this.availablePositions.get(newDepth).size()-1);
        int randomPosition=this.availablePositions.get(newDepth).get(randomIndex);
        if(this.farmersMetadata.get(farmerId).depth!=-1){
            path[farmersMetadata.get(farmerId).depth][farmersMetadata.get(farmerId).position]=null;
        }
        this.availablePositions.get(newDepth).remove(randomIndex);
        path[newDepth][randomPosition]=farmerId;
        farmersMetadata.get(farmerId).position=randomPosition;
        farmersMetadata.get(farmerId).depth=newDepth;
        this.availablePositions.get(this.farmersMetadata.get(farmerId).depth).add(farmersMetadata.get(farmerId).position);

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
        switch(action){
            case "stopHarvest":
                break;
            case "endSimulation":
                break;
        }
    }

    
}






//lock.lock();
//        this.currentNumFarmers+=1;
//        try{
//            Condition farmerCondition = lock.newCondition();
//            farmers.add(farmerCondition);
//            int randomIndex;
//            while(this.run){
//                randomIndex = (int)Math.round(Math.random()*5);
//                while(farmerPosition[randomIndex][currentIteration]!=null){
//                    randomIndex = (int)Math.round(Math.random()*5);
//                }
//                farmerPosition[randomIndex][currentIteration]=1;
//                if(this.currentNumFarmers==this.MAXFARMERS){
//                    farmers.get(0).signal();
//                    farmerCondition.await();
//                }else{
//                    farmerCondition.await();
//                }
//            }
//        } catch (InterruptedException ex) {
//            Logger.getLogger(Path.class.getName()).log(Level.SEVERE, null, ex);
//        }finally{
//            lock.unlock();
//        }