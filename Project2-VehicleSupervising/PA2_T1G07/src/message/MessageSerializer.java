package message;

import java.io.UnsupportedEncodingException;
import java.nio.ByteBuffer;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.apache.kafka.common.serialization.Serializer;

/**
 *
 * @author joaoalegria
 */
public class MessageSerializer implements Serializer<Message> {
    
    private String encoding="UTF8";

    @Override
    public byte[] serialize(String topic, Message data) {
        
        try {
            byte[] ser_car_reg;
            int ser_car_reg_size;
            byte[] ser_car_status;
            int ser_car_status_size;
            
            if(data==null){
                return null;
            }
            
            ser_car_reg=data.getCar_reg().getBytes(encoding);
            ser_car_reg_size=ser_car_reg.length;
            
            ByteBuffer buf;
            switch(data.getType()){
                case 0:
                    buf = ByteBuffer.allocate(4+ser_car_reg_size+8+4);
                    buf.putInt(ser_car_reg_size);
                    buf.put(ser_car_reg);
                    buf.putLong(data.getTimestamp());
                    buf.putInt(data.getType());
                    return buf.array();
                case 1:
                    buf = ByteBuffer.allocate(4+ser_car_reg_size+8+4+4);
                    buf.putInt(ser_car_reg_size);
                    buf.put(ser_car_reg);
                    buf.putLong(data.getTimestamp());
                    buf.putInt(data.getType());
                    buf.putInt(data.getSpeed());
                    return buf.array();
                case 2:
                    ser_car_status=data.getCar_status().getBytes(encoding);
                    ser_car_status_size=ser_car_status.length;
                    
                    buf = ByteBuffer.allocate(4+ser_car_reg_size+8+4+4+ser_car_status_size);
                    buf.putInt(ser_car_reg_size);
                    buf.put(ser_car_reg);
                    buf.putLong(data.getTimestamp());
                    buf.putInt(data.getType());
                    buf.putInt(ser_car_status_size);
                    buf.put(ser_car_status);
                    return buf.array();
            }
            
            
        } catch (UnsupportedEncodingException ex) {
            Logger.getLogger(MessageSerializer.class.getName()).log(Level.SEVERE, null, ex);
        }
        return null;
    }


}
