package com.hadoop.mapreduce.example.wordcount;

import org.apache.hadoop.io.IntWritable;
import org.apache.hadoop.io.LongWritable;
import org.apache.hadoop.io.Text;
import org.apache.hadoop.mapreduce.Mapper;

import java.io.IOException;

/*
需求：单词统计
1 继承Mapper类
2 Mapper类的泛型参数：共4个2对kv
2.1 第一对kv：map输出参数类型  LongWritable, Text --> 文本偏移量，一行文本内容
2.2 第二对kv: map输出从参数类型  Text, IntWritable--》单词， 单词数量   b
 */
public class WordCountMapper extends Mapper<LongWritable, Text, Text, IntWritable> {
    // 3 重写Mapper类的map方法
    /*
    1 接收到文本内容，转为String类型
    2 按照空格进行切分
    3 输出<单词，1>
     */

    // 提升全集变量，避免每次执

    Text word = new Text();
    IntWritable one = new IntWritable(1);

    @Override
    protected void map(LongWritable key, Text value, Mapper<LongWritable, Text, Text, IntWritable>.Context context) throws IOException, InterruptedException {
//        1 接收到文本内容，转为String类型
        String str = value.toString();
//        2 按照空格进行切分
        String[] words = str.split(" ");
//        3 输出<单词，1>

        //遍历数据
        for (String s : words) {
            word.set(s);
            context.write(word, one);
        }
    }
}
