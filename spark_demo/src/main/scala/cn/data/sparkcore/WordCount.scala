package cn.data.sparkcore

import org.apache.spark.rdd.RDD
import org.apache.spark.{SparkConf, SparkContext}

object WordCount {
  def main(args: Array[String]): Unit = {
    val conf = new SparkConf().setMaster("local").setAppName("WordCount")
    val sc = new SparkContext(conf)
    // val lines: RDD[String] = sc.textFile("hdfs://linux121:9000/wcinput/wc.txt")
    val lines: RDD[String] = sc.textFile("/Users/weiliu/Documents/Learning/Data/code/data_engineer/spark_demo/data/wordcount.txt")
    // val lines: RDD[String] = sc.textFile("data/wc.dat")
    lines.flatMap(_.split(" ")).map((_,1)).reduceByKey(_ + _).collect().foreach(println)
    sc.stop()
  }
}
