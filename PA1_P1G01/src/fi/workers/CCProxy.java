package fi.workers;

import common.SocketClient;
import fi.utils.EndSimulationException;
import fi.FarmInfrastructure;
import fi.utils.StopHarvestException;
import fi.ccInterfaces.GranaryCCInt;
import fi.ccInterfaces.PathCCInt;
import fi.ccInterfaces.StandingCCInt;
import fi.ccInterfaces.StorehouseCCInt;
import common.MessageProcessor;
import fi.UiAndMainControlsFI;

/**
 * Message processor for the Farm Infrastructure.
 * @author Filipe Pires (85122) and João Alegria (85048)
 */
public class CCProxy implements MessageProcessor {
    
    
    private class ProcessingThread implements Runnable{
        
        private String message;
        
        public ProcessingThread(String message){
            this.message=message;
        }
        
        @Override
        public void run() {
            try {
                String[] processedMessage = this.message.split(";");
                switch(processedMessage[0]){
                    case "waitSimulationReady":
                        storeHouse.waitAllFarmersReady();
                        fi.sendMessage("allFarmersrReadyWaiting");
                        break;
                    case "prepareOrder":
                        storeHouse.sendSelectionAndPrepareOrder(Integer.valueOf(processedMessage[1]),Integer.valueOf(processedMessage[2]),Integer.valueOf(processedMessage[3]),Integer.valueOf(processedMessage[4]));
                        standing.waitForAllFarmers();
                        fi.sendMessage("allFarmersrReadyToStart");
                        break;
                    case "startHarvestOrder":
                        standing.sendStartOrder();
                        granary.waitAllFarmersReadyToCollect();
                        fi.sendMessage("allFarmersrReadyToCollect");
                        break;
                    case "collectOrder":
                        granary.sendCollectOrder();
                        granary.waitAllFarmersCollect();
                        fi.sendMessage("allFarmersrReadyToReturn");
                        break;
                    case "returnOrder":
                        granary.sendReturnOrder();
                        storeHouse.waitAllFarmersReady();
                        fi.sendMessage("allFarmersrReadyWaiting");
                        break;
                    case "stopHarvestOrder":
                        storeHouse.control("stopHarvest");
                        standing.control("stopHarvest");
                        path.control("stopHarvest");
                        granary.control("stopHarvest");
                        storeHouse.waitAllFarmersReady();
                        fi.sendMessage("allFarmersrReadyWaiting");
                        break;
                    case "endSimulationOrder":
                        storeHouse.control("endSimulation");
                        standing.control("endSimulation");
                        path.control("endSimulation");
                        granary.control("endSimulation");
                        fi.closeSocketClientAndUI();
                        break;
                }
            } catch (StopHarvestException ex) {
                return;
            } catch (EndSimulationException ex) {
                return;
            }
        }
    }
    
    private StorehouseCCInt storeHouse;
    private StandingCCInt standing;
    private PathCCInt path;
    private GranaryCCInt granary;
    private UiAndMainControlsFI fi;
    
    public CCProxy(UiAndMainControlsFI fi, StorehouseCCInt storeHouse,StandingCCInt standing,PathCCInt path,GranaryCCInt granary) {
        this.storeHouse=storeHouse;
        this.standing=standing;
        this.path=path;
        this.granary=granary;
        this.fi=fi;

    }

    @Override
    public void processMessage(String message) {
        Thread processor = new Thread(new ProcessingThread(message));
        processor.start();
    }
    
    
}
