package com.sxt.transformer.mr.mNu;

import com.sxt.common.KpiType;
import com.sxt.transformer.model.dim.StatsUserDimension;
import com.sxt.transformer.model.value.map.TimeOutputValue;
import com.sxt.transformer.model.value.reduce.MapWritableValue;
import org.apache.hadoop.io.IntWritable;
import org.apache.hadoop.io.MapWritable;
import org.apache.hadoop.mapreduce.Reducer;

import java.io.IOException;
import java.util.HashSet;
import java.util.Set;

public class NewInstallUserReducer extends Reducer<StatsUserDimension, TimeOutputValue, StatsUserDimension, MapWritableValue> {

    private MapWritableValue mapWritableValue = new MapWritableValue();

    private Set<String> unique = new HashSet<>();

    @Override
    protected void reduce(StatsUserDimension key, Iterable<TimeOutputValue> values, Context context) throws IOException, InterruptedException {
        unique.clear();
        for (TimeOutputValue timeOutputValue : values) {
            unique.add(timeOutputValue.getId());
        }

        MapWritable mapWritable = new MapWritable();
        mapWritable.put(new IntWritable(-1), new IntWritable(unique.size()));
        mapWritableValue.setValue(mapWritable);

        String kpiName = key.getStatsCommon().getKpi().getKpiName();

        if (KpiType.NEW_INSTALL_USER.name.equals(kpiName)) {
            mapWritableValue.setKpi(KpiType.NEW_INSTALL_USER);
        } else if (KpiType.BROWSER_NEW_INSTALL_USER.name.equals(kpiName)) {
            mapWritableValue.setKpi(KpiType.NEW_INSTALL_USER);
        }
        context.write(key, mapWritableValue);
    }
}
