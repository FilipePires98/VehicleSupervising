package message;

import java.nio.ByteBuffer;
import org.apache.kafka.common.serialization.Deserializer;

/**
 * Custom message deserializer for the car supervising system.
 * This class allows the deserialization of serialized messages for Kafka communications.
 * 
 * @author Filipe Pires (85122) and João Alegria (85048)
 */
public class MessageDeserializer implements Deserializer<Message> {
    
    
    /**
     * Desrializes a message of any of the three types supported by the system.
     * 
     * @param topic topic where the message is sent
     * @param data serialized content of the message
     * @return returns deserialized message as an instance of the Message class
     */
    @Override
    public Message deserialize(String topic, byte[] data) {
        String car_reg;
        long timestamp;
        int type;
        int speed;
        String car_status;
        
        int index=0;
        
        byte[] tmp = new byte[Integer.BYTES];
        for(int i=0; i<Integer.BYTES; i++){
            tmp[i]=data[i+index];
        }
        ByteBuffer bb = ByteBuffer.wrap(tmp);
        int idx1=bb.getInt();
        index=index+Integer.BYTES;
        
        tmp = new byte[idx1];
        for(int i=0; i<idx1; i++){
            tmp[i]=data[i+index];
        }
        car_reg=new String(tmp);
        index=index+idx1;
        
        tmp = new byte[Long.BYTES];
        for(int i=0; i<Long.BYTES; i++){
            tmp[i]=data[i+index];
        }
        bb = ByteBuffer.wrap(tmp);
        timestamp=bb.getLong();
        index=index+Long.BYTES;
        
        
        tmp = new byte[Integer.BYTES];
        for(int i=0; i<Integer.BYTES; i++){
            tmp[i]=data[i+index];
        }
        bb = ByteBuffer.wrap(tmp);
        type=bb.getInt();
        index=index+Integer.BYTES;
        
        Message m;
        switch(type){
            case 1:
                tmp = new byte[Integer.BYTES];
                for(int i=0; i<Integer.BYTES; i++){
                    tmp[i]=data[i+index];
                }
                bb = ByteBuffer.wrap(tmp);
                speed=bb.getInt();
                index=index+Integer.BYTES;
                m=new Message(car_reg, timestamp, type, speed);
                break;
            case 2:
                tmp = new byte[Integer.BYTES];
                for(int i=0; i<Integer.BYTES; i++){
                    tmp[i]=data[i+index];
                }
                bb = ByteBuffer.wrap(tmp);
                int idx2=bb.getInt();
                index=index+Integer.BYTES;
                
                tmp = new byte[idx2];
                for(int i=0; i<idx2; i++){
                    tmp[i]=data[index+i];
                }
                car_status=new String(tmp);
                m=new Message(car_reg, timestamp, type, car_status);
                break;
            default:
                m=new Message(car_reg, timestamp, type);
                break;
        }
        
        return m;
    }

}
