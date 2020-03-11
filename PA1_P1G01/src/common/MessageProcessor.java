package common;

/**
 * 
 * @author Filipe Pires (85122) and João Alegria (85048)
 */
public interface MessageProcessor extends Runnable {

    void defineMessage(String message);
    
}
