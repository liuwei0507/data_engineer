package com.hadoop.mapreduce.example.reducejoin;

import org.apache.commons.beanutils.BeanUtils;
import org.apache.hadoop.io.NullWritable;
import org.apache.hadoop.io.Text;
import org.apache.hadoop.mapreduce.Reducer;

import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;

public class ReduceJoinReducer extends Reducer<Text, DeliveryBean, DeliveryBean, NullWritable> {
    @Override
    protected void reduce(Text key, Iterable<DeliveryBean> values, Reducer<Text, DeliveryBean, DeliveryBean, NullWritable>.Context context) throws IOException, InterruptedException {
        // 相同positionId的数据放到一起（1个职位数据，n个投递行为数据）
        ArrayList<DeliveryBean> deliveryBeans = new ArrayList<>();
        DeliveryBean positionBean = new DeliveryBean();
        for (DeliveryBean bean : values) {
            String flag = bean.getFlag();
            if (flag.equalsIgnoreCase("deliver")) {
                //投递行为
                DeliveryBean newBean = new DeliveryBean();
                try {
                    BeanUtils.copyProperties(newBean, bean);
                    deliveryBeans.add(newBean);
                } catch (IllegalAccessException e) {
                    throw new RuntimeException(e);
                } catch (InvocationTargetException e) {
                    throw new RuntimeException(e);
                }
            } else {
                //职位
                try {
                    BeanUtils.copyProperties(positionBean, bean);
                } catch (IllegalAccessException e) {
                    throw new RuntimeException(e);
                } catch (InvocationTargetException e) {
                    throw new RuntimeException(e);
                }
            }
        }
        // 遍历投递行为数据，拼接positionName
        for (DeliveryBean bean : deliveryBeans) {
            bean.setPositionName(positionBean.getPositionName());
            context.write(bean, NullWritable.get());
        }
    }
}
