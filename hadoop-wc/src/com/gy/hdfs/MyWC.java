package com.gy.hdfs;

import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.fs.*;
import org.apache.hadoop.io.IOUtils;
import org.junit.Before;
import org.junit.Test;

import java.io.BufferedInputStream;
import java.io.FileInputStream;
import java.io.IOException;

public class MyWC {

    Configuration conf;
    FileSystem fs;

    @Before
    public void befor() throws IOException {
        //读取配置文件
        conf = new Configuration(true);
        fs = FileSystem.get(conf);
    }


    @Test
    public void mkdirs() throws IOException {
        Path path = new Path("/usr");
        if (fs.exists(path))
            fs.delete(path, true);
        boolean bl = fs.mkdirs(path);
        System.out.println(bl);
    }

    @Test
    public void upload() throws IOException {
        Path path = new Path("/usr/test.txt");
        FSDataOutputStream out = fs.create(path);
        BufferedInputStream in = new BufferedInputStream(new FileInputStream("D:\\disk\\workspace\\hadoop-wc\\conf\\hdfs-site.xml"));

        IOUtils.copyBytes(in, out, conf, true);

        System.out.println("ok");
    }

    @Test
    public void blaks() throws IOException {
        Path f = new Path("/usr/test.txt");
        FileStatus file = fs.getFileLinkStatus(f);
        BlockLocation[] blks = fs.getFileBlockLocations(file, 0, file.getLen());
        for (BlockLocation blk : blks) {
            System.err.println(blk);
        }
    }
}
