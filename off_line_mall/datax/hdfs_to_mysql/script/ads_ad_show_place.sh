#!/bin/bash
source /etc/profile
JSON=/data/lagoudw/script
if [ -n "$1" ] ;then
    do_date=$1
else
    do_date=`date -d "-1 day" +%F`
fi
python2 $DATAX_HOME/bin/datax.py -p "-Ddo_date=$do_date" $JSON/advertisement/ads_ad_show_place.json