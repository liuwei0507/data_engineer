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
with tmp as(
    select split(str, ' ')[7] line
    from ods.ods_start_log
    where dt='$do_date'
)
insert overwrite table dwd.dwd_start_log
partition(dt='$do_date')
select get_json_object(line, '$.attr.device_id'),
       get_json_object(line, '$.attr.area'),
       get_json_object(line, '$.attr.uid'),
       get_json_object(line, '$.attr.app_v'),
       get_json_object(line, '$.attr.event_type'),
       get_json_object(line, '$.attr.os_type'),
       get_json_object(line, '$.attr.channel'),
       get_json_object(line, '$.attr.language'),
       get_json_object(line, '$.attr.brand'),
       get_json_object(line, '$.app_active.json.entry'),
       get_json_object(line, '$.app_active.json.action'),
       get_json_object(line, '$.app_active.json.error_code')
from tmp;
"
hive -e "$sql"