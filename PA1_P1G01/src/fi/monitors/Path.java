package fi.monitors;

import fi.MonitorMetadata;
import fi.ccInterfaces.PathCCInt;
import fi.farmerInterfaces.PathFarmerInt;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.ReentrantLock;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Class for the Path Sector of the farm.
 * @author Filipe Pires (85122) and João Alegria (85048)
 */
public class Path implements PathFarmerInt, PathCCInt {
    
    private MonitorMetadata metadata;
    
    private int MAXFARMERS=0;
    private int currentNumFarmers=0;
    
    private ReentrantLock lock = new ReentrantLock();
    private List<Condition> farmers = new ArrayList();
    private Integer farmerPosition[][] = new Integer[5][10];
    private int currentIteration=0;
    private boolean run=true;

    public Path(MonitorMetadata metadata) {
        this.metadata=metadata;
    }

    @Override
    public void farmerEnter(int farmerId) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public void farmerGoToGranary(int farmerId) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public void farmerReturn(int farmerId) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public void farmerGoToStorehouse(int farmerId) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public void control() {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
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