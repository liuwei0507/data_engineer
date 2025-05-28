package com.hadoop.mapreduce.example.partition;

import org.apache.hadoop.io.Text;
import org.apache.hadoop.mapreduce.Partitioner;

public class CustomPartitioner extends Partitioner<Text, PartitionBean> {
    @Override
    public int getPartition(Text text, PartitionBean partitionBean, int i) {
        int partition;
        String appKey = text.toString();
        if (appKey.equals("kar")) {
            partition = 1;
        } else if (appKey.equals("pandora")) {
            partition = 2;
        } else {
            partition = 0;
        }
        return partition;
    }
}
