
import java.util.Properties;
import org.apache.kafka.clients.producer.KafkaProducer;
import org.apache.kafka.clients.producer.Producer;

/**
 *
 * @author kamal
 */
public class MyKafkaProducer {

    private static Producer producer = null;
    private MyKafkaProducer() {
    }

    public static Producer getProducer() {
        if (producer == null) {
            Properties props = new Properties();
            props.put("bootstrap.servers", "kafka-0.kafka-svc:9093,kafka-1.kafka-svc:9093,kafka-2.kafka-svc:9093");
            props.put("key.serializer", "org.apache.kafka.common.serialization.IntegerSerializer");
            props.put("value.serializer", "org.apache.kafka.common.serialization.StringSerializer");
            producer = new KafkaProducer(props);

        }
        return producer;
    }
}