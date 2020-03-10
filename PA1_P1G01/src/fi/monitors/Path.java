package fi.monitors;

import fi.MonitorMetadata;
import fi.ccInterfaces.PathCCInt;
import fi.farmerInterfaces.PathFarmerInt;
import java.util.ArrayList;
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
    
    private MonitorMetadata metadata;
    private int pathLength;
    
    private ReentrantLock rl = new ReentrantLock();
    private Condition allInPath = rl.newCondition();
    
    private int farmersInPath;
    private List<Integer> farmersOrder;
    private Integer currentFarmerToMove=null;
    private Integer path[][];
    private Map<Integer, ConditionAndPathDepth> farmersMetadata;

    /*
        Constructors
    */

    /**
     * Path monitor constructor.
     * @param metadata
     * @param pathLength 
     */
    public Path(MonitorMetadata metadata, int pathLength) {
        this.metadata=metadata;
        this.farmersInPath = 0;
        this.farmersOrder=new ArrayList();
        this.pathLength=pathLength;
        this.path=new Integer[pathLength][metadata.MAXNUMBERFARMERS];
        this.farmersMetadata = new HashMap();
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
            System.out.println(metadata.NUMBERFARMERS);

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
                this.currentFarmerToMove=(this.farmersOrder.indexOf(farmerId)+1)%this.pathLength;
                this.farmersMetadata.get(this.currentFarmerToMove).condition.signalAll();
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
                this.currentFarmerToMove=(this.farmersOrder.indexOf(farmerId)+1)%this.pathLength;
                this.farmersMetadata.get(this.currentFarmerToMove).condition.signalAll();
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
        int randomPosition=(int)Math.random()*(this.metadata.NUMBERFARMERS-1);
        while(path[newDepth][randomPosition]!=null){
            randomPosition=(int)Math.random()*(this.metadata.NUMBERFARMERS-1);
        }
        path[farmersMetadata.get(farmerId).depth][farmersMetadata.get(farmerId).position]=null;
        path[newDepth][randomPosition]=farmerId;
        farmersMetadata.get(farmerId).position=randomPosition;
        farmersMetadata.get(farmerId).depth=newDepth;
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