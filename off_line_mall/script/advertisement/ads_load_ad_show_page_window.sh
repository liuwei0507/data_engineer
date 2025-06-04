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
insert overwrite table ads.ads_ad_show_place_window
    partition (dt = '$do_date')
select *
from (select hour,
             place,
             product_id,
             cnt,
             row_number() over (partition by hour, place, product_id order by cnt desc) rank
      from ads.ads_ad_show_place
      where dt = '$do_date'
        and ad_action = '0') t
where rank <= 100;
"
hive -e "$sql"