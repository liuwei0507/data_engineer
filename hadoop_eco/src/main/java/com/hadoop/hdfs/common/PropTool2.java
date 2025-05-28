package com.hadoop.hdfs.common;

import com.hadoop.hdfs.exapmle.log.collect.LogCollectorTask;

import java.io.IOException;
import java.util.Properties;

/**
 * 单例模式
 */
public class PropTool2 {
    //累加载时初始化执行一次即可
    // 使用静态代码块实现 懒汉时加载
    // 添加volatile禁止指令重排
    private static volatile Properties properties = null;

    /**
     * 西安城安全问题
     *
     * @return
     */
//    public static Properties getProp() {
//        properties = new Properties();
//        try {
//            properties.load(LogCollectorTask.class.getClassLoader().getResourceAsStream("collector.properties"));
//        } catch (IOException e) {
//            e.printStackTrace();
//        }
//        return properties;
//    }
    public static synchronized Properties getProp_1() {
        if (properties == null) {
            properties = new Properties();
            try {
                properties.load(LogCollectorTask.class.getClassLoader().getResourceAsStream("collector.properties"));
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        return properties;
    }

    public static Properties getProp_2() {
        synchronized ("lock") {
            if (properties == null) {
                properties = new Properties();
                try {
                    properties.load(LogCollectorTask.class.getClassLoader().getResourceAsStream("collector.properties"));
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
        return properties;
    }

    public static Properties getProp() {
        if (properties == null) {
            synchronized ("lock") {
                if (properties == null) {
                    properties = new Properties();
                    try {
                        properties.load(LogCollectorTask.class.getClassLoader().getResourceAsStream("collector.properties"));
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            }
        }
        return properties;
    }
}
