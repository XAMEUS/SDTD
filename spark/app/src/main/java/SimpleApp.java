import com.mongodb.spark.MongoSpark;
import com.mongodb.spark.rdd.api.java.JavaMongoRDD;
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
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.text.ParseException;
import java.util.*;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.json.*;
import scala.Tuple2;

import static java.util.Collections.singletonList;


public final class SimpleApp {

    private final static String TOPIC_QUERIES = "requests";
    private final static String TOPIC_DATA = "articles";
    private final static String TOPIC_RESULTS = "responses";

    public static void main(final String[] args) throws InterruptedException {

        Logger.getLogger("org").setLevel(Level.OFF);
        Logger.getLogger("akka").setLevel(Level.OFF);


        // Spark config
        SparkSession spark = SparkSession.builder()
                .appName("MongoSparkConnectorIntro")
//                .config("spark.mongodb.input.uri",  "mongodb://mongo-0.mongodb-service:27017,mongo-1.mongodb-service:27017,mongo-2.mongodb-service:27017/test.myCollection?replicaSet=my_replica_set")
//                .config("spark.mongodb.output.uri", "mongodb://mongo-0.mongodb-service:27017,mongo-1.mongodb-service:27017,mongo-2.mongodb-service:27017/test.myCollection?replicaSet=my_replica_set")
                .config("spark.mongodb.input.uri", "mongodb://mongo-0.mongodb-service:27017/test.myCollection")
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
//                    obj.put("error", error);
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
        JavaMongoRDD<Document> mongo_rdd = MongoSpark.load(jsc);

        stream.foreachRDD(rdd -> {
            List<Document> documents = rdd.filter(entry -> Objects.equals(entry.topic(), TOPIC_DATA)).map(message -> {
                JSONObject obj;
                try {
                    obj = new JSONObject(message.value());
                    return Document.parse(obj.toString());
                } catch (Exception e) {
                    return null;
                }
            }).filter(document -> document != null).collect();

            if (documents.isEmpty())
                return;

//            Map<String, List<String>> articleList = new HashMap<>();
            List<Document> filteredDocuments = new LinkedList<Document>();
            for (Document doc : documents) {
				String article = doc.getString("article");
				String date = doc.getString("date");

		    	JavaMongoRDD<Document> aggregatedRdd = mongo_rdd.withPipeline(
		    			Arrays.asList(
	    					Document.parse("{ $match: { article : \"" + article.replace("\"", "\\\"") + "\" , date : \"" + date + "\" } }")
		    			)
		    	);

		    	if (aggregatedRdd.count() == 0) {
		    	    filteredDocuments.add(doc);
                }
//		    		if (articleList.containsKey(article)) {
//		    			articleList.get(article).add(date);
//		    		} else {
//		    			articleList.put(article, Arrays.asList(date));
//		    		}
//		    	}
            };

//            documents.filter(document -> articleList.containsKey(document.getString("article")) && articleList.get(document.getString("article")).contains(document.getString("date")) );

//            documents.mapToPair(document -> new Tuple2<>(document.getString("article"), Arrays.asList(document.getInteger("views"), document.getInteger("rank"), document.getString("date"))))
//            	.reduceByKey((array1, array2) -> { array1.addAll(array2); return array1; })
//                .map(tuple -> {
//                    JSONObject obj = new JSONObject();
//                    obj.put("article", tuple._1());
//                    obj.put("views", new JSONArray(tuple._2()));
//                    return obj;
//                });

            MongoSpark.save(jsc.parallelize(documents));
        });


        stream.foreachRDD(rdd -> {
            List<String> queries = rdd.filter(entry -> Objects.equals(entry.topic(), TOPIC_QUERIES)).map(message -> message.value()).collect();
            for (String strQuery : queries) {
                try {

                    JSONObject query = new JSONObject(strQuery);
                    JSONObject request = query.getJSONObject("request");

                    // We execute the query and send the result through Kafka messages
                    switch (request.getString("type")) {
                        case "infos":
                            infoRequest(request, mongo_rdd);
                            break;
                        case "tops":
                            topRequest(request, mongo_rdd);
                            break;
                        default:
                            break;
                    }

                } catch (Exception e) {
                    System.err.println("Exception with query: " + strQuery);
                    e.printStackTrace();
                }
            }
        });

        // Start the reading of the stream;
        streamingContext.start();
        streamingContext.awaitTermination();
        // No code will be executed after this line
    }

    /**
     * Get Views information for an `article` between `from` and `to`
     * Result will be sent to kafka topic TOPIC_RESULTS
     *
     * @param request JSONObject {article: String, from: String, to: String}
     * @param mongo_rdd
     * @throws IOException
     * @throws ParseException
     */
    private static void infoRequest(JSONObject request, JavaMongoRDD<Document> mongo_rdd) throws IOException, ParseException {
        String article = request.getString("article");
        String from = request.getString("from");
        String to = request.getString("to");

        // Get data from database
        JavaMongoRDD<Document> aggregatedRdd = mongo_rdd.withPipeline(singletonList(Document.parse("{ $match: { article: \"" + article.replace("\"", "\\\"") + "\", date : { $gte : \"" + from + "\", $lte: \"" + to + "\" } } }")));
        List<Tuple2<String, Integer>> views = aggregatedRdd.map(elm -> new Tuple2<String, Integer>(elm.getString("date"), elm.getInteger("views"))).collect();
        long returnCount = aggregatedRdd.count();
        Integer totalViews = 0;
        if (returnCount > 0) {
            totalViews = aggregatedRdd.map(elm -> elm.getInteger("views")).reduce((views1, views2) -> views1 + views2);
        }

        // Collect missing data from the database
        List<String> datesInDB = aggregatedRdd.map(elm -> elm.getString("date")).collect();

        HashSet<String> missingDates = new HashSet<>();

        {
            // We check which dates are missing
            HashSet<String> knownDates = new HashSet<>();
            knownDates.addAll(datesInDB);

            for (String date : DateUtils.getDatesBetween(from, to)) {
                if (!knownDates.contains(date)) {
                    missingDates.add(date);
                }
            }
        }

        Integer totalViewNewData = 0;
        List<Tuple2<String, Integer>> viewsNewData = new LinkedList<>();

//        System.out.println("=================================================");
//        for (String missingDate : missingDates) {
//            System.out.println(missingDate);
//        }
//        System.out.println("=================================================");

        // Retrieving the missing data from the API
        if (missingDates.size() > 0) {
            String _article = request.getString("article");
            String _from = request.getString("from").replace("-", "") + "00";
            String _to = request.getString("to").replace("-", "") + "00";


            URL url = new URL("https://wikimedia.org/api/rest_v1/metrics/pageviews/per-article/en.wikipedia/all-access/all-agents/" + _article + "/daily/" + _from + "/" + _to);
            HttpURLConnection conn = (HttpURLConnection) url.openConnection();
            conn.setRequestMethod("GET");
            conn.setRequestProperty("Accept", "application/json");

            String error = "";
            String resultText = "";

            if (conn.getResponseCode() != 200) {
                error = "Failed : HTTP error code : " + conn.getResponseCode();
            } else {
                BufferedReader br = new BufferedReader(new InputStreamReader(
                        (conn.getInputStream())));

                StringBuilder response = new StringBuilder();

                String line;
                System.out.println("Output from Server .... \n");
                while ((line = br.readLine()) != null) {
                    response.append(line);
                }
                resultText = response.toString();
            }

            conn.disconnect();

            JSONObject obj;
            if (!Objects.equals(error, "")) {
                obj = new JSONObject();
                obj.put("error", error);
            } else {
                obj = new JSONObject(resultText);

                obj.getJSONArray("items").forEach(o -> {
                    if (o instanceof JSONObject) {
                        JSONObject entry = (JSONObject) o;
                        String timestamp = entry.getString("timestamp");
                        String date = timestamp.substring(0, 4) + "-" + timestamp.substring(4, 6) + "-" + timestamp.substring(6, 8);

                        if (!datesInDB.contains(date)) {
                            JSONObject dbentry = new JSONObject();
                            dbentry.put("article", entry.getString("article"));
                            dbentry.put("views", entry.getInt("views"));
                            dbentry.put("rank", entry.optString("rank"));
                            dbentry.put("date", timestamp.substring(0, 4) + "-" + timestamp.substring(4, 6) + "-" + timestamp.substring(6, 8));

                            viewsNewData.add(new Tuple2(date, entry.getInt("views")));

                            MyKafkaProducer.getProducer().send(new ProducerRecord<>(TOPIC_DATA, null, dbentry.toString()));
                        }
                    }
                });
            }
        }


        // Construction of the response
        JSONObject obj = new JSONObject();
        obj.put("request", request);


        JSONArray viewsListJSON = new JSONArray();
        views.forEach(tuple -> {
            viewsListJSON.put(new JSONObject("{\"date\": \"" + tuple._1() + "\", \"views\": \"" + tuple._2() + "\"}"));
        });

        for (Tuple2 tuple : viewsNewData) {
            totalViewNewData += (Integer) tuple._2();
            viewsListJSON.put(new JSONObject("{\"date\": \"" + tuple._1() + "\", \"views\": \"" + tuple._2() + "\"}"));
        }

        JSONObject results = new JSONObject();
        results.put("totalViews", totalViews + totalViewNewData);
        results.put("views", viewsListJSON);
        obj.put("response", results);

        MyKafkaProducer.getProducer().send(new ProducerRecord<>(TOPIC_RESULTS, null, obj.toString()));
    }


    /**
     * Retreive TOP article for a period determined by `from` and `to`
     *
     * @param request
     * @param mongo_rdd
     * @throws IOException
     * @throws ParseException
     */
    private static void topRequest(JSONObject request, JavaMongoRDD<Document> mongo_rdd) throws IOException, ParseException {
        String from = request.getString("from");
        String to = request.getString("to");
        Integer topSize;
        if (request.isNull("top_size")) {
            topSize = 100;
        } else {
            topSize = request.getInt("top_size");
            if (topSize <= 0) {
                return;
            }
        }

        List<Tuple2<Integer, String>> articleViews = mongo_rdd.withPipeline(
                singletonList(
                        Document.parse("{ $match: { date : { $gte : \"" + from + "\", $lte: \"" + to + "\" } } }")
                )
        ).mapToPair(document -> new Tuple2<>(document.getString("article"), document.getInteger("views")))
                .reduceByKey((views1, views2) -> views1 + views2)
                .mapToPair(pair -> new Tuple2<>(pair._2(), pair._1()))
                .sortByKey(false)
                .take(topSize);


        JSONArray response = new JSONArray();

        Integer rank = 1;
        for (Tuple2 articleView : articleViews) {
            JSONObject article = new JSONObject();

            article.put("rank", rank);
            article.put("article", articleView._2());
            article.put("views", articleView._1());

            rank++;

            response.put(article);
        }

        JSONObject obj = new JSONObject();
        obj.put("request", request);
        obj.put("response", response);

        MyKafkaProducer.getProducer().send(new ProducerRecord<>(TOPIC_RESULTS, null, obj.toString()));
    }
}
