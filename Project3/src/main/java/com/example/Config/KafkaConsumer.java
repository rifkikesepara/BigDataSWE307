package com.example.Config;

import com.datastax.spark.connector.japi.CassandraJavaUtil;
import com.datastax.spark.connector.japi.SparkContextJavaFunctions;
import com.example.DTO.DataGenerator;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.apache.spark.SparkConf;
import org.apache.spark.SparkContext;
import org.apache.spark.api.java.JavaRDD;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;
import scala.Tuple2;

import java.util.List;

@Component
public class KafkaConsumer {
    public static List<Tuple2<Integer, Float>> expenses;
    public final SparkContext sparkContext;

    KafkaConsumer(){
        SparkConf conf = new SparkConf().setAppName("Java-Spark-Cassandra").setMaster("local[*]");

        sparkContext = new SparkContext(conf);
        SparkContextJavaFunctions functions= CassandraJavaUtil.javaFunctions(sparkContext);

        JavaRDD<DataGenerator> empRDD=functions.cassandraTable("test","generated",CassandraJavaUtil.mapRowTo(DataGenerator.class));

        JavaRDD<Tuple2<Integer, Float>> userPaymentsRDD = empRDD.mapToPair(data -> new Tuple2<>(data.getUser_id(), data.getPayment()))
                .reduceByKey(Float::sum).map(tuple -> new Tuple2<>(tuple._1(), tuple._2()));

        var list=userPaymentsRDD.collect();
        expenses=list;
    }

    //create different queues for each userid
    @KafkaListener(topicPattern = "user_.*")
    public void consumer(ConsumerRecord<String, String> record) {
        System.out.println("message = "+record.value());
        SparkContextJavaFunctions functions= CassandraJavaUtil.javaFunctions(sparkContext);

        JavaRDD<DataGenerator> empRDD=functions.cassandraTable("test","generated",CassandraJavaUtil.mapRowTo(DataGenerator.class));
        JavaRDD<Tuple2<Integer, Float>> userPaymentsRDD = empRDD.mapToPair(data -> new Tuple2<>(data.getUser_id(), data.getPayment()))
                .reduceByKey((a, b) -> a + b) .map(tuple -> new Tuple2<>(tuple._1(), tuple._2()));

        var list=userPaymentsRDD.collect();

        System.out.println(list);
        expenses=list;

//        sparkContext.stop();

    }
}
