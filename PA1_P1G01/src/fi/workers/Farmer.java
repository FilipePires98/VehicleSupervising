package fi.workers;

import fi.EndSimulationException;
import fi.StopHarvestException;
import fi.farmerInterfaces.GranaryFarmerInt;
import fi.farmerInterfaces.PathFarmerInt;
import fi.farmerInterfaces.StandingFarmerInt;
import fi.farmerInterfaces.StorehouseFarmerInt;
import java.util.logging.Level;
import java.util.logging.Logger;

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
    private int cornCobs=0;

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
            try {
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
            } catch (StopHarvestException ex) {
                
            } catch (EndSimulationException ex) {
                System.out.println("Farmer "+this.id+" exited with success!");
                return;
            }
        }
    }
    
    public int getID(){
        return this.id;
    }

    public int getCornCobs() {
        return cornCobs;
    }

    public void setCornCobs(int cornCobs) {
        this.cornCobs = cornCobs;
    }
    
    
}
