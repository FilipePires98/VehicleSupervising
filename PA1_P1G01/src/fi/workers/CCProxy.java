package fi.workers;

import common.MessageProcessor;
import common.SocketClient;
import fi.FarmInfrastructure;
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
    private SocketClient ccClient;

    public CCProxy(SocketClient ccClient, StorehouseCCInt storeHouse,StandingCCInt standing,PathCCInt path,GranaryCCInt granary) {
        this.storeHouse=storeHouse;
        this.standing=standing;
        this.path=path;
        this.granary=granary;
        this.ccClient=ccClient;

    }

    
    @Override
    public void process(String message) {
        String[] processedMessage = message.split(";");
        switch(processedMessage[0]){
            case "waitSimulationReady":
                this.storeHouse.waitAllFarmersReady();
                this.ccClient.send("allFarmersrReadyWaiting");
                break;
            case "prepareOrder":
                this.storeHouse.sendSelectionAndPrepareOrder(Integer.valueOf(processedMessage[1]),Integer.valueOf(processedMessage[2]),Integer.valueOf(processedMessage[3]),Integer.valueOf(processedMessage[4]));
                this.standing.waitForAllFarmers();
                this.ccClient.send("allFarmersrReadyToStart");
                break;
            case "startHarvestOrder":
                this.standing.sendStartOrder();
                this.granary.waitAllFarmersReadyToCollect();
                this.ccClient.send("allFarmersrReadyToCollect");
                break;
            case "collectOrder":
                this.granary.sendCollectOrder();
                this.granary.waitAllFarmersCollect();
                this.ccClient.send("allFarmersrReadyToReturn");
                break;
            case "returnOrder":
                this.granary.sendReturnOrder();
                this.storeHouse.waitAllFarmersReady();
                this.ccClient.send("allFarmersrReadyWaiting");
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
