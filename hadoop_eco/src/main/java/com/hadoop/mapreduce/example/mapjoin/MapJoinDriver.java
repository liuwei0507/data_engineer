package com.hadoop.mapreduce.example.mapjoin;

import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.fs.Path;
import org.apache.hadoop.io.NullWritable;
import org.apache.hadoop.io.Text;
import org.apache.hadoop.mapreduce.Job;
import org.apache.hadoop.mapreduce.lib.input.FileInputFormat;
import org.apache.hadoop.mapreduce.lib.output.FileOutputFormat;

import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;


public class MapJoinDriver {
    public static void main(String[] args) throws IOException, InterruptedException, ClassNotFoundException, URISyntaxException {
        //1. 获取配置文件对象，获取job对象实例
        Configuration configuration = new Configuration();
        Job job = Job.getInstance(configuration, "mapJoinDriver");
        //2. 指定程序jar的本地路径
        job.setJarByClass(MapJoinDriver.class);
        //3. 指定Mapper/Reducer类
        job.setMapperClass(MapJoinMapper.class);
//        job.setReducerClass(ReduceJoinReducer.class);
        //4. 指定Mapper输出的kv数据类型
//        job.setMapOutputKeyClass(Text.class);
//        job.setMapOutputValueClass(DeliveryBean.class);
        //5. 指定最终输出的kv数据类型
        job.setOutputKeyClass(Text.class);
        job.setOutputValueClass(NullWritable.class);
        //6. 指定job处理的原始数据路径
//        FileInputFormat.setInputPaths(job, new Path(args[0]));//指定读取数据的原始路径
        FileInputFormat.setInputPaths(job, new Path("/Users/liuwei/Documents/code/data_engineer/hadoop_client/resources/mapjoin/input"));//指定读取数据的原始路径
        //7. 指定job输出结果路径
//        FileOutputFormat.setOutputPath(job, new Path(args[1]));//指定输出结果的路径
        FileOutputFormat.setOutputPath(job, new Path("/Users/liuwei/Documents/code/data_engineer/hadoop_client/resources/mapjoin/output"));//指定输出结果的路径
        //设置加载缓存文件
        job.addCacheFile(new URI("file:///Users/liuwei/Documents/code/data_engineer/hadoop_client/resources/mapjoin/cache"));
        job.setNumReduceTasks(0);
        //8. 提交作业
        boolean flag = job.waitForCompletion(true);
        //jvm退出， 正常退出0，非0则是错误退出
        System.exit(flag ? 0 : 1);
    }

}
