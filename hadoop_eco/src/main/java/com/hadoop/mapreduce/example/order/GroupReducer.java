package com.hadoop.mapreduce.example.order;

import org.apache.hadoop.io.NullWritable;
import org.apache.hadoop.mapreduce.Reducer;

import java.io.IOException;

public class GroupReducer extends Reducer<OrderBean, NullWritable,OrderBean, NullWritable> {
    // key: reduce方法的key注意是一组相同key的kv的第一个key作为传入reduce方法的key，因为我们以及指定了排序规则
    //按照金额降序排列，则第一个key就是金额最大的交易数据
    //values: 一组相同key的kv对中v的集合
    // 如何判断key是否相同，自定义对象是需要我么指定一个规则，这个规则通过GroupingComparator来指定
    @Override
    protected void reduce(OrderBean key, Iterable<NullWritable> values, Reducer<OrderBean, NullWritable, OrderBean, NullWritable>.Context context) throws IOException, InterruptedException {
        // 直接输出key就是金额最大的交易
        context.write(key,NullWritable.get());
    }
}
