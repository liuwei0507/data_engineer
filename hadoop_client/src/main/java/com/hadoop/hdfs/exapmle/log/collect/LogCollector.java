package com.hadoop.hdfs.exapmle.log.collect;

import java.util.Timer;

public class LogCollector {

    /*
    - 定时采集滚动完毕的日志文件
    - 将采集的文件上传到临时目录
    - 备份日志文件
     */
    public static void main(String[] args) {
        Timer timer = new Timer();
        // 定时采集任务的调度
        // task：擦剂的业务逻辑
        // 延迟时间
        //延迟周期
        timer.schedule(new LogCollectorTask(), 10, 3600 * 1000);
    }
}
