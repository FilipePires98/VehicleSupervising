package fi.workers;

import common.MessageProcessor;
import fi.ccInterfaces.GranaryCCInt;
import fi.ccInterfaces.PathCCInt;
import fi.ccInterfaces.StandingCCInt;
import fi.ccInterfaces.StorehouseCCInt;

/**
 * Message processor for the Farm Infrastructure.
 * @author Filipe Pires (85122) and João Alegria (85048)
 */
public class CCProxy extends Thread implements MessageProcessor {
    
    private StorehouseCCInt storeHouse;
    private StandingCCInt standing;
    private PathCCInt path;
    private GranaryCCInt granary;

    public CCProxy(StorehouseCCInt storeHouse,StandingCCInt standing,PathCCInt path,GranaryCCInt granary) {
        this.storeHouse=storeHouse;
        this.standing=standing;
        this.path=path;
        this.granary=granary;
    }

    
    @Override
    public void process(String message) {
        String[] processedMessage = message.split(";");
        switch(processedMessage[0]){
            case "simulationready":
                this.storeHouse.waitAllFarmersReady();
                break;
            case "prepareOrder":
                if(processedMessage.length<5) {
                    System.err.println("[FI]: Error when processing 'prepareOrder' message. Unable to start simulation.");
                }
                this.storeHouse.sendSelectionAndPrepareOrder(Integer.valueOf(processedMessage[1]),Integer.valueOf(processedMessage[2]),Integer.valueOf(processedMessage[3]),Integer.valueOf(processedMessage[4]));
                break;
            case "startHarvestOrder":
                this.standing.sendStartOrder();
                break;
            case "collectOrder":
                this.granary.sendCollectOrder();
                break;
            case "returnOrder":
                this.granary.sendReturnOrder();
                this.storeHouse.waitAllFarmersReady();
                break;
            case "stopHarvestOrder":
                this.storeHouse.control("stopHarvest");
                this.standing.control("stopHarvest");
                this.path.control("stopHarvest");
                this.granary.control("stopHarvest");
                this.storeHouse.waitAllFarmersReady();
                break;
            case "endSimulationOrder":
                this.storeHouse.control("endSimulation");
                this.standing.control("endSimulation");
                this.path.control("endSimulation");
                this.granary.control("endSimulation");
                break;
        }
    }
    
}
