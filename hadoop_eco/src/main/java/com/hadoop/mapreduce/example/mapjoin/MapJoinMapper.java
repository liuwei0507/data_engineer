package com.hadoop.mapreduce.example.mapjoin;

import org.apache.commons.lang.StringUtils;
import org.apache.hadoop.io.LongWritable;
import org.apache.hadoop.io.NullWritable;
import org.apache.hadoop.io.Text;
import org.apache.hadoop.mapreduce.InputSplit;
import org.apache.hadoop.mapreduce.Mapper;
import org.apache.hadoop.mapreduce.lib.input.FileSplit;

import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.HashMap;

/**
 * 使用map端join完成投递行为与职位数据的关联
 * map端混存所有的职位数据
 * map方法读取的文件是投递行为数据
 * 基于有滴行为数据的positionId去缓存中查询出positionname，输出即可
 * 这个job无需reducetask,setnumreduceTask=0
 */
public class MapJoinMapper extends Mapper<LongWritable, Text, Text, NullWritable> {
    Text k = new Text();

    // map 任务启东市初始化执行一次
    HashMap<String, String> map = new HashMap<>();

    // 加载职位数据
    @Override
    protected void setup(Mapper<LongWritable, Text, Text, NullWritable>.Context context) throws IOException, InterruptedException {
        //读取缓存文件
        InputStreamReader inputStreamReader = new InputStreamReader(new FileInputStream("/Users/liuwei/Documents/code/data_engineer/hadoop_client/resources/mapjoin/cache/position.txt"), "UTF-8");
        BufferedReader reader = new BufferedReader(inputStreamReader);
        //读取职位数据解析为kv类型（hashmap）: key positionId, value:positionName
        String line;
        while (StringUtils.isNotEmpty(line = reader.readLine())) {
            String[] fields = line.split("\t");
            map.put(fields[0], fields[1]);
        }
        reader.lines();
    }

    @Override
    protected void map(LongWritable key, Text value, Context context) throws IOException, InterruptedException {
        String line = value.toString();
        String[] arr = line.split("\t");
        DeliveryBean bean = new DeliveryBean();
        // 都是投递行为数据
        String positionName = map.get(arr[1]);

        k.set(line+"\t"+positionName);
        context.write(k, NullWritable.get());
    }
}
