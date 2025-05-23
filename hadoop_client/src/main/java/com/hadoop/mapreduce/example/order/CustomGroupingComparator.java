package com.hadoop.mapreduce.example.order;

import org.apache.hadoop.io.WritableComparable;
import org.apache.hadoop.io.WritableComparator;

public class CustomGroupingComparator extends WritableComparator {
    // 重写其中的compare方法，通过这个方法来让MR接收orderId相同，则两个对象相等的规则，key相等


    public CustomGroupingComparator() {
        super(OrderBean.class,true); // 注册自定义的GroupingComparator接受OrderBean对象
    }

    @Override
    public int compare(WritableComparable a, WritableComparable b) { // a 和 b是orderbean对象
        //比较两个对象的orderId
        final OrderBean o1 = (OrderBean) a;
        final OrderBean o2 = (OrderBean) b;
        return o1.getOrderId().compareTo(o2.getOrderId());//1 0 -1
    }
}
