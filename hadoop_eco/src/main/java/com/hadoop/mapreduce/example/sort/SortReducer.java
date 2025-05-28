package com.hadoop.mapreduce.example.sort;

import org.apache.hadoop.io.NullWritable;
import org.apache.hadoop.mapreduce.Reducer;

import java.io.IOException;

public class SortReducer extends Reducer<SortSpeakBean, NullWritable, SortSpeakBean, NullWritable> {
    //reduce方法调用的是相同key的value组成一个集合调用一次

    /*
    java中如何判断两个对象是否相等
    根据equals方法
     */
    @Override
    protected void reduce(SortSpeakBean key, Iterable<NullWritable> values, Reducer<SortSpeakBean, NullWritable, SortSpeakBean, NullWritable>.Context context) throws IOException, InterruptedException {
        // 讨论按照总流量排序这个事情，还需要reduce端处理吗？因为之前以及利用MR的shuffle机制对数据进行了排序

        // 为了避免前面compareTo方法导致总流量相等，而合并了key，所以遍历values获取每个key(bean对象)
        for (NullWritable value : values) {// 遍历value的同时，key也会随着遍历
            context.write(key, value);
        }
    }
}
