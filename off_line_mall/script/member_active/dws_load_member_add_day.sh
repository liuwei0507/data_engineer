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
insert into table dws.dws_member_add_day
select t1.device_id,
       t1.uid,
       t1.app_v,
       t1.os_type,
       t1.language,
       t1.channel,
       t1.area,
       t1.brand,
       '$do_date'
from dws.dws_member_start_day t1
         left join dws.dws_member_add_day t2
                   on t1.device_id = t2.device_id
where t1.dt = '$do_date'
  and t2.device_id is null;
"
hive -e "$sql"