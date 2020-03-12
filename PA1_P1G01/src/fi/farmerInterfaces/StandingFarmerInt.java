package fi.farmerInterfaces;

import fi.EndSimulationException;
import fi.StopHarvestException;

/**
 * 
 * @author Filipe Pires (85122) and João Alegria (85048)
 */
public interface StandingFarmerInt {
    public void farmerEnter(int farmerId) throws StopHarvestException, EndSimulationException;
    public void farmerWaitStartOrder(int farmerId) throws StopHarvestException, EndSimulationException;
}
