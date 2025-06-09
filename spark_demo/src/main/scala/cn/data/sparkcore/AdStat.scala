package cn.data.sparkcore

import org.apache.spark.{SparkConf, SparkContext}

object AdStat {
  def main(args: Array[String]): Unit = {
    val conf = new SparkConf().setAppName("AdStat").setMaster("local")
    val sc = new SparkContext(conf)
    sc.setLogLevel("WARN")
    val N = 3
    // 读文件
    // 字段:时间、省份、城市、用户、广告
    val lines = sc.textFile("/Users/weiliu/Documents/Learning/Data/code/data_engineer/spark_demo/data/advert.log")
    val rowRDD = lines.map(line => {
      var attr = line.split("\\s+")
      (attr(0), attr(1), attr(4))
    })
    // 需求1:统计每个省份点击 TOP3 的广告ID
    rowRDD.map({ case (_, province, adid) => ((province, adid), 1) })
      .reduceByKey(_ + _)
      .map { case ((province, adid), count) => (province, (adid, count)) }
      .groupByKey()
      .mapValues(_.toList.sortWith(_._2 > _._2).take(N))
      .foreach(println)

    // 需求2:统计每个省份每小时 TOP3 的广告ID
    rowRDD.map({ case (time, province, adid) => ((getHour(time), province, adid), 1) })
      .reduceByKey(_ + _)
      .map { case ((hour, province, adid), count) => ((hour, province), (adid, count)) }
      .groupByKey()
      .mapValues(_.toList.sortWith(_._2 > _._2).take(N))
      .foreach(println)

    sc.stop()
  }


  def getHour(timelong: String): String = {
    import org.joda.time.DateTime
    val dateTime = new DateTime(timelong.toLong)
    dateTime.getHourOfDay.toString
  }
}
