package kafkaUtils;

/**
 *
 * @author joaoalegria
 */
public interface EntityAction<K,V> {
    public void processMessage(int consumerId, String topic, K key, V value);
}
