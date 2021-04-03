package kafkaUtils;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import org.apache.kafka.clients.consumer.ConsumerRebalanceListener;
import org.apache.kafka.clients.consumer.KafkaConsumer;
import org.apache.kafka.clients.consumer.OffsetAndMetadata;
import org.apache.kafka.common.TopicPartition;

/**
 * Self implementation of ConsumerRebalanceListener.
 * This entity allows to have a fine control over the commit of offsets.
 * Allows our consumers to commit the latest processed offsets even if the consumer crashes or a new distribution of topics and partitions are made.
 */
public class RebalanceListener<K,V> implements ConsumerRebalanceListener{
    
    /**
     * Instance of the consumer to control.
     */
    private KafkaConsumer<K,V> consumer;
    
    /**
     * Structure containing the latest offsets for each topics and partition assigned to the consumer.
     */
    private Map<TopicPartition, OffsetAndMetadata> currentOffsets = new HashMap<TopicPartition,OffsetAndMetadata>();

    /**
     * Class constructor.
     * @param con Instance of the consumer to apply the rebalance listener. 
     */
    public RebalanceListener(KafkaConsumer<K,V> con) {
        this.consumer = con;
    }
    
    /**
     * Enables the update of a topic or partition offset.
     * @param topic String representing the topic were the offset belongs
     * @param partition int representing the partition were the offset belongs
     * @param offset long representing the offset
     */
    public void addOffset(String topic, int partition, long offset) {
        currentOffsets.put(new TopicPartition(topic, partition), new OffsetAndMetadata(offset, "Commit"));
    }
    
    /**
     * Returns the latest offsets of each partition and topic and resets the internal structure.
     * @return Map<TopicPartition, OffsetAndMetadata> containing the topics/partitions and corresponding offset.
     */
    public Map<TopicPartition, OffsetAndMetadata> getCurrentOffsets() {
         Map<TopicPartition, OffsetAndMetadata> tmp = currentOffsets;
         currentOffsets.clear();
         return tmp;
    }

    
    /**
     * Inject the code when a new partition or topic is assigned to the consumer.
     * @param partitions Collection<TopicPartition> containing the partition/topics
     */
    @Override
    public void onPartitionsAssigned(Collection<TopicPartition> partitions) {
//        System.out.println("Following Partitions Assigned ....");
//        for (TopicPartition partition : partitions)
//            System.out.println(partition.partition() + ",");
    }

    /**
     * Injects the code when partitions or topics are removed from the consumer.
     * @param partitions Collection<TopicPartition> containing the revoked partition/topics
     */
    @Override
    public void onPartitionsRevoked(Collection<TopicPartition> partitions) {
//        System.out.println("Following Partitions Revoked ....");
//        for (TopicPartition partition : partitions)
//            System.out.println(partition.partition() + ",");
//
//
//        System.out.println("Following Partitions commited ....");
//        for (TopicPartition tp : currentOffsets.keySet())
//            System.out.println(tp.partition());

        consumer.commitSync(currentOffsets);
        currentOffsets.clear();
    }

}
