package fi.workers;

import common.MessageProcessor;
import fi.ccInterfaces.GranaryCCInt;
import fi.ccInterfaces.PathCCInt;
import fi.ccInterfaces.StandingCCInt;
import fi.ccInterfaces.StorehouseCCInt;

/**
 * 
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
            case "prepareOrder":
                this.storeHouse.sendSelectionAndPrepareOrder(5);
                break;
            case "startHarvestOrder":
                this.standing.sendStartOrder();
                break;
            case "collectOrder":
                this.granary.sendCollectOrder();
                break;
            case "returnOrder":
                this.granary.sendReturnOrder();
                break;
            case "stopHarvestOrder":
                this.storeHouse.control("stopHarvest");
                this.standing.control("stopHarvest");
                this.path.control("stopHarvest");
                this.granary.control("stopHarvest");
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
