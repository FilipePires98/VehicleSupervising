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
    private KafkaProducer<K,V> producer;

    public Producer(Properties properties) {
        this.properties = properties;
        this.producer = new KafkaProducer<K,V>(properties);
    }
    
    
    public void fireAndForget(String[] topics, K key, V value){
        for(String topic:topics){
            ProducerRecord<K,V> record = new ProducerRecord<K,V>(topic,key,value);
            this.producer.send(record);
        }
    }
    
    public void sendAsync(String[] topics, K key, V value){
        for(String topic:topics){
            ProducerRecord<K,V> record = new ProducerRecord<K,V>(topic,key,value);
            this.producer.send(record).isDone();
        }
    }
    
    public void senSync(String[] topics, K key, V value) throws InterruptedException, ExecutionException{
        for(String topic:topics){
            ProducerRecord<K,V> record = new ProducerRecord<K,V>(topic,key,value);
            this.producer.send(record, new ProducerCallback());
        }
    }
    
    public void close(){
        this.producer.close();
    }
    

}
