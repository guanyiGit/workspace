package com.gy.tq;

import org.apache.hadoop.io.IntWritable;
import org.apache.hadoop.util.StringUtils;
import org.junit.Test;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;

public class TestMain {

    TqEntity mKey = new TqEntity();
    IntWritable mVal = new IntWritable();
    DateTimeFormatter dtf = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");

    @Test
    public void test1() {
        String value = "1950-10-02 12:21:02\t41c";
        String[] lineTokens = StringUtils.split(value.toString(), '\t');


        LocalDate date = LocalDate.parse(lineTokens[0], dtf);

        mKey.setYear(date.getYear());
        mKey.setMonth(date.getDayOfMonth());
        mKey.setDay(date.getDayOfMonth());
        Integer wd = Integer.valueOf(lineTokens[1].substring(0, lineTokens[1].length() - 1));
        mKey.setWd(wd);

        mVal.set(wd);
    }
}
