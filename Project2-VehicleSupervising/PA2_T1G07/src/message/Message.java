package message;

/**
 *
 * @author joaoalegria
 */
public class Message {
    private String car_reg;
    private long timestamp;
    private int type;
    private int speed;
    private String car_status;

    public Message(String car_reg, long timestamp, int type) {
        this.car_reg = car_reg;
        this.timestamp = timestamp;
        this.type = type;
    }

    public Message(String car_reg, long timestamp, int type, int speed) {
        this.car_reg = car_reg;
        this.timestamp = timestamp;
        this.type = type;
        this.speed = speed;
    }

    public Message(String car_reg, long timestamp, int type, String car_status) {
        this.car_reg = car_reg;
        this.timestamp = timestamp;
        this.type = type;
        this.car_status = car_status;
    }

    public String getCar_reg() {
        return car_reg;
    }

    public long getTimestamp() {
        return timestamp;
    }

    public int getType() {
        return type;
    }

    public int getSpeed() {
        return speed;
    }

    public String getCar_status() {
        return car_status;
    }
    
    
}
