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
with tmp as (
    select
        sum(case when ad_action='0' then cnt end ) show_cnt,
        sum(case when ad_action='1' then cnt end ) click_cnt,
        sum(case when ad_action='2' then cnt end ) buy_cnt,
        hour
    from ads.ads_ad_show
    where dt='$do_date'
    group by hour
)
insert overwrite table ads.ads_ad_show_rate
    partition (dt='$do_date')
select
    hour,
    click_cnt / show_cnt as click_rate,
    buy_cnt / click_cnt as buy_rate
from tmp;
"
hive -e "$sql"