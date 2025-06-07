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
insert overwrite table dws.dws_trade_orders
partition(dt='$do_date')
select t1.orderid                      as orderid,
       t3.categoryid                   as cat_3rd_id,
       t3.shopid                       as shopid,
       t1.paymethod                    as paymethod,
       t2.productnum                   as productsnum,
       t2.productnum * t2.productprice as pay_money,
       t1.paytime                      as paytime
from (select orderid,
             paymethod,
             paytime
      from dwd.dwd_trade_orders
      where dt = '$do_date'
        and status >= 0) t1
         left join
     (select orderid, productid, productnum, productprice
      from ods.ods_trade_order_product
      where dt = '$do_date') t2
     on t1.orderid = t2.orderid
         left join
     (select productid, shopid, categoryid
      from dim.dim_trade_product_info
      where start_dt <= '$do_date'
        and end_dt >= '$do_date') T3
     on t2.productid = t3.productid;

insert overwrite table dws.dws_trade_orders_w
    partition (dt = '$do_date')
select t1.orderid,
       t1.cat_3rd_id,
       t2.thirdname,
       t2.secondname,
       t2.firstname,
       t1.shopid,
       t3.shopname,
       t3.regionname,
       t3.cityname,
       t1.paymethod,
       t1.productsnum,
       t1.paymoney,
       t1.paytime
from (select orderid,
             cat_3rd_id,
             shopid,
             paymethod,
             productsnum,
             paymoney,
             paytime
      from dws.dws_trade_orders
      where dt = '$do_date') T1
         join
     (select thirdid,
             thirdname,
             secondid,
             secondname,
             firstid,
             firstname
      from dim.dim_trade_product_cat
      where dt = '$do_date') T2
     on T1.cat_3rd_id = T2.thirdid
         join
     (select shopid, shopname, regionname, cityname
      from dim.dim_trade_shops_org
      where dt = '$do_date') T3
     on T1.shopid = T3.shopid
"
hive -e "$sql"