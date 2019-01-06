import com.mongodb.spark.MongoSpark;
import org.apache.spark.api.java.JavaRDD;
import org.apache.spark.api.java.JavaSparkContext;
import org.apache.spark.api.java.function.Function;
import org.apache.spark.sql.SparkSession;
import org.bson.Document;


import static java.util.Arrays.asList;

public final class SimpleApp {

    public static void main(final String[] args) throws InterruptedException {

        SparkSession spark = SparkSession.builder()
//                .master("local")
                .appName("MongoSparkConnectorIntro")
                .config("spark.mongodb.input.uri", "mongodb://mongo-0.mongodb-service/test.myCollection")
                .config("spark.mongodb.output.uri", "mongodb://mongo-0.mongodb-service/test.myCollection")
                .getOrCreate();

        JavaSparkContext jsc = new JavaSparkContext(spark.sparkContext());

        // Create a RDD of 10 documents
        JavaRDD<Document> documents = jsc.parallelize(asList(1, 2, 3, 4, 5, 6, 7, 8, 9, 10)).map
                (new Function<Integer, Document>() {
                    public Document call(final Integer i) throws Exception {
                        return Document.parse("{test: " + i + "}");
                    }
                });

        /*Start Example: Save data from RDD to MongoDB*****************/
        MongoSpark.save(documents);
        /*End Example**************************************************/

        jsc.close();
    }
}
