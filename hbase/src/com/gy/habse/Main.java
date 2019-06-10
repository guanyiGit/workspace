package com.gy.habse;

import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.hbase.*;
import org.apache.hadoop.hbase.client.*;
import org.apache.hadoop.hbase.filter.CompareFilter;
import org.apache.hadoop.hbase.filter.FilterList;
import org.apache.hadoop.hbase.filter.PrefixFilter;
import org.apache.hadoop.hbase.filter.SingleColumnValueFilter;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import java.io.IOException;
import java.io.InterruptedIOException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class Main {

    HBaseAdmin admin;
    HTable table;
    String tName = "phone";

    @Before
    public void befor() throws IOException {
        Configuration conf = new Configuration();
        conf.set("hbase.zookeeper.quorum", "centos15:2181");
        admin = new HBaseAdmin(conf);
        table = new HTable(conf, tName);
    }

    @After
    public void after() throws IOException {
        if (admin != null) {
            admin.close();
        }
        if (table != null) {
            table.close();
        }
    }


    @Test
    public void create() throws IOException {
        if (admin.tableExists(tName)) {
            admin.disableTable(tName);
            admin.deleteTable(tName);
        }
        HTableDescriptor desc = new HTableDescriptor(TableName.valueOf(tName));
        HColumnDescriptor cf = new HColumnDescriptor("cf".getBytes());
//        cf.setMaxVersions(3);
//        cf .setInMemory(true);

        desc.addFamily(cf);
        admin.createTable(desc);
    }

    @Test
    public void put() throws IOException {
        Put put = new Put("222".getBytes());
        put.add("cf".getBytes(), "name".getBytes(), "xiaoming".getBytes());
        put.add("cf".getBytes(), "age".getBytes(), "18".getBytes());
        table.put(put);
        System.err.println("ok");
    }

    @Test
    public void get() throws IOException {
        Get get = new Get("111".getBytes());
        get.addFamily("cf".getBytes());
        get.addColumn("cf".getBytes(), "name".getBytes());
        Result result = table.get(get);
        Cell cell = result.getColumnLatestCell("cf".getBytes(), "name".getBytes());
        System.out.println(new String(CellUtil.cloneRow(cell)));
        System.out.println(new String(CellUtil.cloneFamily(cell)));
        System.out.println(new String(CellUtil.cloneValue(cell)));
    }

    @Test
    public void scan() throws IOException {
        Scan scan = new Scan();
        scan.addFamily("cf".getBytes());
        ResultScanner results = table.getScanner(scan);
        results.forEach(x -> {
            System.out.println(getResult(x, "cf", "name"));
            System.out.println(getResult(x, "cf", "age"));
        });
    }

    private String getDate(String year) {
        return year + String.format("%02d%02d%02d%02d%02d",
                new Object[]{r.nextInt(12) + 1, r.nextInt(31) + 1, r.nextInt(24), r.nextInt(60), r.nextInt(60)});
    }

    private String getDate2(String yearMonthDay) {
        return yearMonthDay
                + String.format("%02d%02d%02d", new Object[]{r.nextInt(24), r.nextInt(60), r.nextInt(60)});
    }

    /**
     * 生成随机的手机号码
     *
     * @param string
     * @return
     */
    private String getPhoneNum(String string) {
        return string + String.format("%08d", r.nextInt(99999999));
    }

    Random r = new Random();
    SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMddHHmmss");

    /**
     * 100个用户 每个用户100条记录
     */
    @Test
    public void addData() throws ParseException, InterruptedIOException, RetriesExhaustedWithDetailsException {
        ArrayList<Put> puts = new ArrayList<>();
        for (int i = 0; i < 100; i++) {
            //用户手机号码
            String num = getPhoneNum("188");
            for (int j = 0; j < 100; j++) {
                //获取对方手机号
                String dNum = getPhoneNum("133");
                //获取通话时间
                String date = getDate("2019");
                //获得通话类型
                String type = r.nextInt(2) + "";
                String rowKey = num + "_" + (Long.MAX_VALUE - sdf.parse(date).getTime());
                Put put = new Put(rowKey.getBytes());
                put.add("cf".getBytes(), "dNum".getBytes(), dNum.getBytes());
                put.add("cf".getBytes(), "date".getBytes(), date.getBytes());
                put.add("cf".getBytes(), "type".getBytes(), type.getBytes());
                puts.add(put);
            }
        }

        table.put(puts);
    }

    /**
     * 获取2月份通话记录 18898411955
     */
    @Test
    public void getPhoneRecord() throws ParseException, IOException {
        Scan scan = new Scan();
        scan.addFamily("cf".getBytes());
        String startRow = "18898411955_" + (Long.MAX_VALUE - sdf.parse("20190301000000").getTime());
        String endRow = "18898411955_" + (Long.MAX_VALUE - sdf.parse("20190201000000").getTime());
        scan.setStartRow(startRow.getBytes());
        scan.setStopRow(endRow.getBytes());

        table.getScanner(scan).forEach(x -> {
            System.out.print(getResult(x, "cf", "dNum"));
            System.out.print("\t");
            System.out.print(getResult(x, "cf", "date"));
            System.out.print("\t");
            System.out.println(getResult(x, "cf", "type"));
        });
    }

    /**
     * 获取用户的主叫
     */
    @Test
    public void getPhoneRecordByType() throws IOException {
        FilterList list = new FilterList(FilterList.Operator.MUST_PASS_ALL);
        PrefixFilter prefixFilter = new PrefixFilter("18898411955".getBytes());

        SingleColumnValueFilter columnValueFilter = new SingleColumnValueFilter(
                "cf".getBytes(),
                "type".getBytes(),
                CompareFilter.CompareOp.EQUAL,
                "1".getBytes());
        list.addFilter(prefixFilter);
        list.addFilter(columnValueFilter);

        Scan scan = new Scan();
        scan.setFilter(list);
        table.getScanner(scan).forEach(x -> {
            System.out.print(getResult(x, "cf", "dNum"));
            System.out.print("_" + getResult(x, "cf", "date"));
            System.out.println("\t" + getResult(x, "cf", "type"));
        });
    }

    private String getResult(Result result, String cf, String key) {
        return new String(CellUtil.cloneValue(result.getColumnLatestCell(cf.getBytes(), key.getBytes())));
    }

    /**
     * 10个用户 每天100个电话 20190104
     */
    @Test
    public void addDataBrot() throws ParseException, InterruptedIOException, RetriesExhaustedWithDetailsException {
        List<Put> puts = new ArrayList<>();
        for (int i = 0; i < 10; i++) {
            //用户手机号码
            String num = getPhoneNum("188");
            String rowKey = num + "_" + (Long.MAX_VALUE - sdf.parse("20190104000000").getTime());
            ProtPhone.dayPhoneDetail.Builder dayPhone = ProtPhone.dayPhoneDetail.newBuilder();
            for (int j = 0; j < 100; j++) {
                //获取对方手机号
                String dNum = getPhoneNum("133");
                //获取通话时间
                String date = getDate2("20190104");
                //获得通话类型
                String type = r.nextInt(2) + "";
                ProtPhone.phoneDetail.Builder phoneDetail = ProtPhone.phoneDetail.newBuilder();
                phoneDetail.setNum(num);
                phoneDetail.setDnum(dNum);
                phoneDetail.setDate(date);
                phoneDetail.setType(type);

                dayPhone.addDayPhoneDetail(phoneDetail);
            }
            Put put = new Put(rowKey.getBytes());
            put.add("cf".getBytes(), "dayPhone".getBytes(), dayPhone.build().toByteArray());

            puts.add(put);
        }

        table.put(puts);
    }

    /**
     * 获取某一个用户某天100条通话记录 18890062128
     */
    @Test
    public void getByProt() throws ParseException, IOException {
        String rowKey = "18890062128_" + (Long.MAX_VALUE - sdf.parse("20190104000000").getTime());
        Get get = new Get(rowKey.getBytes());
        Result result = table.get(get);
        Cell cell = result.getColumnLatestCell("cf".getBytes(), "dayPhone".getBytes());
        byte[] bytes = CellUtil.cloneValue(cell);

        ProtPhone.dayPhoneDetail dayPhoneDetail = ProtPhone.dayPhoneDetail.parseFrom(bytes);
        dayPhoneDetail.getDayPhoneDetailList().forEach(System.out::println);
    }

}
