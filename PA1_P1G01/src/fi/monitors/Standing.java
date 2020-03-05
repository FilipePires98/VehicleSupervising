package fi.monitors;

import fi.MonitorMetadata;
import fi.ccInterfaces.StandingCCInt;
import fi.farmerInterfaces.StandingFarmerInt;

/**
 * Class for the Standing Sector of the farm.
 * @author Filipe Pires (85122) and João Alegria (85048)
 */
public class Standing implements StandingFarmerInt, StandingCCInt{
    
    private MonitorMetadata metadata;

    public Standing(MonitorMetadata metadata) {
        this.metadata=metadata;
    }
    

    void prepare() {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public void farmerEnter(int farmerId) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public void farmerWaitStartOrder(int farmerId) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public void waitForAllFarmers() {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public void sendStartOrder() {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public void control(String action) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }
    
    
    
    
    
}