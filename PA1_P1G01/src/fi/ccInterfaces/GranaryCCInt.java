package fi.ccInterfaces;

/**
 *
 * @author joaoalegria
 */
public interface GranaryCCInt {
    public void waitAllFarmersReadyToCollect();
    public void sendCollectOrder();
    public void waitAllFarmersCollect();
    public void sendReturnOrder();
    public void control(String action);
}