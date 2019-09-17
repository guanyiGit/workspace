package com.sxt.transformer.mr.mNu;

import com.sxt.common.DateEnum;
import com.sxt.common.EventLogConstants;
import com.sxt.common.KpiType;
import com.sxt.transformer.model.dim.StatsCommonDimension;
import com.sxt.transformer.model.dim.StatsUserDimension;
import com.sxt.transformer.model.dim.base.BrowserDimension;
import com.sxt.transformer.model.dim.base.DateDimension;
import com.sxt.transformer.model.dim.base.KpiDimension;
import com.sxt.transformer.model.dim.base.PlatformDimension;
import com.sxt.transformer.model.value.map.TimeOutputValue;
import org.apache.hadoop.hbase.CellUtil;
import org.apache.hadoop.hbase.client.Result;
import org.apache.hadoop.hbase.io.ImmutableBytesWritable;
import org.apache.hadoop.hbase.mapreduce.TableMapper;
import org.apache.hadoop.hbase.util.Bytes;

import java.io.IOException;
import java.util.List;

public class NewInstallUserMapper extends TableMapper<StatsUserDimension, TimeOutputValue> {

    private byte[] family = Bytes.toBytes(EventLogConstants.EVENT_LOGS_FAMILY_NAME);
    private StatsUserDimension statsUserDimension = new StatsUserDimension();
    private TimeOutputValue timeOutputValue = new TimeOutputValue();
    private KpiDimension newInstallUserKpi = new KpiDimension(KpiType.NEW_INSTALL_USER.name);
    private KpiDimension browserNewInstallUserKpi = new KpiDimension(KpiType.BROWSER_NEW_INSTALL_USER.name);

    @Override
    protected void map(ImmutableBytesWritable key, Result value, Context context) throws IOException, InterruptedException {
        String date = Bytes.toString(CellUtil.cloneValue(value.getColumnLatestCell(family, Bytes.toBytes(EventLogConstants.LOG_COLUMN_NAME_SERVER_TIME))));
        long time = Long.valueOf(date);
        DateDimension dateDimension = DateDimension.buildDate(time, DateEnum.DAY);

        String platformName = Bytes.toString(CellUtil.cloneValue(value.getColumnLatestCell(family, Bytes.toBytes(EventLogConstants.LOG_COLUMN_NAME_PLATFORM))));
        List<PlatformDimension> platformDimensions = PlatformDimension.buildList(platformName);

        String browserName = Bytes.toString(CellUtil.cloneValue(value.getColumnLatestCell(family, Bytes.toBytes(EventLogConstants.LOG_COLUMN_NAME_BROWSER_NAME))));
        String browserVersion = Bytes.toString(CellUtil.cloneValue(value.getColumnLatestCell(family, Bytes.toBytes(EventLogConstants.LOG_COLUMN_NAME_BROWSER_VERSION))));
        List<BrowserDimension> browserDimensions = BrowserDimension.buildList(browserName, browserVersion);

        String uuid = Bytes.toString(CellUtil.cloneValue(value.getColumnLatestCell(family, Bytes.toBytes(EventLogConstants.LOG_COLUMN_NAME_UUID))));
        timeOutputValue.setId(uuid);
        timeOutputValue.setTime(time);

        StatsCommonDimension statsCommon = statsUserDimension.getStatsCommon();
        statsCommon.setDate(dateDimension);

        BrowserDimension defaultBrowserDimension = new BrowserDimension("", "");
        for (PlatformDimension pd : platformDimensions) {
            statsCommon.setKpi(newInstallUserKpi);
            statsCommon.setPlatform(pd);
            statsUserDimension.setBrowser(defaultBrowserDimension);
            context.write(statsUserDimension, timeOutputValue);
            for (BrowserDimension bd : browserDimensions) {
                statsCommon.setKpi(browserNewInstallUserKpi);
                statsUserDimension.setBrowser(bd);
                context.write(statsUserDimension, timeOutputValue);
            }
        }
    }
}
