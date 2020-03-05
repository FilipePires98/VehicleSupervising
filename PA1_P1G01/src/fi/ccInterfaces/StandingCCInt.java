/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package fi.ccInterfaces;

import fi.farmerInterfaces.*;

/**
 *
 * @author joaoalegria
 */
public interface StandingCCInt {
    
    public void waitForAllFarmers();
    public void sendStartOrder();
    public void control(String action);
    
}
