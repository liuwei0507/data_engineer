package cn.data;

import org.apache.spark.SparkConf;
import org.apache.spark.api.java.JavaRDD;
import org.apache.spark.api.java.JavaSparkContext;
import org.joda.time.DateTime;
import scala.Tuple2;
import scala.Tuple3;

import java.util.Comparator;
import java.util.stream.Collectors;
import java.util.stream.StreamSupport;

public class AdStat {
    public static void main(String[] args) {
        SparkConf conf = new SparkConf().setAppName("JavaWordCount").setMaster("local[*]");
        JavaSparkContext jsc = new JavaSparkContext(conf);
        jsc.setLogLevel("warn");
        int N = 3;
        JavaRDD<String> lines = jsc.textFile("/Users/weiliu/Documents/Learning/Data/code/data_engineer/spark_demo/data/advert.log");

        JavaRDD<Tuple3<String, String, String>> rowRDD = lines.map(line -> {
            String[] attrs = line.split("\\s+");
            return new Tuple3<>(attrs[0], attrs[1], attrs[4]);
        });

        // 需求1:统计每个省份点击 TOP3 的广告ID
        rowRDD.mapToPair(row -> {
                    return new Tuple2<>(new Tuple2<>(row._2(), row._3()), 1);
                })
                .reduceByKey((a, b) -> a + b)
                .mapToPair(row -> {
                    return new Tuple2<>(row._1._1(), new Tuple2<>(row._1._2(), row._2()));
                })
                .groupByKey()
                .mapValues(values -> {
                    return StreamSupport.stream(values.spliterator(), false)
                            .sorted(Comparator.comparingInt(Tuple2<String, Integer>::_2).reversed())
                            .limit(N)
                            .collect(Collectors.toList());
                }).foreach(result -> System.out.println(result._1 + ": " + result._2));

        // 需求2:统计每个省份每小时 TOP3 的广告ID
        rowRDD.mapToPair(row -> {
                    return new Tuple2<>(new Tuple3<>(getHour(row._1()), row._2(), row._3()), 1);
                }).reduceByKey((a, b) -> a + b)
                .mapToPair(row -> {
                    return new Tuple2<>(new Tuple2<>(row._1._1(), row._1._2()), new Tuple2<>(row._1._3(), row._2()));
                }).groupByKey()
                .mapValues(values -> {
                    return StreamSupport.stream(values.spliterator(), false)
                            .sorted(Comparator.comparingInt(Tuple2<String, Integer>::_2).reversed())
                            .limit(N)
                            .collect(Collectors.toList());
                }).foreach(result -> System.out.println(result._1 + ": " + result._2));


    }

    private static String getHour(String timeLong) {
        DateTime dateTime = new DateTime(Long.parseLong(timeLong));
        return String.valueOf(dateTime.getHourOfDay());
    }
}