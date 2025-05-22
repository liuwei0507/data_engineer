package com.hadoop.mapreduce.example.speak;

import org.apache.hadoop.io.LongWritable;
import org.apache.hadoop.io.Text;
import org.apache.hadoop.mapreduce.Mapper;

import java.io.IOException;

/*
四个泛型参数，分两队kv
第一对kv： map输入参数的kv类型： k -> 一行文本偏移量，v -> 一行文本内容
第二对kv: map输出参数的kv类型：k--> map输出的key类型， v: map 输出的value类型
 */
public class SpeakMapper extends Mapper<LongWritable, Text, Text, SpeakBean> {

    /*
    1 转换接收到的text为String
    2 按照制表符进行切分；得到自由内容时长，第三方内容时长，设备ID，封装SpeakBean
    3 直接输出： k -> 设备ID，value: SpeakBean
     */

    Text device_id = new Text();
    @Override
    protected void map(LongWritable key, Text value, Mapper<LongWritable, Text, Text, SpeakBean>.Context context) throws IOException, InterruptedException {
        String line = value.toString();
        String[] fields = line.split("\t");
        String selfDuration = fields[fields.length - 3];
        String thirdPartDuration = fields[fields.length - 2];
        String deviceId = fields[1];
        SpeakBean speakBean = new SpeakBean(Long.parseLong(selfDuration), Long.parseLong(thirdPartDuration), deviceId);
        device_id.set(deviceId);
        context.write(device_id, speakBean);
    }
}
