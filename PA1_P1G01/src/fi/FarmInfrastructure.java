package fi;

import java.io.InputStream;
import java.io.OutputStream;

/**
 * Class for the Farm Infrastructure Server for the agricultural harvest.
 * @author Filipe Pires (85122) and João Alegria (85048)
 */
public class FarmInfrastructure extends Process {

    public FarmInfrastructure() {
        System.out.print("Hello world!");
    }

    @Override
    public OutputStream getOutputStream() {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public InputStream getInputStream() {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public InputStream getErrorStream() {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public int waitFor() throws InterruptedException {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public int exitValue() {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public void destroy() {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }
    
}
