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

    public Consumer(Properties properties, String[] topics, EntityAction<K,V> entity) {
        this.properties = properties;
        this.consumer = new KafkaConsumer<K,V>(properties);
        this.consumer.subscribe(Arrays.asList(topics));
        this.entity=entity;
    }

    @Override
    public void run() {
        while (true) {
            ConsumerRecords<K, V> records = consumer.poll(Duration.ofMillis(1000));
            for (ConsumerRecord<K,V> record : records) {
                this.entity.processMessage(record.topic(), record.key(), record.value());
            }
        }
    }

    
}
