package kafkaUtils;

import java.time.Duration;
import java.util.Arrays;
import java.util.Properties;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.apache.kafka.clients.consumer.ConsumerRecords;
import org.apache.kafka.clients.consumer.KafkaConsumer;
/**
 * Class for Kafka message consumers.
 * A consumer instance works as a thread and belongs to a given entity and subscribes to an array of Kafka topics according to a set of properties.
 * Each instance has a unique identifier and is responsible for retrieving and processing messages from the topics assigned to them.
 * 
 * @author Filipe Pires (85122) and João Alegria (85048)
 */
public class Consumer<K,V> implements Runnable{
    
    /**
     * Consumer properties (bootstrap.servers, group.id, key.deserializer, value.deserializer, etc.).
     */
    private final Properties properties;
    /**
     * Consumer instance from org.apache.kafka.clients.
     */
    private final KafkaConsumer<K,V> consumer;
    /**
     * Entity instance to which the consumer belongs to.
     */
    private final EntityAction<K,V> entity;
    /**
     * Unique consumer identifier.
     */
    private final int id;

    /**
     * Internal reference to the rebalance listener assigned to the consumer.
     */
    private final RebalanceListener rebalanceListener;
    
    /**
     * Control variable to notify the consumer that it should stop working.
     */
    private volatile boolean done = false;
    
    /**
     * Creates new consumer with a specific id, a owner entity and a set of properties and topics for Kafka.
     * 
     * @param id unique identifier
     * @param properties consumer properties (bootstrap.servers, group.id, key.deserializer, value.deserializer, etc.)
     * @param topics array of topics to which the consumer will subscribe to
     * @param entity entity to which the consumer belongs to
     */
    public Consumer(int id, Properties properties, String[] topics, EntityAction<K,V> entity) {
        this.properties = properties;
        this.consumer = new KafkaConsumer<>(properties);
        this.rebalanceListener = new RebalanceListener<>(consumer);
        this.consumer.subscribe(Arrays.asList(topics), rebalanceListener);
        this.entity=entity;
        this.id=id;
    }

    /**
     * Consumer thread's run method.
     */
    @Override
    @SuppressWarnings("unchecked")
    public void run() {
        // while (true) {
        while(!done) {
            ConsumerRecords<K, V> records = consumer.poll(Duration.ofMillis(100));
            for (ConsumerRecord<K,V> record : records) {
                this.entity.processMessage(id, record.topic(), record.key(), record.value());
                rebalanceListener.addOffset(record.topic(), record.partition(), record.offset());
            }
            consumer.commitSync(rebalanceListener.getCurrentOffsets());
            
        }
        consumer.close();
    }

    /**
     * Shuts down the consumer once all tasks are completed.
     * This method is called by the owner entity and works with the control variable "done" in order to stop the thread.
     */
    public void shutdown() {
        done = true;
    }
    
}
