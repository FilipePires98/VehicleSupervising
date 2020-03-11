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
public class CCMessageProcessor implements MessageProcessor {
    
    private ControlCenter cc;
    
    private String message;

    public CCMessageProcessor(ControlCenter cc) {
        this.cc=cc;
    }
    

    @Override
    public void defineMessage(String message) {
        this.message=message;
    }

    @Override
    public void run() {
        switch(message){
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
        }
    }
    
}
