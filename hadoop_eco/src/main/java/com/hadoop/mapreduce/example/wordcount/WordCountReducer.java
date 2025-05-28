package com.hadoop.mapreduce.example.wordcount;

import org.apache.hadoop.io.IntWritable;
import org.apache.hadoop.io.Text;
import org.apache.hadoop.mapreduce.Reducer;

import java.io.IOException;

/*
继承reducer类有四个泛型参数，2对kv
第一对kv与Mapper输出类型一致： Text, IntWritable
第二对kv，自己设计决定输出结果是什么类型 Text, IntWritable
 */
public class WordCountReducer extends Reducer<Text, IntWritable, Text, IntWritable> {
    // 重新reduce方法
    // Text key: map方法输出的key，本案例就是单词
    // Iterable<IntWritable> values: 一组key相同的kv的value组成的集合
    /*
    假设map方法：hello 1; hello 1; hello 1
    reduce 的key 和value是什么
    key: hello,
    values: <1,1,1>
    假设map方法输出: hello 1; hello1; hello1; hadoop 1; mapreduce 1;hadoop 1;
    第一次：key: hello, values:<1,1,1,>
    第二次： key: hadoop, values:<1,1>
    第三次： key mapreduce, values:<1>
     */
    IntWritable total = new IntWritable();

    @Override
    protected void reduce(Text key, Iterable<IntWritable> values, Reducer<Text, IntWritable, Text, IntWritable>.Context context) throws IOException, InterruptedException {
        //遍历 key对应的values，然后累加结果
        int sum = 0;
        for (IntWritable value : values) {
            int i = value.get();
            sum += i;
        }
        total.set(sum);
        context.write(key, total);
    }

}
