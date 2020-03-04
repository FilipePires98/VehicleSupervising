package fi.farmerInterfaces;

/**
 * 
 * @author Filipe Pires (85122) and João Alegria (85048)
 */
public interface PathFarmerInt {
    
    public void farmerEnter(int farmerId);
    public void farmerGoToGranary(int farmerId);
    public void farmerReturn(int farmerId);
    public void farmerGoToStorehouse(int farmerId);
    
}
