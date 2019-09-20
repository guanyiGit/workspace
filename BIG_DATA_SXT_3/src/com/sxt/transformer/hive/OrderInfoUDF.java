package com.sxt.transformer.hive;


import com.sxt.transformer.model.dim.base.OrderInfoDimension;
import com.sxt.transformer.service.IDimensionConverter;
import com.sxt.transformer.service.impl.DimensionConverterImpl;
import org.apache.hadoop.hive.ql.exec.UDF;
import org.apache.hadoop.io.IntWritable;
import org.apache.hadoop.io.Text;

import java.io.IOException;

/**
 * 操作订单dimension 相关的udf
 *
 * @author root
 */
public class OrderInfoUDF extends UDF {

    private IDimensionConverter converter = new DimensionConverterImpl();

    /**
     * 根据给定的订单类型 返回id
     *
     * @param input
     * @return
     */
    public IntWritable evaluate(Text input) {
        OrderInfoDimension dimension = OrderInfoDimension.buildDimension(input.toString());
        try {
            int id = this.converter.getDimensionIdByValue(dimension);
            return new IntWritable(id);
        } catch (IOException e) {
            throw new RuntimeException("获取id异常");
        }
    }
}
