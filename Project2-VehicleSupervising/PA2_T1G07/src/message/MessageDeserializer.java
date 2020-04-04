package message;

import org.apache.kafka.common.serialization.Deserializer;

/**
 *
 * @author joaoalegria
 */
public class MessageDeserializer implements Deserializer<Message> {
    
    @Override
    public Message deserialize(String topic, byte[] data) {
        String car_reg;
        int timestamp;
        int type;
        int speed;
        String car_status;
        
        int index=0;
        
        byte[] tmpBytes = new byte[data[index]];
        for(index=1; index<data[0]; index++){
            tmpBytes[index-1]=data[index];
        }
        car_reg=new String(tmpBytes);
        timestamp=data[index];
        index++;
        type=data[index];
        index++;
        
        Message m;
        switch(type){
            case 1:
                speed=data[index];
                m=new Message(car_reg, timestamp, type, speed);
                break;
            case 2:
                tmpBytes = new byte[data[index]];
                for(index=index+1; index<data[index]; index++){
                    tmpBytes[index-1]=data[index];
                }
                car_status=new String(tmpBytes);
                m=new Message(car_reg, timestamp, type, car_status);
                break;
            default:
                m=new Message(car_reg, timestamp, type);
                break;
        }
        
        return m;
    }

}
