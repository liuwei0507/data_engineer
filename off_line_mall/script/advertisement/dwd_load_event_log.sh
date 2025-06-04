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
add jar /home/hive/jar/off_line_mall-1.0-SNAPSHOT-jar-with-dependencies.jar;
create temporary function json_array as 'cn.dw.hive.udf.ParseJsonArray';
with tmp_start as (
select
-- split(str, ' ')[7] as line,
   substring(str,length(substring_index(str,' ',7))+1) as line
from ods.ods_log_event
where dt = '$do_date'
)
insert overwrite table dwd.dwd_event_log
  PARTITION (dt='$do_date')
select
    device_id,
    uid,
    app_v,
    os_type,
    event_type,
    language,
    channel,
    area,
    brand,
    get_json_object(k,'$.name') as name,
    get_json_object(k,'$.json') as json,
    get_json_object(k,'$.time') as `time`
from
(
select get_json_object(line, '$.attr.device_id')  as device_id,
       get_json_object(line, '$.attr.uid')        as uid,
       get_json_object(line, '$.attr.app_v')      as app_v,
       get_json_object(line, '$.attr.os_type')    as os_type,
       get_json_object(line, '$.attr.event_type') as event_type,
       get_json_object(line, '$.attr.language')   as language,
       get_json_object(line, '$.attr.channel')    as channel,
       get_json_object(line, '$.attr.area')       as area,
       get_json_object(line, '$.attr.brand')      as brand,
       get_json_object(line,'$.lagou_event') as lagou_event
from tmp_start
) A lateral view explode(json_array(lagou_event)) B as k
;
"
hive -e "$sql"