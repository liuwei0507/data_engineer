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
insert overwrite table ads.ads_member_retention_count
    partition (dt='$do_date')
select add_date,retention_date,
    count(*) retention_count
from dws.dws_member_retention_day
where dt='$do_date'
group by add_date, retention_date;

insert overwrite table ads.ads_member_retention_rate
    partition (dt='$do_date')
select
    t1.add_date,
    t1.retention_day,
    t1.retention_count,
    t2.cnt,
    t1.retention_count/t2.cnt*100
    from ads.ads_member_retention_count t1 join
        ads.ads_new_member_cnt t2 on t1.dt=t2.dt
    where t1.dt='$do_date'
"
hive -e "$sql"