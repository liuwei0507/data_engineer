## 安装

解压
```shell
tar -zxvf apache-flume-1.9.0-bin.tar.gz -C /opt/lagou/servers
mv apache-flume-1.9.0-bin flume-1.9.0
```
添加环境变量
```shell
export FLUME_HOME=/opt/lagou/servers/flume-1.9.0 
export PATH=$PATH:$FLUME_HOME/bin
```
将 $FLUME_HOME/conf 下的 flume-env.sh.template 改名为 flume-env.sh，并 添加 JAVA_HOME的配置
```shell
cd $FLUME_HOME/conf
mv flume-env.sh.template flume-env.sh
vi flume-env.sh
export JAVA_HOME=/opt/lagou/servers/jdk1.8.0_231
```

## 配置（基于自定义的拦截器配置多个file group）
```shell
a1.sources = r1
a1.sinks = k1
a1.channels = c1
# taildir source
a1.sources.r1.type = TAILDIR
a1.sources.r1.positionFile = /data/lagoudw/conf/startlog_position.json
a1.sources.r1.filegroups = f1 f2
a1.sources.r1.filegroups.f1 = /data/lagoudw/logs/start/.*log
a1.sources.r1.headers.f1.logtype = start
a1.sources.r1.filegroups.f2 = /data/lagoudw/logs/event/.*log
a1.sources.r1.headers.f2.logtype = event
# 自定义拦截器
a1.sources.r1.interceptors = i1
a1.sources.r1.interceptors.i1.type = cn.dw.flume.interceptor.LogTypeInterceptor$Builder
# 已经使用了拦截器，修改配置以处理时间戳缺失的情况：
a1.sources.r1.interceptors.i1.preserveExisting = true
# memorychannel
a1.channels.c1.type = memory
a1.channels.c1.capacity = 100000
a1.channels.c1.transactionCapacity = 2000

# hdfs sink
a1.sinks.k1.type = hdfs
a1.sinks.k1.hdfs.path = /user/data/logs/%{logtype}/dt=%{logtime}/
a1.sinks.k1.hdfs.filePrefix = startlog
a1.sinks.k1.hdfs.fileType = DataStream

# 配置文件滚动方式(文件大小32M)
a1.sinks.k1.hdfs.rollSize = 33554432
a1.sinks.k1.hdfs.rollCount = 0
a1.sinks.k1.hdfs.rollInterval = 0
a1.sinks.k1.hdfs.idleTimeout = 0
a1.sinks.k1.hdfs.minBlockReplicas = 1

# 向hdfs上刷新的event的个数
a1.sinks.k1.hdfs.batchSize = 1000

# Bind the source and sink to the channel
a1.sources.r1.channels = c1
```

## 配置JVM参数
缺省情况下 Flume jvm堆最大分配20m，这个值太小，需要调整。
```shell
vi $FLUME_HOME/conf/flume-env.sh
# 添加如下参数
export JAVA_OPTS="-Xms4000m -Xmx4000m -Dcom.sun.management.jmxremote"
# 运行时指定配置文件
flume-ng agent --conf /opt/lagou/servers/flume-1.9/conf --conf-file /data/lagoudw/conf/flume-log2hdfs3.conf -name a1  -Dflume.root.logger=INFO,console
```

## 启动
```shell
# 清理环境
rm -f /data/lagoudw/conf/startlog_position.json
rm -f /data/lagoudw/logs/start/*.log
rm -f /data/lagoudw/logs/event/*.log

# 启动 Agent
flume-ng agent --conf /opt/lagou/servers/flume-1.9/conf --conf-file /data/lagoudw/conf/flume-log2hdfs3.conf -name a1  -Dflume.root.logger=INFO,console

# 拷贝日志
cd /data/lagoudw/logs/source
cp event0802.log ../event/
cp start0802.log ../start/

# 检查HDFS文件
hdfs dfs -ls /user/data/logs/event
hdfs dfs -ls /user/data/logs/start

# 生产环境中用以下方式启动Agent
nohup flume-ng agent --conf /opt/apps/flume-1.9/conf --conf-file /data/lagoudw/conf/flume-log2hdfs3.conf -name a1  
-Dflume.root.logger=INFO,LOGFILE > /dev/null 2>&1 &
```