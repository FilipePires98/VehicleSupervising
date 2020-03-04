/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package fi.farmerInterfaces;

/**
 *
 * @author joaoalegria
 */
public interface PathFarmerInt {
    
    public void farmerEnter(int farmerId);
    public void farmerGoToGranary(int farmerId);
    public void farmerReturn(int farmerId);
    public void farmerGoToStorehouse(int farmerId);
    
}
