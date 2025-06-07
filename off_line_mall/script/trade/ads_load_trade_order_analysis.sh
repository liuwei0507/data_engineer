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
with mid_orders as (
    select regionname,
           cityname,
           firstname  category1,
           secondname category2,
           count(distinct orderid) as totalcount,
           sum(productsnum) as total_productnum,
           sum(paymoney) as totalmoney
    from dws.dws_trade_orders_w
    where dt='$do_date'
    group by regionname, cityname, firstname, secondname
)
insert overwrite table ads.ads_trade_order_analysis
partition(dt='$do_date')
select '全国' as areatype,
       ''     as regionname,
       ''     as cityname,
       ''     as categorytype,
       ''     as category1,
       ''     as category2,
       sum(totalcount),
       sum(total_productnum),
       sum(totalmoney)
from mid_orders
union all
select '全国' as areatype,
       ''     as regionname,
       ''     as cityname,
       '一级' as categorytype,
       category1,
       ''     as category2,
       sum(totalcount),
       sum(total_productnum),
       sum(totalmoney)
from mid_orders
group by category1
union all
select '全国' as areatype,
       ''     as regionname,
       ''     as cityname,
       '二级' as categorytype,
       ''     as category1,
       category2,
       sum(totalcount),
       sum(total_productnum),
       sum(totalmoney)
from mid_orders
group by category2
union all
select
    '大区' as areatype, regionname,
    '' as cityname,
    '' as categorytype, '' as category1,
    '' as category2,
    sum(totalcount),
    sum(total_productnum),
    sum(totalmoney)
from mid_orders
group by regionname
union all
select '大区' as areatype,
       regionname,
       ''     as cityname,
       '一级' as categorytype,
       category1,
       ''     as category2,
       sum(totalcount),
       sum(total_productnum),
       sum(totalmoney)
from mid_orders
group by regionname, category1
union all
select '大区' as areatype,
       regionname,
       ''     as cityname,
       '二级' as categorytype,
       ''     as category1,
       category2,
       sum(totalcount),
       sum(total_productnum),
       sum(totalmoney)
from mid_orders
group by regionname, category2
union all
select '城市' as areatype,
       ''     as regionname,
       cityname,
       ''     as categorytype,
       ''     as category1,
       ''     as category2,
       sum(totalcount),
       sum(total_productnum),
       sum(totalmoney)
from mid_orders
group by cityname
union all
select '城市' as areatype,
       ''     as regionname,
       cityname,
       '一级' as categorytype,
       category1,
       ''     as category2,
       sum(totalcount),
       sum(total_productnum),
       sum(totalmoney)
from mid_orders
group by cityname, category1
union all
select '城市' as areatype,
       ''     as regionname,
       cityname,
       '二级' as categorytype,
       ''     as category1,
       category2,
       sum(totalcount),
       sum(total_productnum),
       sum(totalmoney)
from mid_orders
group by cityname, category2;
"
hive -e "$sql"