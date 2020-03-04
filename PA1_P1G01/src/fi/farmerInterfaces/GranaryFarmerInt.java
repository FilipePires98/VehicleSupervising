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
public interface GranaryFarmerInt {
    public void farmerEnter(int farmerId);
    public void farmerWaitCollectOrder(int farmerId);
    public void farmerCollect(int farmerId);
    public void farmerWaitReturnOrder(int farmerId);
}
