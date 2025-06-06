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
insert overwrite table dim.dim_trade_product_cat
    partition (dt = '$do_date')
select t1.catid,
       t1.catname,
       t2.catid,
       t2.catname,
       t3.catid,
       t3.catname
from
    -- 商品三级分类数据
    (select catid, catname, parentid
     from ods.ods_trade_product_category
     where level = 3
       and dt = '$do_date') t3

        left join
    -- 商品二级分类数据
        (select catid, catname, parentid
         from ods.ods_trade_product_category
         where level = 2
           and dt = '$do_date') t2
    on t3.parentid = t2.catid

        left join
    -- 商品一级分类数据
        (select catid, catname, parentid
         from ods.ods_trade_product_category
         where level = 1
           and dt = '$do_date') t1
    on t2.parentid = t1.catid;
"
hive -e "$sql"