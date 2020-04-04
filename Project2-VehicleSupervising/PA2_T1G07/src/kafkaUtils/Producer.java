package kafkaUtils;

import java.util.Properties;
import java.util.concurrent.ExecutionException;
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
    private KafkaProducer producer;

    public Producer(Properties properties) {
        this.properties = properties;
        this.producer = new KafkaProducer<K,V>(properties);
    }
    
    
    public void fireAndForget(String topic, K key, V value){
        ProducerRecord<K,V> record = new ProducerRecord<>(topic,key,value);
        this.producer.send(record);
    }
    
    public void sendAsync(String topic, K key, V value){
        ProducerRecord<K,V> record = new ProducerRecord<>(topic,key,value);
        this.producer.send(record).isDone();
    }
    
    public void senSync(String topic, K key, V value) throws InterruptedException, ExecutionException{
        ProducerRecord<K,V> record = new ProducerRecord<>(topic,key,value);
        this.producer.send(record, new ProducerCallback());
    }
    
    public void close(){
        this.producer.close();
    }
    

}
