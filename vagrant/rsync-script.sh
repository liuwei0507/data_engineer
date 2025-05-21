#!/bin/bash
#脚本编写大致步骤
# 需求:循环复制文件到集群所有节点的相同目录下
# 使用方式：脚本+需要复制的文件名称
#1.获取传入脚本的参数，参数个数
paramnum=$#
if((paramnum==0));
then
  echo no args;
exit;
fi

#2.获取到文件名称
p1=$1

file_name=`basename $p1`
echo fname=${file_name}

#3.获取到文件的绝对路径，获取文件的目录信息
dir_name=`cd -P $(dirname $p1);pwd`
#dir_name=`dirname $p1`
echo dirname=${dir_name}

#4.获取当前用户信息
user=`whoami`
#5 执行rsync命令，循环执行,要把数据发送到其他节点
for((host=121;host<124;host++));
do
  echo --------------target hostname=linux$host----------
  rsync -rvl ${dir_name}/${file_name} ${user}@linux${host}:${dir_name}
done

