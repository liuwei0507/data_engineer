package com.hadoop.mapreduce.example.partition;

import com.hadoop.mapreduce.example.speak.SpeakBean;
import com.hadoop.mapreduce.example.speak.SpeakMapper;
import com.hadoop.mapreduce.example.speak.SpeakReducer;
import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.fs.Path;
import org.apache.hadoop.io.NullWritable;
import org.apache.hadoop.io.Text;
import org.apache.hadoop.mapreduce.Job;
import org.apache.hadoop.mapreduce.lib.input.FileInputFormat;
import org.apache.hadoop.mapreduce.lib.output.FileOutputFormat;

import java.io.IOException;

/*
封装任务并提交运行
 */
public class PartitionDriver {
    public static void main(String[] args) throws IOException, InterruptedException, ClassNotFoundException {
        //1. 获取配置文件对象，获取job对象实例
        Configuration configuration = new Configuration();
        Job job = Job.getInstance(configuration, "partitionDriver");
        //2. 指定程序jar的本地路径
        job.setJarByClass(PartitionDriver.class);
        //3. 指定Mapper/Reducer类
        job.setMapperClass(PartitionMapper.class);
        job.setReducerClass(PartitionReducer.class);
        //4. 指定Mapper输出的kv数据类型
        job.setMapOutputKeyClass(Text.class);
        job.setMapOutputValueClass(PartitionBean.class);
        //5. 指定最终输出的kv数据类型
        job.setOutputKeyClass(NullWritable.class);
        job.setOutputValueClass(PartitionBean.class);

        // 设置自定义分区逻辑
        job.setPartitionerClass(CustomPartitioner.class);
        job.setNumReduceTasks(5);

        //6. 指定job处理的原始数据路径
        FileInputFormat.setInputPaths(job, new Path(args[0]));//指定读取数据的原始路径
        //7. 指定job输出结果路径
        FileOutputFormat.setOutputPath(job, new Path(args[1]));//指定输出结果的路径
        //8. 提交作业
        boolean flag = job.waitForCompletion(true);
        //jvm退出， 正常退出0，非0则是错误退出
        System.exit(flag ? 0 : 1);
    }

}
