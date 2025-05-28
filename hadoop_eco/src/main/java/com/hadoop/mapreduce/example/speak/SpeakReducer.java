package com.hadoop.mapreduce.example.speak;

import org.apache.hadoop.io.Text;
import org.apache.hadoop.mapreduce.Reducer;

import java.io.IOException;

public class SpeakReducer extends Reducer<Text, SpeakBean, Text, SpeakBean> {
    @Override
    protected void reduce(Text key, Iterable<SpeakBean> values, Reducer<Text, SpeakBean, Text, SpeakBean>.Context context) throws IOException, InterruptedException {
        Long self_duration = 0L;
        Long self_third_part_duration = 0L;
        //reduce方法的key: map输出的key
        //reduce 方法的value： map输出的kv中相同key的value组成的一个集合
        //reduce逻辑：遍历迭代器，累加时长即可
        for (SpeakBean bean : values) {
            Long selfDuration = bean.getSelfDuration();
            Long thirdPartDuration = bean.getThirdPartDuration();
            self_duration += selfDuration;
            self_third_part_duration += thirdPartDuration;
        }
        SpeakBean speakBean = new SpeakBean(self_duration, self_third_part_duration, key.toString());
        context.write(key, speakBean);
    }
}
