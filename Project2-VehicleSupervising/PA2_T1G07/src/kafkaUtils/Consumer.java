package kafkaUtils;

import java.time.Duration;
import java.util.Arrays;
import java.util.Properties;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.apache.kafka.clients.consumer.ConsumerRecords;
import org.apache.kafka.clients.consumer.KafkaConsumer;

/**
 *
 * @author joaoalegria
 */
public class Consumer<K,V> implements Runnable{
    
    private Properties properties;
    private KafkaConsumer<K,V> consumer;
    private EntityAction<K,V> entity;
    private int id;
    
    private volatile boolean done = false;
    
    public Consumer(int id, Properties properties, String[] topics, EntityAction<K,V> entity) {
        this.properties = properties;
        this.consumer = new KafkaConsumer<K,V>(properties);
        this.consumer.subscribe(Arrays.asList(topics));
        this.entity=entity;
        this.id=id;
    }

    @Override
    public void run() {
        // while (true) {
        while(!done) {
            ConsumerRecords<K, V> records = consumer.poll(Duration.ofMillis(1000));
            for (ConsumerRecord<K,V> record : records) {
                this.entity.processMessage(id, record.topic(), record.key(), record.value());
            }
        }
        consumer.close();
    }

    public void shutdown() {
        done = true;
    }
    
}
