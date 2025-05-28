package com.hadoop.hdfs.exapmle.log.collect;

import com.hadoop.hdfs.common.Constant;
import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.fs.FileSystem;
import org.apache.hadoop.fs.Path;

import java.io.File;
import java.io.FilenameFilter;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Properties;
import java.util.TimerTask;

import static com.hadoop.hdfs.common.PropTool2.getProp;

public class LogCollectorTask extends TimerTask {
    @Override
    public void run() {
        // 采集的业务逻辑
        Properties properties = getProp();
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
        String todayStr = sdf.format(new Date());
        //1 扫描指定目录，找到待上传的文件，原始日志目录
        File logsDir = new File(properties.getProperty(Constant.LOG_DIR));
        File[] uploadFiles = logsDir.listFiles(new FilenameFilter() {
            @Override
            public boolean accept(File dir, String name) {
                return name.startsWith(properties.getProperty(Constant.LOG_PREFIX));
            }
        });

        // 2 把待上传的文件转移到临时目录
        // 判断临时目录是否存在
        File tmpfile = new File(properties.getProperty(Constant.LOG_TMP_FOLDER));
        if (!tmpfile.exists()) {
            tmpfile.mkdirs();
        }
        System.out.println("file count:" + uploadFiles.length);
        for (File file : uploadFiles) {
            boolean result = file.renameTo(new File(tmpfile.getPath() + "/" + file.getName()));
            System.out.println(result);
        }

        //3 使用hdfs api上传日志文件到指定目录
        Configuration configuration = new Configuration();
        configuration.set("fs.defaultFS", "hdfs://linux121:9000");
        FileSystem fs = null;
        try {
            fs = FileSystem.get(configuration);
            // 判断hdfs的目标路径是否存在，备份目录是否存在
            Path path = new Path(properties.getProperty(Constant.HDFS_TARGET_FOLDER) + todayStr);
            if (!fs.exists(path)) {
                fs.mkdirs(path);
            }
            Path backupFolder = new Path(properties.getProperty(Constant.BAK_FOLDER) + todayStr);
            if (!fs.exists(backupFolder)) {
                fs.mkdirs(backupFolder);
            }
            File[] files = tmpfile.listFiles();
            for (File file : files) {
                //按照日期分类存放
                fs.copyFromLocalFile(new Path(file.getPath()), new Path("/collect_log/" + todayStr + "/" + file.getName()));
                //4 上传后的文件转移到备份目录
                file.renameTo(new File("/log_bak/" + todayStr + "/" + file.getName()));
            }
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            try {
                fs.close();
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        }
    }
}
