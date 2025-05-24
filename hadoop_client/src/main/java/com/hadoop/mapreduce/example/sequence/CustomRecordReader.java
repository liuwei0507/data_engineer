package com.hadoop.mapreduce.example.sequence;

import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.fs.FSDataInputStream;
import org.apache.hadoop.fs.FileSystem;
import org.apache.hadoop.fs.Path;
import org.apache.hadoop.io.BytesWritable;
import org.apache.hadoop.io.IOUtils;
import org.apache.hadoop.io.Text;
import org.apache.hadoop.mapreduce.InputSplit;
import org.apache.hadoop.mapreduce.RecordReader;
import org.apache.hadoop.mapreduce.TaskAttemptContext;
import org.apache.hadoop.mapreduce.lib.input.FileSplit;

import java.io.IOException;

// 负责读取数据，一次读取整个文件内容，封装成kv输出
public class CustomRecordReader extends RecordReader<Text, BytesWritable> {
    private FileSplit split;
    // hadoop 配置文件对象
    private Configuration conf;

    // 定义key value的成员遍历
    private Text key = new Text();
    private BytesWritable value = new BytesWritable();

    // 初始化方法，把切片以及上下文提升为全局
    @Override
    public void initialize(InputSplit inputSplit, TaskAttemptContext context) throws IOException, InterruptedException {
        split = (FileSplit) inputSplit;
        conf = context.getConfiguration();
    }

    // 用来读取数据的方法
    private Boolean flag = true;

    @Override
    public boolean nextKeyValue() throws IOException, InterruptedException {
        // 对于当前split来说，只需要读取一次即可，因为一次就把整个文件全部读取了
        if (flag) {
            // 准备一个数组存放读取到的数据，数据大小是多少
            byte[] content = new byte[(int) split.getLength()];
            Path path = split.getPath();// 获取切片的path信息
            FileSystem fs = path.getFileSystem(conf);// 获取文件系统对象
            FSDataInputStream fis = fs.open(path);// 获取到输入流
            IOUtils.readFully(fis, content, 0, content.length);// 读取数据并发数据放入到byte[]

            // 封装key和value
            key.set(path.toString());
            value.set(content, 0, content.length);

            IOUtils.closeStream(fis);
            // 把再次读取的开关设置为false
            flag = false;
            return true;
        }
        return false;
    }

    // 获取key
    @Override
    public Text getCurrentKey() throws IOException, InterruptedException {
        return key;
    }

    // 获取value
    @Override
    public BytesWritable getCurrentValue() throws IOException, InterruptedException {
        return value;
    }


    // 获取进度
    @Override
    public float getProgress() throws IOException, InterruptedException {
        return 0;
    }

    //关闭资源
    @Override
    public void close() throws IOException {

    }
}
