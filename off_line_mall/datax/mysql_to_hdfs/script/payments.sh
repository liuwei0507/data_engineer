#!/bin/bash
source /etc/profile
JSON=/data/lagoudw/script
if [ -n "$1" ] ;then
    do_date=$1
else
    do_date=`date -d "-1 day" +%F`
fi

# 创建目录
hdfs dfs -mkdir -p /user/data/trade.db/payments/dt=$do_date
# 数据迁移
python2 $DATAX_HOME/bin/datax.py -p "-Ddo_date=$do_date" $JSON/trade/payments.json