package message;

import java.nio.ByteBuffer;
import org.apache.kafka.common.serialization.Deserializer;

/**
 *
 * @author joaoalegria
 */
public class MessageDeserializer implements Deserializer<Message> {
    
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
