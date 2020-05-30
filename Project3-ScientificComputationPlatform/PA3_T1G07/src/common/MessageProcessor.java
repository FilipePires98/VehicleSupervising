package common;

/**
 * Interface defining the service the future implementation of message processor must provide.
 * @author Filipe Pires (85122) and João Alegria (85048)
 */
public interface MessageProcessor {

    /**
     * Defines the method to use when processing incoming messages.
     * @param message String containing the incoming message
     * @return String containing the acknowledge message intended to be returned
     */
    String processMessage(String message);
    
    /**
     * Updates the class's socket status.
     * @param status int representing the status of the socket.
     */
    void setSocketStatus(int status);
    
}
