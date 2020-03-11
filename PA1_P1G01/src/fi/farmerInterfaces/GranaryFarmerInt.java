package fi.farmerInterfaces;

import fi.EndSimulationException;
import fi.StopHarvestException;

/**
 * 
 * @author Filipe Pires (85122) and João Alegria (85048)
 */
public interface GranaryFarmerInt {
    public void farmerEnter(int farmerId) throws StopHarvestException, EndSimulationException;
    public void farmerWaitCollectOrder(int farmerId) throws StopHarvestException, EndSimulationException;
    public void farmerCollect(int farmerId) throws StopHarvestException, EndSimulationException;
    public void farmerWaitReturnOrder(int farmerId) throws StopHarvestException, EndSimulationException;
}
