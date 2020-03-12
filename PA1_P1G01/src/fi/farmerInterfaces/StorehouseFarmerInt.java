package fi.farmerInterfaces;

import fi.EndSimulationException;
import fi.StopHarvestException;

/**
 * 
 * @author Filipe Pires (85122) and João Alegria (85048)
 */
public interface StorehouseFarmerInt {
    public void farmerEnter(int farmerId) throws StopHarvestException, EndSimulationException;
    public void farmerWaitPrepareOrder(int farmerId) throws StopHarvestException, EndSimulationException;
}
