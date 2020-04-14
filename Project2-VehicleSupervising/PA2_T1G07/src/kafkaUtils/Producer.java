package kafkaUtils;

import java.util.Properties;
import java.util.concurrent.ExecutionException;
import message.Message;
import org.apache.kafka.clients.producer.Callback;
import org.apache.kafka.clients.producer.KafkaProducer;
import org.apache.kafka.clients.producer.ProducerRecord;
import org.apache.kafka.clients.producer.RecordMetadata;

/**
 * Class for Kafka message producers.
 * A producer instance follows a set of properties and is responsible for producing messages from the data given as input and publish them to the topics also given as input.
 * It supports three distinct forms of sending messages, each with its specific purpose.
 * 
 * @author Filipe Pires (85122) and João Alegria (85048)
 */
public class Producer<K,V>{
    
    /**
     * Class that is instantiated when the producer receives a response. 
     * It allows to determine wether asynchronous messages were successfully sent or not.
     */
    private class ProducerCallback implements Callback{

        /**
         * Determines whether message sending by a producer was completed with any errors or not.
         * 
         * @param recordMetadata metadata of the record whose response was received
         * @param e possible exception thrown in the callback
         */
        @Override
        public void onCompletion(RecordMetadata recordMetadata, Exception e) {
            if(e != null){
                System.out.println("AsynchronousProducer failed with an exception.");
            }
        }
        
    }
    /**
     * Producer properties (bootstrap.servers, group.id, key.deserializer, value.deserializer, etc.).
     */
    private Properties properties;
    /**
     * Producer instance from org.apache.kafka.clients.
     */
    private KafkaProducer<K,V> producer;

    /**
     * Creates new producer with a set of properties for Kafka.
     * 
     * @param properties producer properties (bootstrap.servers, group.id, key.deserializer, value.deserializer, etc.)
     */
    public Producer(Properties properties) {
        this.properties = properties;
        this.producer = new KafkaProducer<K,V>(properties);
    }
    
    /**
     * Sends message through a given topic, without any concerns regarding delivery confirmations.
     * This sending option is the simplest and fastest way to send messages through topics.
     * Adopted when there is margin for data loss.
     * 
     * @param topic topic through where the message is sent
     * @param key unique message key
     * @param value actual content of the message
     */
    public void fireAndForget(String topic, K key, V value){
        ProducerRecord<K,V> record = new ProducerRecord<K,V>(topic,key,value);
        this.producer.send(record);
    }
    
    /**
     * Sends message through a given topic, and asynchronously awaits for a delivery confirmation.
     * This sending option allows high speed performance to be kept while permitting failure handling using a callback function.
     * 
     * @param topic topic through where the message is sent
     * @param key unique message key
     * @param value actual content of the message
     */
    public void sendAsync(String topic, K key, V value){
        ProducerRecord<K,V> record = new ProducerRecord<K,V>(topic,key,value);
        this.producer.send(record).isDone();
    }
    
    /**
     * Sends message through a given topic, and synchronously awaits for a delivery confirmation.
     * This sending option ensures message delivery at the cost of reduced speed performance.
     * By handling exceptions in real time, it allows message reprocessing without reordering.
     * Adopted when messages are critical.
     * 
     * @param topic topic through where the message is sent
     * @throws InterruptedException
     * @throws ExecutionException 
     */
    public void sendSync(String topic, K key, V value) throws InterruptedException, ExecutionException{
        ProducerRecord<K,V> record = new ProducerRecord<K,V>(topic,key,value);
        this.producer.send(record, new ProducerCallback());
    }
    
    /**
     * Shuts down the producer once all messages have been sent.
     */
    public void close(){
        this.producer.close();
    }

}
