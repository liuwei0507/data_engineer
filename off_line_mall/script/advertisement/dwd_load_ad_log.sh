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
insert overwrite table dwd.dwd_ad
    PARTITION (dt = '$do_date')
select device_id,
       uid,
       app_v,
       os_type,
       event_type,
       language,
       channel,
       area,
       brand,
       report_time,
       get_json_object(event_json, '$.duration'),
       get_json_object(event_json, '$.ad_action'),
       get_json_object(event_json, '$.shop_id'),
       get_json_object(event_json, '$.ad_type'),
       get_json_object(event_json, '$.show_style'),
       get_json_object(event_json, '$.product_id'),
       get_json_object(event_json, '$.place'),
       get_json_object(event_json, '$.sort'),

       from_unixtime(ceil(report_time / 1000), 'HH')
from dwd.dwd_event_log
where dt = '$do_date'
  and name = 'ad';
"
hive -e "$sql"