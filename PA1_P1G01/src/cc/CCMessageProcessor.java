/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package cc;

import common.MessageProcessor;

/**
 *
 * @author joaoalegria
 */
public class CCMessageProcessor {
    
    private ControlCenter cc;
    
    private String message;

    public CCMessageProcessor(ControlCenter cc) {
        this.cc=cc;
    }
    

    public void defineMessage(String message) {
        this.message=message;
    }

    public void run() {
        String[] processedMessage = message.split(";");
        switch(processedMessage[0]){
            case "presentInStorehouse":
                this.cc.presentFarmerInStorehouse(Integer.valueOf(processedMessage[1]), Integer.valueOf(processedMessage[2]));
                break;
            case "presentInStanding":
                this.cc.presentFarmerInStandingArea(Integer.valueOf(processedMessage[1]), Integer.valueOf(processedMessage[2]));
                break;
            case "presentInPath":
                this.cc.presentFarmerInPath(Integer.valueOf(processedMessage[1]), Integer.valueOf(processedMessage[2]), Integer.valueOf(processedMessage[3]));
                break;
            case "presentInGranary":
                this.cc.presentFarmerInGranary(Integer.valueOf(processedMessage[1]), Integer.valueOf(processedMessage[2]));
                break;
            case "presentInCollecting":
                this.cc.presentCollectingFarmer(Integer.valueOf(processedMessage[1]));
                break;
            case "updateGranaryCobs":
                this.cc.updateGranaryCornCobs(Integer.valueOf(processedMessage[1]));
                break;
            case "updateStorehouseCobs":
                this.cc.updateStorehouseCornCobs(Integer.valueOf(processedMessage[1]));
                break;
            case "infrastructureServerOnline":
                this.cc.initFIClient();
                break;
            case "allFarmersrReadyToStart":
                this.cc.enableStartBtn();
                break;
            case "allFarmersrReadyToCollect":
                this.cc.enableCollectBtn();
                break;
            case "allFarmersrReadyToReturn":
                this.cc.enableReturnBtn();
                break;
            case "allFarmersrReadyWaiting":
                this.cc.enablePrepareBtn();
                break;
            case "endSimulationOrder":
                this.cc.closeSocketClientAndUI();
                break;
        }
    }
    
}
