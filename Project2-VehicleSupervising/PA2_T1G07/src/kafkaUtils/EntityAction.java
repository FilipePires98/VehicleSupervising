package kafkaUtils;

/**
 *
 * @author joaoalegria
 */
public interface EntityAction<K,V> {
    public void processMessage(String topic, K key, V value);
}
