package message;

/**
 * Class for Kafka messages.
 * A message instance contains the register code of the vehicle that generated the message, the time instance when the message was created, the message type and an additional parameter that varies according to the message type.
 * It supports three distinct message types, each with its specific purpose:
 * 
 * - Heartbeat - the simplest type of message meant to notify the system that the car is still connected.
 * 
 * - Speed - message meant to inform the system about the current speed of the car.
 * 
 * - Status - messages meant to detect whether the smart sensor detects any malfunction.
 * 
 * @author Filipe Pires (85122) and João Alegria (85048)
 */
public class Message {
    
    /**
     * Car register code.
     */
    private String car_reg;
    /**
     * Time instance when the message was created.
     */
    private long timestamp;
    /**
     * Message type (0=heartbeat, 1=speed, 2=status).
     */
    private int type;
    /**
     * Car velocity (Km/h).
     */
    private int speed;
    /**
     * Car status (OK or KO).
     */
    private String car_status;

    /**
     * Message constructor for messages of type Heartbeat.
     * 
     * @param car_reg car register code
     * @param timestamp time instance when the message was created
     * @param type message type
     */
    public Message(String car_reg, long timestamp, int type) {
        this.car_reg = car_reg;
        this.timestamp = timestamp;
        this.type = type;
    }

    /**
     * Message constructor for messages of type Speed.
     * 
     * @param car_reg car register code
     * @param timestamp time instance when the message was created
     * @param type message type
     * @param speed car velocity (Km/h)
     */
    public Message(String car_reg, long timestamp, int type, int speed) {
        this.car_reg = car_reg;
        this.timestamp = timestamp;
        this.type = type;
        this.speed = speed;
    }

    /**
     * Message constructor for messages of type Status.
     * 
     * @param car_reg car register code
     * @param timestamp time instance when the message was created
     * @param type message type
     * @param car_status car status (OK or KO)
     */
    public Message(String car_reg, long timestamp, int type, String car_status) {
        this.car_reg = car_reg;
        this.timestamp = timestamp;
        this.type = type;
        this.car_status = car_status;
    }

    /**
     * Retrieves register code of the car that originated the message.
     * @return car register code
     */
    public String getCar_reg() {
        return car_reg;
    }

    /**
     * Retrieves time instance when the message was created.
     * @return time instance
     */
    public long getTimestamp() {
        return timestamp;
    }

    /**
     * Retrieves message type.
     * @return message type
     */
    public int getType() {
        return type;
    }

    /**
     * Retrieves car velocity (Km/h).
     * @return car speed
     */
    public int getSpeed() {
        return speed;
    }

    /**
     * Retrieves car status (OK or KO).
     * @return car status
     */
    public String getCar_status() {
        return car_status;
    }

    /**
     * Retrieves string representation of the message, in the same format present in CAR.TXT.
     * @return message
     */
    @Override
    public String toString() {
        String message="";
        switch(type){
            case 0:
                message="| "+ car_reg + " | " + timestamp + " | " + type + " |";
                break;
            case 1:
                message="| "+ car_reg + " | " + timestamp + " | " + type + " | " + speed + " |";
                break;
            case 2:
                message="| "+ car_reg + " | " + timestamp + " | " + type + " | " + car_status + " |";
                break;
        }
        return message;
    }
    
}
