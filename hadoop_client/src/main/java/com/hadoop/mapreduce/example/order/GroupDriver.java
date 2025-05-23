package com.hadoop.mapreduce.example.order;

import com.hadoop.mapreduce.example.sort.SortMapper;
import com.hadoop.mapreduce.example.sort.SortReducer;
import com.hadoop.mapreduce.example.sort.SortSpeakBean;
import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.fs.Path;
import org.apache.hadoop.io.NullWritable;
import org.apache.hadoop.mapreduce.Job;
import org.apache.hadoop.mapreduce.lib.input.FileInputFormat;
import org.apache.hadoop.mapreduce.lib.output.FileOutputFormat;

import java.io.IOException;

public class GroupDriver {
    public static void main(String[] args) throws IOException, InterruptedException, ClassNotFoundException {
        //1. 获取配置文件对象，获取job对象实例
        Configuration configuration = new Configuration();
        Job job = Job.getInstance(configuration, "groupDriver");
        //2. 指定程序jar的本地路径
        job.setJarByClass(GroupDriver.class);
        //3. 指定Mapper/Reducer类
        job.setMapperClass(GroupMapper.class);
        job.setReducerClass(GroupReducer.class);
        //4. 指定Mapper输出的kv数据类型
        job.setMapOutputKeyClass(OrderBean.class);
        job.setMapOutputValueClass(NullWritable.class);
        //5. 指定最终输出的kv数据类型
        job.setOutputKeyClass(OrderBean.class);
        job.setOutputValueClass(NullWritable.class);

        //指定分区器
        job.setPartitionerClass(CustomPartitioner.class);
        // 指定reduceTask的数量，不使用默认的一个，不然分区效果不明显
        job.setNumReduceTasks(2);
        //指定使用groupingComparator
        job.setGroupingComparatorClass(CustomGroupingComparator.class);

        //6. 指定job处理的原始数据路径
//        FileInputFormat.setInputPaths(job, new Path(args[0]));//指定读取数据的原始路径
        FileInputFormat.setInputPaths(job, new Path("/Users/liuwei/Documents/code/data_engineer/hadoop_client/resources/group"));//指定读取数据的原始路径
        //7. 指定job输出结果路径
//        FileOutputFormat.setOutputPath(job, new Path(args[1]));//指定输出结果的路径
        FileOutputFormat.setOutputPath(job, new Path("/Users/liuwei/Documents/code/data_engineer/hadoop_client/resources/group/output"));//指定输出结果的路径
        //8. 提交作业
        boolean flag = job.waitForCompletion(true);
        //jvm退出， 正常退出0，非0则是错误退出
        System.exit(flag ? 0 : 1);
    }

}
