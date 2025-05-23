package com.hadoop.mapreduce.example.reducejoin;

import org.apache.hadoop.io.LongWritable;
import org.apache.hadoop.io.Text;
import org.apache.hadoop.mapreduce.InputSplit;
import org.apache.hadoop.mapreduce.Mapper;
import org.apache.hadoop.mapreduce.lib.input.FileSplit;

import java.io.IOException;

public class ReduceJoinMapper extends Mapper<LongWritable, Text, Text, DeliveryBean> {

    // map 任务启东市初始化执行一次
    String name = "";
    Text k = new Text();
    @Override
    protected void setup(Mapper<LongWritable, Text, Text, DeliveryBean>.Context context) throws IOException, InterruptedException {
        InputSplit inputSplit = context.getInputSplit();
        FileSplit split = (FileSplit) inputSplit;
        name = split.getPath().getName();
    }

    @Override
    protected void map(LongWritable key, Text value, Mapper<LongWritable, Text, Text, DeliveryBean>.Context context) throws IOException, InterruptedException {
        String line = value.toString();
        String[] arr = line.split("\t");
        DeliveryBean bean = new DeliveryBean();
        if (name.startsWith("deliver_info")) {

            bean.setUserId(arr[0]);
            bean.setPositionId(arr[1]);
            bean.setDate(arr[2]);

            bean.setPositionName("");
            bean.setFlag("deliver");
        } else {
            bean.setUserId("");
            bean.setPositionId(arr[0]);
            bean.setDate("");

            bean.setPositionName(arr[1]);
            bean.setFlag("position");
        }
        k.set(bean.getPositionId());
        context.write(k, bean);
    }
}
