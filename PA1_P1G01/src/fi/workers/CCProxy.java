package fi.workers;

import common.MessageProcessor;
import fi.monitors.Granary;
import fi.monitors.Path;
import fi.monitors.Standing;
import fi.monitors.Storehouse;

/**
 * 
 * @author Filipe Pires (85122) and João Alegria (85048)
 */
public class CCProxy extends Thread implements MessageProcessor {
    
    private Storehouse storeHouse;
    private Standing standing;
    private Path path;
    private Granary granary;

    public CCProxy(Storehouse storeHouse,Standing standing,Path path,Granary granary) {
        this.storeHouse=storeHouse;
        this.standing=standing;
        this.path=path;
        this.granary=granary;
    }

    
    
    @Override
    public void process(String message) {
        switch(message) {
            // ...
                
        }
    }
    
}
