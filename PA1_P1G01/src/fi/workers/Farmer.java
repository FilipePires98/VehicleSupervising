package fi.workers;

import fi.farmerInterfaces.GranaryFarmerInt;
import fi.farmerInterfaces.PathFarmerInt;
import fi.farmerInterfaces.StandingFarmerInt;
import fi.farmerInterfaces.StorehouseFarmerInt;

/**
 * Class for the Farmer Thread for the agricultural harvest.
 * @author Filipe Pires (85122) and João Alegria (85048)
 */
public class Farmer extends Thread {
    
    private FarmerState state;
    private StorehouseFarmerInt storeHouse;
    private StandingFarmerInt standing;
    private PathFarmerInt path;
    private GranaryFarmerInt granary;
    private final int id;

    public Farmer(int id,StorehouseFarmerInt storeHouse,StandingFarmerInt standing,PathFarmerInt path,GranaryFarmerInt granary) {
        this.storeHouse=storeHouse;
        this.standing=standing;
        this.path=path;
        this.granary=granary;
        this.id=id;
    }
    
    @Override
    public void run() {
        while(true){
            this.state=FarmerState.INITIAL;
            this.storeHouse.farmerEnter(id);
            this.storeHouse.farmerWaitPrepareOrder(id);
            this.state=FarmerState.PREPARE;
            this.standing.farmerEnter(id);
            this.standing.farmerWaitStartOrder(id);
            this.state=FarmerState.WALK;
            this.path.farmerEnter(id);
            this.path.farmerGoToGranary(id);
            this.granary.farmerEnter(id);
            this.state=FarmerState.WAITTOCOLLECT;
            this.granary.farmerWaitCollectOrder(id);
            this.state=FarmerState.COLLECT;
            this.granary.farmerCollect(id);
            this.state=FarmerState.WAITTORETURN;
            this.granary.farmerWaitReturnOrder(id);
            this.state=FarmerState.RETURN;
            this.path.farmerReturn(id);
            this.path.farmerGoToStorehouse(id);
        }
    }
    
    /*
    private boolean nextStep(int step) {
        if() {
            
        }
        switch(step) {
            case 1:
                this.state=FarmerState.INITIAL;
                this.storeHouse.farmerEnter(id);
                break;
            case 2:
                this.storeHouse.farmerWaitPrepareOrder(id);
                break;
            // ...
        }
        return false;
    }
    */

    public int getID() {
        return id;
    }
    
    
    
}
