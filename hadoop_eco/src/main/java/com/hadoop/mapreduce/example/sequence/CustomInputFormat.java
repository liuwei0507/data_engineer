package com.hadoop.mapreduce.example.sequence;

import org.apache.hadoop.fs.Path;
import org.apache.hadoop.io.BytesWritable;
import org.apache.hadoop.io.Text;
import org.apache.hadoop.mapreduce.InputSplit;
import org.apache.hadoop.mapreduce.JobContext;
import org.apache.hadoop.mapreduce.RecordReader;
import org.apache.hadoop.mapreduce.TaskAttemptContext;
import org.apache.hadoop.mapreduce.lib.input.FileInputFormat;

import java.io.IOException;

// 自定义inputformat读取多个小文件合并为一个SequenceFile文件
//SequenceFile文件中以lv形式存储文件， key -> 文件路径+文件名， value --> 文件的整个内容
//TextInputFormat 中泛型是LongWritable： 文本偏移量，Text：一行文本的内容；指明当前input format的输出数据类型
//自定义inputformat： key -> 文件路径+文件名， value --> 文件的整个内容(二进制内容)
public class CustomInputFormat extends FileInputFormat<Text, BytesWritable> {
    //重写是否可切分


    @Override
    protected boolean isSplitable(JobContext context, Path filename) {
        // 对于当前文件需求，不需要把文件切分，保证一个切片就是一个文件
        return false;
    }

    //recordReader 就是来读取数据的对象
    @Override
    public RecordReader<Text, BytesWritable> createRecordReader(InputSplit inputSplit, TaskAttemptContext context) throws IOException, InterruptedException {
        CustomRecordReader recordReader = new CustomRecordReader();
        recordReader.initialize(inputSplit, context);
        return recordReader;

    }
}
