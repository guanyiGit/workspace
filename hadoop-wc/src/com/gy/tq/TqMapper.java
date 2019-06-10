package com.gy.tq;

import org.apache.hadoop.io.IntWritable;
import org.apache.hadoop.io.LongWritable;
import org.apache.hadoop.io.Text;
import org.apache.hadoop.mapreduce.Mapper;
import org.apache.hadoop.util.StringUtils;

import java.io.IOException;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;

public class TqMapper extends Mapper<LongWritable, Text, TqEntity, IntWritable> {

    TqEntity mKey = new TqEntity();
    IntWritable mVal = new IntWritable();
    DateTimeFormatter dtf = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");


    @Override
    protected void map(LongWritable key, Text value, Context context) throws IOException, InterruptedException {
        //1950-10-03 12:21:02	27c
        String[] lineTokens = StringUtils.split(value.toString(), '\t');
        LocalDate date = LocalDate.parse(lineTokens[0], dtf);

        mKey.setYear(date.getYear());
        mKey.setMonth(date.getDayOfMonth());
        mKey.setDay(date.getDayOfMonth());
        Integer wd = Integer.valueOf(lineTokens[1].substring(0, lineTokens[1].length() - 1));
        mKey.setWd(wd);

        mVal.set(wd);

        context.write(mKey, mVal);
    }
}
