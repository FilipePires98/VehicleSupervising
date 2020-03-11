package fi.ccInterfaces;

import fi.EndSimulationException;
import fi.StopHarvestException;
import fi.farmerInterfaces.*;

/**
 * 
 * @author Filipe Pires (85122) and João Alegria (85048)
 */
public interface StandingCCInt {
    
    //public void waitForAllFarmers();
    public void sendStartOrder();
    public void waitForAllFarmers() throws StopHarvestException, EndSimulationException;
    public void control(String action);
    
}
