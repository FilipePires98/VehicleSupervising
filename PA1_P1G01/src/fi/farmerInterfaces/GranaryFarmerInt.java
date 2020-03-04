package fi.farmerInterfaces;

/**
 * 
 * @author Filipe Pires (85122) and João Alegria (85048)
 */
public interface GranaryFarmerInt {
    public void farmerEnter(int farmerId);
    public void farmerWaitCollectOrder(int farmerId);
    public void farmerCollect(int farmerId);
    public void farmerWaitReturnOrder(int farmerId);
}
