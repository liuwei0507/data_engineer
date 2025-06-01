#!/bin/bash
source /etc/profile
if [ -n "$1" ] ;then
    do_date=$1
else
    do_date=`date -d "-1 day" +%F`
fi
sql="
with tmp as(
select 'day' datelabel, count(*) cnt, dt
  from dws.dws_member_start_day
 where dt='$do_date'
group by dt
union all
select 'week' datelabel, count(*) cnt, dt
  from dws.dws_member_start_week
 where dt='$do_date'
group by dt
union all
select 'month' datelabel, count(*) cnt, dt
  from dws.dws_member_start_month
 where dt='$do_date'
group by dt
)
insert overwrite table ads.ads_member_active_count
partition(dt='$do_date')
select sum(case when datelabel='day' then cnt end) as
day_count,
       sum(case when datelabel='week' then cnt end) as
week_count,
       sum(case when datelabel='month' then cnt end) as
month_count
  from tmp
group by dt;
"
hive -e "$sql"