package com.sxt.transformer.mr.mAu;

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

public class ActiveUserReducer extends Reducer<StatsUserDimension, TimeOutputValue, StatsUserDimension, MapWritableValue> {


    private MapWritableValue mapWritableValue = new MapWritableValue();

    private Set unique = new HashSet<String>();


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
        KpiType kpiType = KpiType.valueOfName(kpiName);
        mapWritableValue.setKpi(kpiType);

        context.write(key, mapWritableValue);
    }
}
