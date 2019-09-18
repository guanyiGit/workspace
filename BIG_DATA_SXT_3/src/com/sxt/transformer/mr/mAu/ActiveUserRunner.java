package com.sxt.transformer.mr.mAu;

import com.sxt.common.EventLogConstants;
import com.sxt.common.GlobalConstants;
import com.sxt.transformer.model.dim.StatsUserDimension;
import com.sxt.transformer.model.value.map.TimeOutputValue;
import com.sxt.transformer.model.value.reduce.MapWritableValue;
import com.sxt.transformer.mr.TransformerOutputFormat;
import com.sxt.util.TimeUtil;
import org.apache.commons.lang.StringUtils;
import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.hbase.HBaseConfiguration;
import org.apache.hadoop.hbase.client.Scan;
import org.apache.hadoop.hbase.filter.CompareFilter;
import org.apache.hadoop.hbase.filter.FilterList;
import org.apache.hadoop.hbase.filter.MultipleColumnPrefixFilter;
import org.apache.hadoop.hbase.filter.SingleColumnValueFilter;
import org.apache.hadoop.hbase.mapreduce.TableMapReduceUtil;
import org.apache.hadoop.hbase.util.Bytes;
import org.apache.hadoop.mapreduce.Job;
import org.apache.hadoop.util.Tool;
import org.apache.hadoop.util.ToolRunner;

import java.util.Arrays;
import java.util.List;

public class ActiveUserRunner implements Tool {

    Configuration conf = null;

    public static void main(String[] args) {
        try {
            ToolRunner.run(new Configuration(), new ActiveUserRunner(), args);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public int run(String[] args) throws Exception {
        Configuration conf = getConf();
        Job job = Job.getInstance(conf, "step2");
        job.setJarByClass(ActiveUserRunner.class);

        processArgs(conf, args);

        TableMapReduceUtil.initTableMapperJob(
                getScans(conf),
                ActiveUserMapper.class,
                StatsUserDimension.class,
                TimeOutputValue.class,
                job,
                false);

        job.setReducerClass(ActiveUserReducer.class);

        job.setOutputKeyClass(StatsUserDimension.class);
        job.setOutputValueClass(MapWritableValue.class);
        job.setOutputFormatClass(TransformerOutputFormat.class);

        return job.waitForCompletion(true) ? 0 : 1;
    }

    private void processArgs(Configuration conf, String[] args) {
        String date = null;
        for (int i = 0; i < args.length; i++) {
            if ("-d".equals(args[i])) {
                if (i + 1 < args.length) {
                    date = args[++i];
                    break;
                }
            }
        }

        // 要求date格式为: yyyy-MM-dd
        if (StringUtils.isBlank(date) || !TimeUtil.isValidateRunningDate(date)) {
            // date是一个无效时间数据
            date = TimeUtil.getYesterday(); // 默认时间是昨天
        }
        conf.set(GlobalConstants.RUNNING_DATE_PARAMES, date);
    }

    private List<Scan> getScans(Configuration conf) {
        String date = conf.get(GlobalConstants.RUNNING_DATE_PARAMES);
        Scan scan = new Scan();
        long time = TimeUtil.parseString2Long(date);
        String startRow = String.valueOf(time);
        String stopRow = String.valueOf(time + GlobalConstants.DAY_OF_MILLISECONDS);
        scan.setStartRow(Bytes.toBytes(startRow));
        scan.setStopRow(Bytes.toBytes(stopRow));

        FilterList filterList = new FilterList(FilterList.Operator.MUST_PASS_ALL);

        SingleColumnValueFilter filter1 = new SingleColumnValueFilter(
                Bytes.toBytes(EventLogConstants.EVENT_LOGS_FAMILY_NAME),
                Bytes.toBytes(EventLogConstants.LOG_COLUMN_NAME_EVENT_NAME),
                CompareFilter.CompareOp.EQUAL,
                Bytes.toBytes(EventLogConstants.EventEnum.PAGEVIEW.alias)
        );

        MultipleColumnPrefixFilter filter2 = new MultipleColumnPrefixFilter(new byte[][]{
                Bytes.toBytes(EventLogConstants.LOG_COLUMN_NAME_CLIENT_TIME),
                Bytes.toBytes(EventLogConstants.LOG_COLUMN_NAME_SERVER_TIME),
                Bytes.toBytes(EventLogConstants.LOG_COLUMN_NAME_PLATFORM),
                Bytes.toBytes(EventLogConstants.LOG_COLUMN_NAME_BROWSER_NAME),
                Bytes.toBytes(EventLogConstants.LOG_COLUMN_NAME_BROWSER_VERSION),
                Bytes.toBytes(EventLogConstants.LOG_COLUMN_NAME_UUID)
        });

        filterList.addFilter(filter1);
        filterList.addFilter(filter2);
        scan.setFilter(filterList);

        scan.setAttribute(Scan.SCAN_ATTRIBUTES_TABLE_NAME, Bytes.toBytes(EventLogConstants.HBASE_NAME_EVENT_LOGS));

        return Arrays.asList(scan);
    }


    @Override
    public void setConf(Configuration conf) {
        conf.set("fs.defaultFS", "hdfs://centos98:9000");
        conf.set("hbase.zookeeper.quorum", "centos98:2181");
        conf.set("dfs.client.use.datanode.hostname", "true");

        conf.addResource("output-collector.xml");
        conf.addResource("query-mapping.xml");
        conf.addResource("transformer-env.xml");

        this.conf = HBaseConfiguration.create(conf);
    }

    @Override
    public Configuration getConf() {
        return conf;
    }
}
