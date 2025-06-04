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
insert overwrite table ads.ads_ad_show
    partition (dt = '$do_date')
select count(1),
       count(distinct uid),
       count(distinct device_id),
       ad_action,
       hour
from dwd.dwd_ad
where dt = '$do_date'
group by ad_action, hour
"
hive -e "$sql"