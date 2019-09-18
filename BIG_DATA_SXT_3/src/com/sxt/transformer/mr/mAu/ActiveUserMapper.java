package com.sxt.transformer.mr.mAu;

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

public class ActiveUserMapper extends TableMapper<StatsUserDimension, TimeOutputValue> {

    private StatsUserDimension statsUserDimension = new StatsUserDimension();
    private TimeOutputValue timeOutputValue = new TimeOutputValue();
    private KpiDimension activeUserkpiDimension = new KpiDimension(KpiType.ACTIVE_USER.name);
    private KpiDimension browserActiveUserkpiDimension = new KpiDimension(KpiType.BROWSER_ACTIVE_USER.name);
    private byte[] family = Bytes.toBytes(EventLogConstants.EVENT_LOGS_FAMILY_NAME);

    @Override
    protected void map(ImmutableBytesWritable key, Result value, Context context) throws IOException, InterruptedException {
        String date = Bytes.toString(CellUtil.cloneValue(value.getColumnLatestCell(family, Bytes.toBytes(EventLogConstants.LOG_COLUMN_NAME_SERVER_TIME))));
        Long time = Long.valueOf(date);
        String browserName = Bytes.toString(CellUtil.cloneValue(value.getColumnLatestCell(family, Bytes.toBytes(EventLogConstants.LOG_COLUMN_NAME_BROWSER_NAME))));
        String browserVersion = Bytes.toString(CellUtil.cloneValue(value.getColumnLatestCell(family, Bytes.toBytes(EventLogConstants.LOG_COLUMN_NAME_BROWSER_VERSION))));
        String uuid = Bytes.toString(CellUtil.cloneValue(value.getColumnLatestCell(family, Bytes.toBytes(EventLogConstants.LOG_COLUMN_NAME_UUID))));
        String platform = Bytes.toString(CellUtil.cloneValue(value.getColumnLatestCell(family, Bytes.toBytes(EventLogConstants.LOG_COLUMN_NAME_PLATFORM))));

        DateDimension dateDimension = DateDimension.buildDate(time, DateEnum.DAY);
        List<BrowserDimension> browserDimensions = BrowserDimension.buildList(browserName, browserVersion);
        List<PlatformDimension> platformDimensions = PlatformDimension.buildList(platform);

        timeOutputValue.setId(uuid);
        timeOutputValue.setTime(time);

        StatsCommonDimension statsCommon = statsUserDimension.getStatsCommon();
        statsCommon.setDate(dateDimension);

        BrowserDimension defaultBrowserDimension = new BrowserDimension("", "");
        for (PlatformDimension platformDimension : platformDimensions) {
            statsCommon.setKpi(activeUserkpiDimension);
            statsCommon.setPlatform(platformDimension);
            statsUserDimension.setBrowser(defaultBrowserDimension);
            context.write(statsUserDimension, timeOutputValue);
            for (BrowserDimension browserDimension : browserDimensions) {
                statsCommon.setKpi(browserActiveUserkpiDimension);
                statsUserDimension.setBrowser(browserDimension);
                context.write(statsUserDimension, timeOutputValue);
            }
        }

    }

}
