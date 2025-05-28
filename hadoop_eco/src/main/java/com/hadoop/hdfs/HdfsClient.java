package com.hadoop.hdfs;

import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.fs.*;
import org.apache.hadoop.io.IOUtils;
import org.apache.hadoop.util.Progressable;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import java.io.*;
import java.net.URI;
import java.net.URISyntaxException;

public class HdfsClient {
    FileSystem fs = null;
    Configuration configuration = null;

    @Before
    public void init() throws IOException, URISyntaxException, InterruptedException {
        configuration = new Configuration();
        configuration.set("fs.defaultFS", "hdfs://linux121:9000");
//        configuration.set("dfs.replication", "2");
        //2 根据configuration获取fileSystem
        fs = FileSystem.get(new URI("hdfs://linux121:9000"), configuration, "root");
//        fs = FileSystem.get(configuration);
    }

    @After
    public void destroy() throws IOException {
        fs.close();
    }

    @Test
    public void testMkdir() throws URISyntaxException, IOException, InterruptedException {
        //1 获取Hadoop集群的configuration对象
//        Configuration configuration = new Configuration();
//        configuration.set("fs.defaultFS", "hdfs://linux121:9000");
        //2 根据configuration获取fileSystem
//        FileSystem fs = FileSystem.get(new URI("hdfs://linux121:9000"), configuration, "root");
//        FileSystem fs = FileSystem.get(configuration);
        //3 使用fileSystem创建一个测试目录
        fs.mkdirs(new Path("/api_test2"));

        //4 释放filesystem对象（类似与数据库连接）
//        fs.close();
    }

    /**
     * 上传文件
     *
     * @throws IOException
     */
    @Test
    public void copyFromLocalToHdfs() throws IOException {
        fs.copyFromLocalFile(new Path("/Users/liuwei/Documents/code/data_engineer/vagrant/Vagrantfile"), new Path("/lagou/bigdata/Vagrantfile_1"));
    }

    /**
     * 下载文件
     *
     * @throws IOException
     */
    @Test
    public void copyFromToLocalFromHdfs() throws IOException {
        fs.copyToLocalFile(false, new Path("/lagou/bigdata/Vagrantfile"), new Path("/Users/liuwei/Documents/code/data_engineer/vagrant/Vagrantfile_Bak"));
    }

    /**
     * 删除文件夹/文件
     *
     * @throws IOException
     */
    @Test
    public void testDelete() throws IOException {
        fs.delete(new Path("/api_test1"), true);
    }

    /**
     * 查看文件名称、权限、长度、块信息
     *
     * @throws IOException
     */
    @Test
    public void testListFiles() throws IOException {
        RemoteIterator<LocatedFileStatus> listFiles = fs.listFiles(new Path("/lagou/"), true);
        while (listFiles.hasNext()) {
            LocatedFileStatus fileStatus = listFiles.next();

            System.out.println(fileStatus.getPath().getName());
            System.out.println(fileStatus.getLen());
            System.out.println(fileStatus.getPermission());
            System.out.println(fileStatus.getGroup());

            BlockLocation[] blockLocations = fileStatus.getBlockLocations();
            for (BlockLocation blockLocation : blockLocations) {
                String[] hosts = blockLocation.getHosts();
                for (String host : hosts) {
                    System.out.println(host);
                }
            }
        }
    }

    /**
     * 判断是文件还是文件夹
     *
     * @throws IOException
     */
    @Test
    public void listStatus() throws IOException {
                   /*
            判断是否是文件夹
             */
        FileStatus[] fileStatuses = fs.listStatus(new Path("/"));
        for (FileStatus status : fileStatuses) {
            if (status.isFile()) {
                System.out.println("file:" + status.getPath().getName());
            } else {
                System.out.println("folder:" + status.getPath().getName());
            }
        }
    }

    /**
     * 文件流上传
     */
    @Test
    public void testFileStreamToHdfs() throws IOException {
        FileInputStream fis = new FileInputStream(new File("/Users/liuwei/Documents/code/data_engineer/vagrant/Vagrantfile"));

        FSDataOutputStream fos = fs.create(new Path("/lagou/bigdata/Vagrant_2"));

        IOUtils.copyBytes(fis, fos, configuration);

        //关闭资源
        IOUtils.closeStream(fos);
        IOUtils.closeStream(fis);
    }

    /**
     * 文件流读取
     */
    @Test
    public void getFileFromHDFS() throws IOException, InterruptedException {
        // 2 获取输入流
        FSDataInputStream fis = fs.open(new Path("/lagou/bigdata/Vagrant_2"));
        // 3 获取输出流
        FileOutputStream fos = new FileOutputStream("/Users/liuwei/Documents/code/data_engineer/vagrant/Vagrantfile_bak2");
        // 4 流的对拷
        IOUtils.copyBytes(fis, fos, configuration);
        // 5 关闭资源
        IOUtils.closeStream(fos);
        IOUtils.closeStream(fis);
    }

    /**
     * seek 定位读取
     */
    @Test
    public void readFileSeek2() throws IOException {
        FSDataInputStream fis = null;
        try {
            fis = fs.open(new Path("/lagou/bigdata/Vagrant_2"));
            IOUtils.copyBytes(fis, System.out, 4096, false);
            fis.seek(0);// 从头开始读取
            IOUtils.copyBytes(fis, System.out, 4096, false);
        } finally {
            IOUtils.closeStream(fis);
        }


    }

    @Test
    public void testUploadPacket() throws IOException { //1 准备读取本地文件的输入流
        final FileInputStream in = new FileInputStream(new
                File("/Users/liuwei/Documents/code/data_engineer/vagrant/Vagrantfile"));
        //2 准备好写出数据到hdfs的输出流
        final FSDataOutputStream out = fs.create(new Path("/lagou/bigdata/Vagrant_3"), new
                Progressable() {
                    public void progress() { //这个progress方法就是每传输64KB(packet)就会执行一次，
                        System.out.println("&");
                    }
                });
        //3 实现流拷贝
        IOUtils.copyBytes(in, out, configuration); //默认关闭流选项是true，所以会自动关闭
        //4 关流 可以再次关闭也可以不关了
    }

}
