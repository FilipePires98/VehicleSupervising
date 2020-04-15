package kafkaUtils;

import java.util.Properties;
import java.util.concurrent.ExecutionException;
import message.Message;
import org.apache.kafka.clients.producer.Callback;
import org.apache.kafka.clients.producer.KafkaProducer;
import org.apache.kafka.clients.producer.ProducerRecord;
import org.apache.kafka.clients.producer.RecordMetadata;

/**
 *
 * @author joaoalegria
 */
public class Producer<K,V>{
    
    private class ProducerCallback implements Callback{

        @Override
        public void onCompletion(RecordMetadata recordMetadat, Exception e) {
            if(e != null){
                System.out.println("AsynchronousProducer failed with an expection.");
            }
        }
        
    }
    
    private Properties properties;
    private KafkaProducer<K,V> producer;

    public Producer(Properties properties) {
        this.properties = properties;
        this.producer = new KafkaProducer<K,V>(properties);
    }
    
    
    public void fireAndForget(String topic, Integer partition, K key, V value){
        ProducerRecord<K,V> record;
        if(partition==null){
            record = new ProducerRecord<K,V>(topic,key,value);
        }else{
            record = new ProducerRecord<K,V>(topic,partition,key,value);
        }
        this.producer.send(record);
        producer.flush();
    }
    
    public void sendAsync(String topic, Integer partition, K key, V value){
        ProducerRecord<K,V> record;
        if(partition==null){
            record = new ProducerRecord<K,V>(topic,key,value);
        }else{
            record = new ProducerRecord<K,V>(topic,partition,key,value);
        }
        this.producer.send(record, new ProducerCallback());
        producer.flush();
    }
    
    public void sendSync(String topic, Integer partition, K key, V value) throws InterruptedException, ExecutionException{
        ProducerRecord<K,V> record;
        if(partition==null){
            record = new ProducerRecord<K,V>(topic,key,value);
        }else{
            record = new ProducerRecord<K,V>(topic,partition,key,value);
        }
        RecordMetadata meta = this.producer.send(record).get();
        producer.flush();
    }
    
    public void close(){
        this.producer.close();
    }

}
