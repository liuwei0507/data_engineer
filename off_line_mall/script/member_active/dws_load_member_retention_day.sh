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
insert overwrite table dws.dws_member_retention_day
    partition (dt = '$do_date')
    (select t2.device_id,
            t2.uid,
            t2.app_v,
            t2.os_type,
            t2.language,
            t2.channel,
            t2.area,
            t2.brand,
            t2.dt add_date,
            1
     from dws.dws_member_start_day t1
              join dws.dws_member_add_day t2 on t1.device_id = t2.device_id
     where t2.dt = date_add('$do_date', -1)
       and t1.dt = '$do_date'
     union all
     select t2.device_id,
            t2.uid,
            t2.app_v,
            t2.os_type,
            t2.language,
            t2.channel,
            t2.area,
            t2.brand,
            t2.dt add_date,
            2
     from dws.dws_member_start_day t1
              join dws.dws_member_add_day t2 on t1.device_id = t2.device_id
     where t2.dt = date_add('$do_date', -2)
       and t1.dt = '$do_date'
     union all
     select t2.device_id,
            t2.uid,
            t2.app_v,
            t2.os_type,
            t2.language,
            t2.channel,
            t2.area,
            t2.brand,
            t2.dt add_date,
            3
     from dws.dws_member_start_day t1
              join dws.dws_member_add_day t2
                   on t1.device_id = t2.device_id
     where t2.dt = date_add('$do_date', -3)
       and t1.dt = '$do_date');
"
hive -e "$sql"