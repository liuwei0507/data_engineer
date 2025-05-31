package cn.dw.flume.interceptor;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import org.apache.commons.codec.Charsets;
import org.apache.flume.Context;
import org.apache.flume.Event;
import org.apache.flume.event.SimpleEvent;
import org.apache.flume.interceptor.Interceptor;
import org.junit.Test;

import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class CustomInterceptor implements Interceptor {
    @Override
    public void initialize() {

    }

    @Override
    //逐条处理event
    public Event intercept(Event event) {
//        1、获取 event 的 header
        Map<String, String> headersMap = event.getHeaders();
//        2、获取 event 的 body
        String eventBody = new String(event.getBody(), Charsets.UTF_8);
//        3、解析body获取json串, 根据空格分割
        String[] bodyAddr = eventBody.split("\\s+");
        try {
            // json字符串在第六个
            String jsonStr = bodyAddr[6];
//        4、解析json串获 取时间戳
            JSONObject jsonObject = JSON.parseObject(jsonStr);
            String timestampStr = jsonObject.getJSONObject("app_active").getString("time");
//        5、将时间戳转换为字符串 "yyyy-MM-dd"
            long timestamp = Long.parseLong(timestampStr);
            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");
            Instant instant = Instant.ofEpochMilli(timestamp);
            LocalDateTime localDateTime = LocalDateTime.ofInstant(instant, ZoneId.systemDefault());
            String date = formatter.format(localDateTime);
//        6、将转换后的字符串放置header中
            headersMap.put("logtime", date);
            event.setHeaders(headersMap);
        } catch (Exception e) {
            headersMap.put("logtime", "Unknown");
            event.setHeaders(headersMap);
        }
//        7、返回event
        return event;
    }

    @Override
    public List<Event> intercept(List<Event> events) {
        List<Event> eventList = new ArrayList<>();
        for (Event event : events) {
            Event outEvent = intercept(event);
            if (outEvent != null) {
                eventList.add(outEvent);
            }
        }
        return eventList;
    }

    @Override
    public void close() {

    }

    public static class Builder implements Interceptor.Builder {
        @Override
        public Interceptor build() {
            return new CustomInterceptor();
        }

        @Override
        public void configure(Context context) {

        }
    }

    @Test
    public void testJUnit() {
        String logStr = "2021-09-16 16:55:01.209 [main] INFO  com.lagou.ecommerce.AppStart - {\"app_active\":{\"name\":\"app_active\",\"json\":{\"entry\":\"1\",\"action\":\"1\",\"error_code\":\"0\"},\"time\":1595260800000},\"attr\":{\"area\":\"溧阳\",\"uid\":\"2F10092A6\",\"app_v\":\"1.1.19\",\"event_type\":\"common\",\"device_id\":\"1FB872-9A1006\",\"os_type\":\"7.4\",\"channel\":\"AB\",\"language\":\"chinese\",\"brand\":\"Huawei-5\"}}";
//        new event
        SimpleEvent event = new SimpleEvent();
        Map<String, String> map = new HashMap<>();
        event.setHeaders(map);
        event.setBody(logStr.getBytes(Charsets.UTF_8));
        // 调用interceptor
        CustomInterceptor customInterceptor = new CustomInterceptor();
        Event outEvent = customInterceptor.intercept(event);
        Map<String, String> headers = outEvent.getHeaders();
        System.out.println(JSON.toJSONString(headers));


    }

}
