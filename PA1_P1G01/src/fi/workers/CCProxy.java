package fi.workers;

import common.SocketClient;
import fi.utils.EndSimulationException;
import fi.FarmInfrastructure;
import fi.utils.StopHarvestException;
import fi.ccInterfaces.GranaryCCInt;
import fi.ccInterfaces.PathCCInt;
import fi.ccInterfaces.StandingCCInt;
import fi.ccInterfaces.StorehouseCCInt;
import java.util.logging.Level;
import java.util.logging.Logger;
import common.MessageProcessorThread;
import fi.UiAndMainControlsFI;

/**
 * Message processor for the Farm Infrastructure.
 * @author Filipe Pires (85122) and João Alegria (85048)
 */
public class CCProxy implements MessageProcessorThread {
    
    private StorehouseCCInt storeHouse;
    private StandingCCInt standing;
    private PathCCInt path;
    private GranaryCCInt granary;
    private UiAndMainControlsFI fi;
    
    private String message;

    public CCProxy(UiAndMainControlsFI fi, StorehouseCCInt storeHouse,StandingCCInt standing,PathCCInt path,GranaryCCInt granary) {
        this.storeHouse=storeHouse;
        this.standing=standing;
        this.path=path;
        this.granary=granary;
        this.fi=fi;

    }

    @Override
    public void defineMessage(String message) {
        this.message=message;
    }

    @Override
    public void run() {
        try {
            String[] processedMessage = message.split(";");
            switch(processedMessage[0]){
                case "waitSimulationReady":
                    this.storeHouse.waitAllFarmersReady();
                    this.fi.sendMessage("allFarmersrReadyWaiting");
                    break;
                case "prepareOrder":
                    this.storeHouse.sendSelectionAndPrepareOrder(Integer.valueOf(processedMessage[1]),Integer.valueOf(processedMessage[2]),Integer.valueOf(processedMessage[3]),Integer.valueOf(processedMessage[4]));
                    this.standing.waitForAllFarmers();
                    this.fi.sendMessage("allFarmersrReadyToStart");
                    break;
                case "startHarvestOrder":
                    this.standing.sendStartOrder();
                    this.granary.waitAllFarmersReadyToCollect();
                    this.fi.sendMessage("allFarmersrReadyToCollect");
                    break;
                case "collectOrder":
                    this.granary.sendCollectOrder();
                    this.granary.waitAllFarmersCollect();
                    this.fi.sendMessage("allFarmersrReadyToReturn");
                    break;
                case "returnOrder":
                    this.granary.sendReturnOrder();
                    this.storeHouse.waitAllFarmersReady();
                    this.fi.sendMessage("allFarmersrReadyWaiting");
                    break;
                case "stopHarvestOrder":
                    this.storeHouse.control("stopHarvest");
                    this.standing.control("stopHarvest");
                    this.path.control("stopHarvest");
                    this.granary.control("stopHarvest");
                    this.storeHouse.waitAllFarmersReady();
                    this.fi.sendMessage("allFarmersrReadyWaiting");
                    break;
                case "endSimulationOrder":
                    this.storeHouse.control("endSimulation");
                    this.standing.control("endSimulation");
                    this.path.control("endSimulation");
                    this.granary.control("endSimulation");
                    this.fi.closeSocketClientAndUI();
                    break;
            }
        } catch (StopHarvestException ex) {
            return;
        } catch (EndSimulationException ex) {
            return;
        }
    }

    
    
    
}
