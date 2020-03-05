package fi.monitors;

import fi.MonitorMetadata;
import fi.ccInterfaces.StorehouseCCInt;
import fi.farmerInterfaces.StorehouseFarmerInt;

/**
 * Class for the Storehouse Sector of the farm.
 * @author Filipe Pires (85122) and João Alegria (85048)
 */
public class Storehouse implements StorehouseFarmerInt, StorehouseCCInt{

    private MonitorMetadata metadata;
    
    public Storehouse(MonitorMetadata metadata) {
        this.metadata=metadata;
    }

    @Override
    public void control(String action) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public void sendSelectionAndPrepareOrder(int numberOfFarmers) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public void waitAllFarmersReady() {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public void farmerEnter(int farmerId) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public void farmerWaitPrepareOrder(int farmerId) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

}