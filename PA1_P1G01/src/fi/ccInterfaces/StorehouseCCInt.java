package fi.ccInterfaces;

import fi.farmerInterfaces.*;

/**
 * 
 * @author Filipe Pires (85122) and João Alegria (85048)
 */
public interface StorehouseCCInt {
    
    public void control(String action);
    public void sendSelectionAndPrepareOrder(int numberOfFarmers);
    public void waitAllFarmersReady();
    
}
