package fi.ccInterfaces;

/**
 * 
 * @author Filipe Pires (85122) and João Alegria (85048)
 */
public interface GranaryCCInt {
    public void waitAllFarmersReadyToCollect();
    public void sendCollectOrder();
    public void waitAllFarmersCollect();
    public void sendReturnOrder();
    public void control(String action);
}