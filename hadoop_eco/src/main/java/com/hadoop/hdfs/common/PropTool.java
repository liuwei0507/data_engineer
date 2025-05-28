package com.hadoop.hdfs.common;

import com.hadoop.hdfs.exapmle.log.collect.LogCollectorTask;

import java.io.IOException;
import java.util.Properties;

/**
 * 单例模式
 */
public class PropTool {
    //累加载时初始化执行一次即可
    // 使用静态代码块实现 饿汉时加载

    private static Properties properties = null;

    static {
        properties = new Properties();
        try {
            properties.load(LogCollectorTask.class.getClassLoader().getResourceAsStream("collector.properties"));
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static Properties getProp() {
        return properties;
    }
}
