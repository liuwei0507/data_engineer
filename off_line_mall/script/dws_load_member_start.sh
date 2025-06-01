#!/bin/bash
source /etc/profile
# 可以输入日期;如果未输入日期取昨天的时间 if [ -n "$1" ]
then
    do_date=$1
else
    do_date=`date -d "-1 day" +%F`
fi
# 定义要执行的SQL
# 汇总得到每日活跃会员信息;每日数据汇总得到每周、每月数据
sql="
insert overwrite table dws.dws_member_start_day
    partition(dt='$do_date')
select device_id,
       concat_ws('|', collect_set(uid)),
       concat_ws('|', collect_set(app_v)),
       concat_ws('|', collect_set(os_type)),
       concat_ws('|', collect_set(language)),
       concat_ws('|', collect_set(channel)),
       concat_ws('|', collect_set(area)),
       concat_ws('|', collect_set(brand))
    from dwd.dwd_start_log
where dt='$do_date'
group by device_id;

insert overwrite table dws.dws_member_start_week
    partition(dt='$do_date')
select device_id,
       concat_ws('|', collect_set(uid)),
       concat_ws('|', collect_set(app_v)),
       concat_ws('|', collect_set(os_type)),
       concat_ws('|', collect_set(language)),
       concat_ws('|', collect_set(channel)),
       concat_ws('|', collect_set(area)),
       concat_ws('|', collect_set(brand)),
       date_add(next_day('$do_date', 'mo'), -7)
from dws.dws_member_start_day
where dt >= date_add(next_day('$do_date', 'mo'), -7)
  and dt <= '$do_date'
group by device_id;


insert overwrite table dws.dws_member_start_month
    partition(dt='$do_date')
select device_id,
       concat_ws('|', collect_set(uid)),
       concat_ws('|', collect_set(app_v)),
       concat_ws('|', collect_set(os_type)),
       concat_ws('|', collect_set(language)),
       concat_ws('|', collect_set(channel)),
       concat_ws('|', collect_set(area)),
       concat_ws('|', collect_set(brand)),
       date_format('$do_date', 'yyyy-MM')
from dws.dws_member_start_day
where dt >= date_format('$do_date', 'yyyy-MM-01')
  and dt <= '$do_date'
group by device_id;
"

hive -e "$sql"