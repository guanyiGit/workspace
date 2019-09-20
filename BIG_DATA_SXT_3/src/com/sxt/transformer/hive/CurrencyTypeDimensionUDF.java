package com.sxt.transformer.hive;

import com.sxt.transformer.model.dim.base.CurrencyTypeDimension;
import com.sxt.transformer.service.IDimensionConverter;
import com.sxt.transformer.service.impl.DimensionConverterImpl;
import org.apache.hadoop.hive.ql.exec.UDF;
import org.apache.hadoop.io.IntWritable;
import org.apache.hadoop.io.Text;

import java.io.IOException;

/**
 * 货币类型返回id
 */
public class CurrencyTypeDimensionUDF extends UDF {

    private IDimensionConverter converter = new DimensionConverterImpl();

    /**
     * 根据给定的货币类型 返回id
     *
     * @param input
     * @return
     */
    public IntWritable evaluate(Text input) {
        CurrencyTypeDimension dimension = CurrencyTypeDimension.buildDimension(input.toString());
        try {
            int id = this.converter.getDimensionIdByValue(dimension);
            return new IntWritable(id);
        } catch (IOException e) {
            throw new RuntimeException("获取id异常");
        }
    }
}
