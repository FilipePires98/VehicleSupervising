package fi;

/**
 *
 * @author joaoalegria
 */
public interface UiAndMainControlsFI {
    public void presentFarmerInStorehouse(int farmerId, int position);

    public void presentFarmerInStandingArea(int farmerId, int position);

    public void presentFarmerInPath(int farmerId, int position, int column);

    public void presentFarmerInGranary(int farmerId, int position);

    public void presentCollectingFarmer(int farmerId);
    
    public void updateGranaryCornCobs(int actualNumber);
    
    public void updateStorehouseCornCobs(int actualNumber);
    
    public void sendMessage(String message);
    
    public void closeSocketClientAndUI();
}
