package zihao;

import org.apache.kafka.clients.producer.KafkaProducer;
import org.apache.kafka.clients.producer.ProducerConfig;
import org.apache.kafka.clients.producer.ProducerRecord;
import org.apache.kafka.common.serialization.StringSerializer;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.Properties;

public class ProducerDemoWithKey {
    private static final Logger logger = LogManager.getLogger(ProducerDemoWithKey.class.getName());

    public static void main(String[] args) {

        String bootstrapServers = "127.0.0.1:9092";

        // create Producer properties
        Properties properties = new Properties();
        properties.setProperty(ProducerConfig.BOOTSTRAP_SERVERS_CONFIG, bootstrapServers);
        properties.setProperty(ProducerConfig.KEY_SERIALIZER_CLASS_CONFIG, StringSerializer.class.getName());
        properties.setProperty(ProducerConfig.VALUE_SERIALIZER_CLASS_CONFIG, StringSerializer.class.getName());

        // create the producer
        KafkaProducer<String, String> producer = new KafkaProducer<String, String>(properties);

        for (int i = 0; i < 10; i++) {
            String topic = "first_topic";
            String value = "hello world " + i;
            String key = "id_" + i;  // !! by providing a key, same record always go to the same partition.

            // create a producer record
            final ProducerRecord<String, String> record = new ProducerRecord<>(topic, key, value);

            // send data -asynchronous
            producer.send(record, (recordMetadata, e) -> {
                if (e == null) {
                    // the record was successfully sent
                    logger.info("########");
                    logger.info("Received new metadata. \n" +
                            "Topic: " + recordMetadata.topic() + "\n" +
                            "Partition: " + recordMetadata.partition() + "\n" +
                            "Offset: " + recordMetadata.offset() + "\n" +
                            "Timestamp" + recordMetadata.timestamp());
                    logger.info("key: " + key);
                } else {
                    logger.error("Error while producing ", e);
                }
            });
        }

        producer.flush();
        producer.close();
    }
}
