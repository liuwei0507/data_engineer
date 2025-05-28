package com.hadoop.mapreduce.example.sort;

import org.apache.hadoop.io.LongWritable;
import org.apache.hadoop.io.NullWritable;
import org.apache.hadoop.io.Text;
import org.apache.hadoop.mapreduce.Mapper;

import java.io.IOException;

public class SortMapper extends Mapper<LongWritable, Text, SortSpeakBean, NullWritable> {

    SortSpeakBean bean = new SortSpeakBean();

    @Override
    protected void map(LongWritable key, Text value, Mapper<LongWritable, Text, SortSpeakBean, NullWritable>.Context context) throws IOException, InterruptedException {
        String[] fields = value.toString().split("\t");

        bean.setDeviceId(fields[0]);
        bean.setSelfDuration(Long.parseLong(fields[1]));
        bean.setThirdPartDuration(Long.parseLong(fields[1]));
        bean.setSumDuration(Long.parseLong(fields[4]));

        context.write(bean, NullWritable.get());
    }


}
