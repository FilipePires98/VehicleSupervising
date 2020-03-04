package fi.monitors;

import fi.MonitorMetadata;
import fi.ccInterfaces.GranaryCCInt;
import fi.farmerInterfaces.GranaryFarmerInt;

/**
 * Class for the Granary Sector of the farm.
 * @author Filipe Pires (85122) and João Alegria (85048)
 */
public class Granary implements GranaryFarmerInt, GranaryCCInt{
    
    private MonitorMetadata metadata;

    public Granary(MonitorMetadata metadata) {
        this.metadata=metadata;
    }

    @Override
    public void farmerEnter(int farmerId) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public void farmerWaitCollectOrder(int farmerId) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public void farmerCollect(int farmerId) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public void farmerWaitReturnOrder(int farmerId) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public void waitAllFarmersReadyToCollect() {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public void sendCollectOrder() {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public void waitAllFarmersCollect() {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public void sendReturnOrder() {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public void control() {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }
}