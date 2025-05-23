package com.hadoop.mapreduce.example.sort;

import org.apache.hadoop.io.WritableComparable;

import java.io.DataInput;
import java.io.DataOutput;
import java.io.IOException;

/*
因为这个类的实例对象要作为map输出的key，所以要实现writableCOmparable接口
因为这个类的实例对象要作为map输出的key，所以要实现writableCOmparable接口
 */
public class SortSpeakBean implements WritableComparable<SortSpeakBean> {
    //定义属性
    private Long selfDuration;
    private Long thirdPartDuration;
    private String deviceId;
    private Long sumDuration;

    public SortSpeakBean(Long selfDuration, Long thirdPartDuration, String deviceId, Long sumDuration) {
        this.selfDuration = selfDuration;
        this.thirdPartDuration = thirdPartDuration;
        this.deviceId = deviceId;
        this.sumDuration = sumDuration;
    }

    public SortSpeakBean() {

    }

    // 指定排序规则,希望按照总时长进行排序
    @Override
    public int compareTo(SortSpeakBean o) { // 返回值三种，0:等于 1:小于，-1: 大于
        // 指定按照Bean对象的总时长字段进行比较
//        if (this.sumDuration > o.sumDuration) {
//            return -1;
//        } else if (this.sumDuration < o.sumDuration) {
//            return 1;
//        } else {
//
//            return 0;
//        }
//        return o.sumDuration.compareTo(this.sumDuration);
        return this.sumDuration.compareTo(o.sumDuration);
    }

    //序列化方法
    @Override
    public void write(DataOutput out) throws IOException {
        out.writeLong(selfDuration);
        out.writeLong(thirdPartDuration);
        out.writeUTF(deviceId);
        out.writeLong(sumDuration);
    }

    //反序列化方法
    @Override
    public void readFields(DataInput in) throws IOException {
        this.selfDuration = in.readLong();
        this.thirdPartDuration = in.readLong();
        this.deviceId = in.readUTF();
        this.sumDuration = in.readLong();
    }

    public Long getSelfDuration() {
        return selfDuration;
    }

    public void setSelfDuration(Long selfDuration) {
        this.selfDuration = selfDuration;
    }

    public Long getThirdPartDuration() {
        return thirdPartDuration;
    }

    public void setThirdPartDuration(Long thirdPartDuration) {
        this.thirdPartDuration = thirdPartDuration;
    }

    public String getDeviceId() {
        return deviceId;
    }

    public void setDeviceId(String deviceId) {
        this.deviceId = deviceId;
    }

    public Long getSumDuration() {
        return sumDuration;
    }

    public void setSumDuration(Long sumDuration) {
        this.sumDuration = sumDuration;
    }

    @Override
    public String toString() {
        return selfDuration + "\t" + thirdPartDuration + "\t" + deviceId + "\t" + sumDuration;
    }
}
