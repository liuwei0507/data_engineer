package cn.data.sparkcore

import org.apache.spark.{SparkConf, SparkContext}
import scala.math.random

object SparkPi {
  def main(args: Array[String]): Unit = {
    val conf = new SparkConf().setAppName(this.getClass.getCanonicalName).setMaster("local[*]")
    val sc = new SparkContext(conf)
    sc.setLogLevel("WARN")
    val slices = if (args.length > 0) args(0).toInt else 100
    val n = 100000
    val count = sc.makeRDD(1 to n, slices).map(idx => {
      val (x, y) = (random, random)
      if (x * x + y * y < 1) 1 else 0
    }).reduce(_ + _)
    // 输出结果
    println("Pi is roughly " + 4.0 * count / n)

  }
}
