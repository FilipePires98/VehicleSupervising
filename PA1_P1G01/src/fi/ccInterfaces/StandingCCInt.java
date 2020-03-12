package fi.ccInterfaces;

import fi.utils.EndSimulationException;
import fi.utils.StopHarvestException;
import fi.farmerInterfaces.*;

/**
 * 
 * @author Filipe Pires (85122) and João Alegria (85048)
 */
public interface StandingCCInt {
    //public void waitForAllFarmers();
    public void control(String action);
    public void sendStartOrder();
    public void waitForAllFarmers() throws StopHarvestException, EndSimulationException;
}
