import com.mongodb.spark.MongoSpark;
import org.apache.kafka.clients.producer.Producer;
import org.apache.kafka.clients.producer.ProducerRecord;
import org.apache.spark.api.java.JavaRDD;
import org.apache.spark.api.java.JavaSparkContext;
import org.apache.spark.sql.SparkSession;
import org.apache.spark.streaming.Duration;
import org.apache.spark.streaming.api.java.JavaInputDStream;
import org.apache.spark.streaming.api.java.JavaStreamingContext;
import org.apache.spark.streaming.kafka010.ConsumerStrategies;
import org.apache.spark.streaming.kafka010.KafkaUtils;
import org.apache.spark.streaming.kafka010.LocationStrategies;
import org.bson.Document;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.apache.kafka.common.serialization.StringDeserializer;

import java.util.*;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.json.*;


public final class SimpleApp {

    public static void main(final String[] args) throws InterruptedException {

        Logger.getLogger("org").setLevel(Level.OFF);
        Logger.getLogger("akka").setLevel(Level.OFF);


        // Spark config
        SparkSession spark = SparkSession.builder()
                .appName("MongoSparkConnectorIntro")
                .config("spark.mongodb.input.uri", "mongodb://mongo-0.mongodb-service/test.myCollection")
                .config("spark.mongodb.output.uri", "mongodb://mongo-0.mongodb-service/test.myCollection")
                .getOrCreate();

        JavaSparkContext jsc = new JavaSparkContext(spark.sparkContext());

        // 10000: batchDuration - The time interval at which streaming data will be divided into batches
        JavaStreamingContext streamingContext = new JavaStreamingContext(jsc, new Duration(10000));

        // Kafka config
        Map<String, Object> kafkaParams = new HashMap<>();
        kafkaParams.put("bootstrap.servers", "kafka-0.kafka-svc:9093,kafka-1.kafka-svc:9093,kafka-2.kafka-svc:9093");
        kafkaParams.put("key.deserializer", StringDeserializer.class);
        kafkaParams.put("value.deserializer", StringDeserializer.class);
        kafkaParams.put("group.id", "use_a_separate_group_id_for_each_stream");
        kafkaParams.put("auto.offset.reset", "latest");
        kafkaParams.put("enable.auto.commit", true);


        Collection<String> topics = Arrays.asList("topicA", "topicB");

        JavaInputDStream<ConsumerRecord<String, String>> stream =
                KafkaUtils.createDirectStream(
                        streamingContext,
                        LocationStrategies.PreferConsistent(),
                        ConsumerStrategies.<String, String>Subscribe(topics, kafkaParams)
                );



        stream.foreachRDD(rdd -> {
//            System.out.println("============= STREAM RDD, partitions :" + rdd.getNumPartitions());
//            JavaRDD<String> values = rdd.map(value -> {
//                System.out.println("============= handling message : " + value.value());
//                return value.value();
//            });
//            List<String> valuesList = values.collect();
//
//            System.out.println(valuesList.size() + " values");
//            for (String val : valuesList) {
//                System.out.println("value : " + val);
//            }

            rdd.foreachPartition(partitionOfRecords -> {

                while (partitionOfRecords.hasNext()) {
                    ConsumerRecord<String, String> message = partitionOfRecords.next();

                    String topic = message.topic();
                    String value = message.value();

                    Producer producer = MyKafkaProducer.getProducer();

                    if (Objects.equals(topic, "topicA")) {
                        producer.send(new ProducerRecord<>("topicB", 1, value));
                    }
                }
            });


            JavaRDD<Document> documents = rdd.map(message -> {
                String topic = message.topic();
                String value = message.value();
//                JSONObject obj = new JSONObject(value);
//                Document doc = Document.parse("{topic: \"" + topic + "\", value: \"" + value + "\"}");
//                doc.getStr
//                Document doc = Document.parse(obj.toString());
//                return doc;
                return Document.parse("{topic: \""+ topic +"\", value: \""+ value +"\"}");
            });
            MongoSpark.save(documents);
        });

        streamingContext.start();
        streamingContext.awaitTermination();

//        // Working Mongo + Spark example
//        // Create a RDD of 10 documents
//        JavaRDD<Document> documents = jsc.parallelize(asList(1, 2, 3, 4, 5, 6, 7, 8, 9, 10)).map
//                (new Function<Integer, Document>() {
//                    public Document call(final Integer i) throws Exception {
//                        return Document.parse("{test: " + i + "}");
//                    }
//                });
//
//        /*Start Example: Save data from RDD to MongoDB*****************/
//        MongoSpark.save(documents);
//        /*End Example**************************************************/
//
//        jsc.close();
    }
}
