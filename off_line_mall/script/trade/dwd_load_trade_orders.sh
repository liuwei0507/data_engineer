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
set hive.exec.dynamic.partition.mode=nonstrict;
set hive.exec.dynamic.partition=true;
INSERT OVERWRITE TABLE dwd.dwd_trade_orders
    partition (dt)
SELECT orderId,
       orderNo,
       userId,
       status,
       productMoney,
       totalMoney,
       payMethod,
       isPay,
       areaId,
       tradeSrc,
       tradeType,
       isRefund,
       dataFlag,
       createTime,
       payTime,
       modifiedTime,
       case
           when modifiedTime is not null
               then from_unixtime(unix_timestamp(modifiedTime, 'yyyy-MM-dd HH:mm:ss'), 'yyyy-MM-dd')
           else from_unixtime(unix_timestamp(createTime, 'yyyy-MM-dd HH:mm:ss'), 'yyyy-MM-dd')
           end                                                                       as start_date,
       '9999-12-31'                                                                  as end_date,
       from_unixtime(unix_timestamp(createTime, 'yyyy-MM-ddHH:mm:ss'), 'yyyy-MM-dd') as dt
FROM ods.ods_trade_orders
WHERE dt = '$do_date'
union all
SELECT A.orderId,
       A.orderNo,
       A.userId,
       A.status,
       A.productMoney,
       A.totalMoney,
       A.payMethod,
       A.isPay,
       A.areaId,
       A.tradeSrc,
       A.tradeType,
       A.isRefund,
       A.dataFlag,
       A.createTime,
       A.payTime,
       A.modifiedTime,
       A.start_date,
       CASE
           WHEN B.orderid IS NOT NULL AND A.end_date > '$do_date' THEN date_add('$do_date', -1)
           ELSE A.end_date END                                                         AS end_date,
       from_unixtime(unix_timestamp(A.createTime, 'yyyy-MM-ddHH:mm:ss'), 'yyyy-MM-dd') as dt
FROM (SELECT *
      FROM dwd.dwd_trade_orders
      WHERE dt > date_add('$do_date', -15)) A
         left outer join (SELECT *
                          FROM ods.ods_trade_orders
                          WHERE dt = '$do_date') B
                         ON A.orderId = B.orderId;
"
hive -e "$sql"