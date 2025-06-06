#!/bin/bash
source /etc/profile
# 可以输入日期;如果未输入日期取昨天的时间
if [ -n "$1" ]
then
    do_date=$1
else
    do_date=`date -d "-1 day" +%F`
fi

# 定义要执行的SQL
sql="
insert overwrite table dim.dim_trade_payment
partition(dt='$do_date')
select id, payName
from ods.ods_trade_payments
where dt='$do_date';
"
hive -e "$sql"