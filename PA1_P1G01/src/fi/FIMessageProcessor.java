/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package fi;

import common.MessageProcessor;

/**
 *
 * @author joaoalegria
 */
public class FIMessageProcessor extends Thread implements MessageProcessor {
    
    private Storehouse storeHouse;
    private Standing standing;
    private Path path;
    private Granary granary;

    public FIMessageProcessor(Storehouse storeHouse,Standing standing,Path path,Granary granary) {
        this.storeHouse=storeHouse;
        this.standing=standing;
        this.path=path;
        this.granary=granary;
    }

    
    
    @Override
    public void process(String message) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }
    
}
