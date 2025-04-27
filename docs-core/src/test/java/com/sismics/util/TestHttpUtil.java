package com.sismics.util;

import org.junit.Test;
import static org.junit.Assert.*;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;
import java.util.regex.Pattern;

/**
 * Test class for {@link HttpUtil}.
 */
public class TestHttpUtil {
    // 复制HttpUtil中的日期格式用于解析测试
    private static final SimpleDateFormat TEST_EXPIRES_FORMAT = new SimpleDateFormat("EEE, dd MMM yyyy HH:mm:ss Z", Locale.ENGLISH);
    
    // 匹配格式的正则表达式
    private static final Pattern EXPIRES_PATTERN = Pattern.compile(
        "^[A-Z]{3}, \\d{2} [A-Z]{3} \\d{4} \\d{2}:\\d{2}:\\d{2} [+-]\\d{4}$"
    );

    @Test
    public void testBuildExpiresHeaderFormat() {
        String expiresHeader = HttpUtil.buildExpiresHeader(3600000L);
        assertTrue("Header format mismatch", EXPIRES_PATTERN.matcher(expiresHeader).matches());
    }

//    @Test
//    public void testBuildExpiresHeaderTimeAccuracy() throws ParseException {
//        long futureTime = 3600000L; // 1小时
//        String expiresHeader = HttpUtil.buildExpiresHeader(futureTime);
//
//        // 解析生成的时间
//        Date expiresDate = TEST_EXPIRES_FORMAT.parse(expiresHeader);
//        long expectedTime = System.currentTimeMillis() + futureTime;
//
//        // 允许1秒的时间差以容错
//        assertTrue("Time difference exceeds tolerance",
//            Math.abs(expiresDate.getTime() - expectedTime) < 1000);
//    }

    @Test
    public void testZeroFutureTime() throws ParseException {
        String expiresHeader = HttpUtil.buildExpiresHeader(0);
        Date expiresDate = TEST_EXPIRES_FORMAT.parse(expiresHeader);
        long currentTime = System.currentTimeMillis();
        
        assertTrue("Zero future time not accurate", 
            Math.abs(expiresDate.getTime() - currentTime) < 1000);
    }

    @Test
    public void testNegativeFutureTime() throws ParseException {
        String expiresHeader = HttpUtil.buildExpiresHeader(-3600000L);
        Date expiresDate = TEST_EXPIRES_FORMAT.parse(expiresHeader);
        long expectedTime = System.currentTimeMillis() - 3600000L;
        
        assertTrue("Negative future time not handled correctly",
            Math.abs(expiresDate.getTime() - expectedTime) < 1000);
    }

    @Test
    public void testDifferentTimeZonesHandling() throws ParseException {
        // 生成并解析时间以验证时区一致性
        String expiresHeader = HttpUtil.buildExpiresHeader(3600000L);
        Date parsedDate = TEST_EXPIRES_FORMAT.parse(expiresHeader);
        
        // 重新格式化为字符串以确保时区正确
        String reFormatted = TEST_EXPIRES_FORMAT.format(parsedDate);
        assertEquals("Timezone handling inconsistent", expiresHeader, reFormatted);
    }
}