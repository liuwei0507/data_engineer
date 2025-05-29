package com.hive.udf;

/**
 * UDF开发步骤
 * 创建maven java 工程，添加依赖
 * 开发java类继承UDF，实现evaluate 方法
 * 将项目打包上传服务器
 * 添加开发的jar包
 * 设置函数与自定义函数关联
 * 使用自定义函数
 */

import org.apache.hadoop.hive.ql.exec.UDF;
import org.apache.hadoop.io.Text;

/**
 * 需求:扩展系统 nvl 函数功能:
 * -- 系统内建的 nvl 函数
 * nvl(ename, "OK"): ename==null => 返回第二个参数
 * <p>
 * -- 要实现的函数功能
 * nvl(ename, "OK"): ename==null or ename=="" or ename=="  " => 返回第二个参数
 */
public class nvl extends UDF {
    public Text evaluate(final Text t, final Text x) {
        if (t == null || t.toString().trim().isEmpty()) {
            return x;
        }
        return t;
    }
}
