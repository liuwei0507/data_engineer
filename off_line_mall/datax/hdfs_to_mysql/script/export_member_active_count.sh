#!/bin/bash
JSON=/data/lagoudw/script/member_active
source /etc/profile
if [ -n "$1" ] ;then
    do_date=$1
else
    do_date=`date -d "-1 day" +%F`
fi
python2 $DATAX_HOME/bin/datax.py -p "-Ddo_date=$do_date" $JSON/export_member_active_count.json