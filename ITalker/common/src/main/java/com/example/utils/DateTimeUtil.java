package com.example.utils;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.Locale;

/**
 * 时间的工具类
 * Created by Administrator on 2017/9/3.
 */

public class DateTimeUtil {

    private static final SimpleDateFormat FORMAT = new SimpleDateFormat("yy-MM-dd", Locale.ENGLISH);
    private static final SimpleDateFormat TODAYFPRMAT = new SimpleDateFormat("HH-mm",Locale.ENGLISH);

    /**
     * 读取一个简单的时间字符串
     * @param date 时间
     * @return String
     */
    public static  String getSimpleDate(Date date){
        Date date1 = new Date();
        date1.setHours(0);
        date1.setMinutes(0);
        date1.setSeconds(0);
        if(date.after(date1)){
            // 如果是今天的日期
            return TODAYFPRMAT.format(date);
        }else {
            // 不是今天的发送的内容就显示日期
            return FORMAT.format(date);
        }
    }

}
