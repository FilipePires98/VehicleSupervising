package kafkaUtils;

/**
 * Interface implemented by the entities from the car supervising system that consume Kafka messages.
 * It defines the method where message processing occurs.
 * 
 * @author Filipe Pires (85122) and João Alegria (85048)
 */
public interface EntityAction<K,V> {
    /**
     * Processes messages from a given topic.
     * 
     * @param consumerId identifier of the consumer processing the current message
     * @param topic Kafka topic to which the message belongs to
     * @param key message unique key
     * @param value message value, actual message content with a format defined a priori
     */
    public void processMessage(int consumerId,String topic, K key, V value);
}
