package com.hadoop.mapreduce.example.output;

import com.hadoop.mapreduce.example.sequence.CustomInputFormat;
import com.hadoop.mapreduce.example.sequence.SequenceMapper;
import com.hadoop.mapreduce.example.sequence.SequenceReducer;
import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.fs.Path;
import org.apache.hadoop.io.BytesWritable;
import org.apache.hadoop.io.NullWritable;
import org.apache.hadoop.io.Text;
import org.apache.hadoop.mapreduce.Job;
import org.apache.hadoop.mapreduce.lib.input.FileInputFormat;
import org.apache.hadoop.mapreduce.lib.output.FileOutputFormat;

import java.io.IOException;

public class OutputDriver {
    public static void main(String[] args) throws IOException, InterruptedException, ClassNotFoundException {
        //1. 获取配置文件对象，获取job对象实例
        Configuration configuration = new Configuration();
        Job job = Job.getInstance(configuration, "outputDriver");
        //2. 指定程序jar的本地路径
        job.setJarByClass(OutputDriver.class);
        //3. 指定Mapper/Reducer类
        job.setMapperClass(OutputMapper.class);
        job.setReducerClass(OutputReducer.class);
        //4. 指定Mapper输出的kv数据类型
        job.setMapOutputKeyClass(Text.class);
        job.setMapOutputValueClass(NullWritable.class);
        //5. 指定最终输出的kv数据类型
        job.setOutputKeyClass(Text.class);
        job.setOutputValueClass(NullWritable.class);

        //指定使用自定义outputformat
        job.setOutputFormatClass(CustomOutputFormat.class);
        //6. 指定job处理的原始数据路径
        FileInputFormat.setInputPaths(job, new Path("/Users/liuwei/Documents/code/data_engineer/hadoop_client/resources/output"));//指定读取数据的原始路径
        //7. 指定job输出结果路径, 因为mr默认要输出一个success等标识文件
        FileOutputFormat.setOutputPath(job, new Path("/Users/liuwei/Documents/code/data_engineer/hadoop_client/resources/output/out"));//指定输出结果的路径
        //8. 提交作业
        boolean flag = job.waitForCompletion(true);
        //jvm退出， 正常退出0，非0则是错误退出
        System.exit(flag ? 0 : 1);
    }

}
