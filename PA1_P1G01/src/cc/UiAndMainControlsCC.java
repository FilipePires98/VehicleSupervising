package cc;

import fi.*;

/**
 *
 * @author joaoalegria
 */
public interface UiAndMainControlsCC {
    public void presentFarmerInStorehouse(int farmerId, int position);

    public void presentFarmerInStandingArea(int farmerId, int position);

    public void presentFarmerInPath(int farmerId, int position, int column);

    public void presentFarmerInGranary(int farmerId, int position);

    public void presentCollectingFarmer(int farmerId);
    
    public void updateGranaryCornCobs(int actualNumber);
    
    public void updateStorehouseCornCobs(int actualNumber);
    
    public void initFIClient();
    
    public void enableStartBtn();
    
    public void enableCollectBtn();
    
    public void enableReturnBtn();
    
    public void enablePrepareBtn();
    
    public void closeSocketClientAndUI();
}
