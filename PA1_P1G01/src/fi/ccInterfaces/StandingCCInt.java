package fi.ccInterfaces;

import fi.farmerInterfaces.*;

/**
 * 
 * @author Filipe Pires (85122) and João Alegria (85048)
 */
public interface StandingCCInt {
    
    public void waitForAllFarmers();
    public void sendStartOrder();
    public void control(String action);
    
}
