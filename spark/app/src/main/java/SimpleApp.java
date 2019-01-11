import com.mongodb.spark.MongoSpark;
import com.mongodb.spark.rdd.api.java.JavaMongoRDD;
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

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.*;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.json.*;

import static java.util.Collections.singletonList;


public final class SimpleApp {

    private final static String TOPIC_QUERIES = "topicA";
    private final static String TOPIC_DATA = "topicB";
    private final static String TOPIC_RESULTS = "topicB";

    public static void main(final String[] args) throws InterruptedException {

        Logger.getLogger("org").setLevel(Level.OFF);
        Logger.getLogger("akka").setLevel(Level.OFF);


        // Spark config
        SparkSession spark = SparkSession.builder()
                .appName("MongoSparkConnectorIntro")
//                .config("spark.mongodb.input.uri",  "mongodb://mongo-0.mongodb-service:27017,mongo-1.mongodb-service:27017,mongo-2.mongodb-service:27017/test.myCollection?replicaSet=my_replica_set")
//                .config("spark.mongodb.output.uri", "mongodb://mongo-0.mongodb-service:27017,mongo-1.mongodb-service:27017,mongo-2.mongodb-service:27017/test.myCollection?replicaSet=my_replica_set")
                .config("spark.mongodb.input.uri",  "mongodb://mongo-0.mongodb-service:27017/test.myCollection")
                .config("spark.mongodb.output.uri", "mongodb://mongo-0.mongodb-service:27017/test.myCollection")
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


        Collection<String> topics = Arrays.asList(TOPIC_QUERIES, TOPIC_DATA);

        JavaInputDStream<ConsumerRecord<String, String>> stream =
                KafkaUtils.createDirectStream(
                        streamingContext,
                        LocationStrategies.PreferConsistent(),
                        ConsumerStrategies.<String, String>Subscribe(topics, kafkaParams)
                );


//        stream.foreachRDD(rdd -> {
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

//            rdd.filter(entry -> Objects.equals(entry.topic(), TOPIC_QUERIES)).map(message -> new JSONObject(message.value())).flatMap(query -> {
//
//            });

        //.collect();

//            rdd.filter(entry -> Objects.equals(entry.topic(), TOPIC_QUERIES)).map(message -> {
//                // Expected query
//                // {"article": "Matt_LaFleur", "from": "2019-01-01", "to": "2019-01-08"}'
//                JSONObject query = new JSONObject(message.value());
//
//                String article = query.getString("article");
//                String from = query.getString("from").replace("-", "");
//                String to = query.getString("to").replace("-", "");
//
//
//                URL url = new URL("https://wikimedia.org/api/rest_v1/metrics/pageviews/per-article/en.wikipedia/all-access/all-agents/"+ article +"/daily/"+ from +"/" + to);
//                HttpURLConnection conn = (HttpURLConnection) url.openConnection();
//                conn.setRequestMethod("GET");
//                conn.setRequestProperty("Accept", "application/json");
//
//                String error = "";
//                String resultText = "";
//
//                if (conn.getResponseCode() != 200) {
//                    error =  "Failed : HTTP error code : " + conn.getResponseCode();
//                } else {
//                    BufferedReader br = new BufferedReader(new InputStreamReader(
//                            (conn.getInputStream())));
//
//                    StringBuilder response = new StringBuilder();
//
//                    String line;
//                    System.out.println("Output from Server .... \n");
//                    while ((line = br.readLine()) != null) {
//                        response.append(line);
//                    }
//                    resultText = response.toString();
//                }
//
//                conn.disconnect();
//
//                List<JSONObject> new_data = new LinkedList<>();
//
//                JSONObject obj;
//                if (!Objects.equals(error, "")) {
//                    obj = new JSONObject();
//                    obj.append("error", error);
////                    MyKafkaProducer.getProducer().send(new ProducerRecord<>(TOPIC_ERROR, null, obj.toString()));
//                } else {
//                    obj = new JSONObject(resultText);
//
//                    obj.getJSONArray("items").forEach(o -> {
//                        if ( o instanceof JSONObject ) {
//                            JSONObject entry = (JSONObject)o;
//                            JSONObject dbentry = new JSONObject();
//                            dbentry.append("article", entry.getString("article"));
//                            dbentry.append("views", entry.getString("views"));
//                            dbentry.append("rank", null);
//                            String timestamp = entry.getString("timestamp");
//                            dbentry.append("date", timestamp.substring(0, 4) + "-" + timestamp.substring(4, 6) + "-" + timestamp.substring(6, 8));
//
//                            new_data.add(dbentry);
//
//                            MyKafkaProducer.getProducer().send(new ProducerRecord<>(TOPIC_DATA, null, dbentry.toString()));
//                        }
//                    });
//
//                }
//
//
////                MyKafkaProducer.getProducer().send(new ProducerRecord<>(topic, null, obj.toString()));
//                return new_data;
//            }).collect();


        // Listening TOPIC_DATA and entering messages directly on the database
        stream.foreachRDD(rdd -> {
            JavaRDD<Document> documents = rdd.filter(entry -> Objects.equals(entry.topic(), TOPIC_DATA)).map(message -> {
                JSONObject obj;
                try {
                    obj = new JSONObject(message.value());
                } catch (Exception e) {
                    obj = new JSONObject();
                }
                return Document.parse(obj.toString());
            });

            MongoSpark.save(documents);

        });

        JavaMongoRDD<Document> mongo_rdd = MongoSpark.load(jsc);

        stream.foreachRDD(rdd -> {
            List<JSONObject> queries = rdd.filter(entry -> Objects.equals(entry.topic(), TOPIC_QUERIES)).map(message -> new JSONObject(message.value())).collect();
            for (JSONObject query: queries) {
                String req_id = query.getString("req_id");
                String article = query.getString("article");
                String from = query.getString("from");
                String to = query.getString("to");

                JavaMongoRDD<Document> aggregatedRdd = mongo_rdd.withPipeline(singletonList(Document.parse("{ $match: { article: \""+ article +"\", date : { $gte : \""+ from +"\", $lte: \""+ to +"\" } } }")));
                Integer totalViews = aggregatedRdd.map(elm -> elm.getInteger("views")).reduce((views1, views2) -> views1 + views2);

                JSONObject obj = new JSONObject();
                obj.append("req_id", req_id);
                obj.append("result", totalViews);

                MyKafkaProducer.getProducer().send(new ProducerRecord<>(TOPIC_RESULTS, null, obj.toString()));
            }

//            List<ConsumerRecord<String, String>> l = rdd.filter(entry -> Objects.equals(entry.topic(), TOPIC_DATA)).collect();//.map(message -> {
//                JSONObject obj;
//                try {
//                    obj = new JSONObject(message.value());
//                } catch (Exception e) {
//                    obj = new JSONObject();
//                }
//                return Document.parse(obj.toString());
//            });
//
//            MongoSpark.save(documents);

        });

//            MongoSpark.lo


//            rdd.foreachPartition(partitionOfRecords -> {
//
//                while (partitionOfRecords.hasNext()) {
//                    ConsumerRecord<String, String> message = partitionOfRecords.next();
//
//                    String topic = message.topic();
//                    String value = message.value();
//
//                    Producer producer = MyKafkaProducer.getProducer();
//
//                    if (Objects.equals(topic, "topicA")) {
//                        producer.send(new ProducerRecord<>("topicB", null, value));
//                    }
//                }
//            });
//
//
//            JavaRDD<Document> documents = rdd.map(message -> {
//                String topic = message.topic();
//                String value = message.value();
////                JSONObject obj = new JSONObject(value);
////                Document doc = Document.parse("{topic: \"" + topic + "\", value: \"" + value + "\"}");
////                doc.getStr
////                Document doc = Document.parse(obj.toString());
////                return doc;
//                return Document.parse("{topic: \""+ topic +"\", value: \""+ value +"\"}");
//            });
//            MongoSpark.save(documents);
//        });

        streamingContext.start();
        streamingContext.awaitTermination();

//        /*Start Example: Read data from MongoDB************************/
//        JavaMongoRDD<Document> rdd = MongoSpark.load(jsc);
//        /*End Example**************************************************/
//
//        {
//            String req_id = "req_id";
//            String article = "article1";
//            String from = "2000-01-01";
//            String to = "2000-01-03";
//            JavaMongoRDD<Document> aggregatedRdd = rdd.withPipeline(singletonList(Document.parse("{ $match: { article: \""+ article +"\", date : { $gte : \""+ from +"\", $lte: \""+ to +"\" } } }")));
//
//            Integer totalViews = aggregatedRdd.map(elm -> elm.getInteger("views")).reduce((views1, views2) -> views1 + views2);
//
//
//            JSONObject obj = new JSONObject();
//            obj.append("req_id", req_id);
//            obj.append("result", totalViews);
//
//            System.out.println("========================");
//            System.out.println("========================");
//            System.out.println("========================");
//            System.out.println("Result of the number of view of the article article1:  " + totalViews);
//            System.out.println("========================");
//            System.out.println("========================");
//            System.out.println("========================");
//
//
//
//            MyKafkaProducer.getProducer().send(new ProducerRecord<>(TOPIC_DATA, null, obj.toString()));
//
//        }
//
//
//
//        // Analyze data from MongoDB
//        System.out.println(rdd.count());
//        rdd.foreach(elm -> {
//            System.out.println(elm.toJson());
//        });
//        System.out.println(rdd.first().toJson());

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
