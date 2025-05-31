#!/bin/bash
APP=ODS
hive=/opt/lagou/servers/hive-2.3.7/bin/hive

# 可以输入日期;如果未输入日期取昨天的时间
if [ -n "$1" ]
then
  do_date=$1
else
  do_date=`date -d "-1 day" +%F`
fi

# 定义要执行的SQL
sql="
alter table "$APP".ods_start_log add partition(dt='$do_date');
"

$hive -e "$sql"