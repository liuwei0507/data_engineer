package cn.dw.flume.interceptor;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
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

public class LogTypeInterceptor implements Interceptor {
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
        // flume sink中必须包含timestamp header，否则会报错
        headersMap.putIfAbsent("timestamp", String.valueOf(System.currentTimeMillis()));
        try {
            // json字符串在第六个

            StringBuilder sb = new StringBuilder();
            for (int i = 6; i < bodyAddr.length; i++) {
                sb.append(bodyAddr[i]);
            }
            String jsonStr = sb.toString();
//        4、解析json串获 取时间戳
            JSONObject jsonObject = JSON.parseObject(jsonStr);
            String timestampStr = "";
            if (headersMap.getOrDefault("logtype", "").equals("start")) {
                //取启动日志的时间戳
                timestampStr = jsonObject.getJSONObject("app_active").getString("time");
            } else if (headersMap.getOrDefault("logtype", "").equals("event")) {
                // 取事件日志的时间戳
                JSONArray jsonArray = jsonObject.getJSONArray("lagou_event");
                if (jsonArray.size() > 0) {
                    timestampStr = jsonArray.getJSONObject(0).getString("time");
                }
            }
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
            return new LogTypeInterceptor();
        }

        @Override
        public void configure(Context context) {

        }
    }

    @Test
    public void testStartLog() {
        String logStr = "2021-09-16 16:55:01.209 [main] INFO  com.lagou.ecommerce.AppStart - {\"app_active\":{\"name\":\"app_active\",\"json\":{\"entry\":\"1\",\"action\":\"1\",\"error_code\":\"0\"},\"time\":1595260800000},\"attr\":{\"area\":\"溧阳\",\"uid\":\"2F10092A6\",\"app_v\":\"1.1.19\",\"event_type\":\"common\",\"device_id\":\"1FB872-9A1006\",\"os_type\":\"7.4\",\"channel\":\"AB\",\"language\":\"chinese\",\"brand\":\"Huawei-5\"}}";
//        new event
        SimpleEvent event = new SimpleEvent();
        Map<String, String> map = new HashMap<>();
        map.put("logtype", "start");
        event.setHeaders(map);
        event.setBody(logStr.getBytes(Charsets.UTF_8));
        // 调用interceptor
        LogTypeInterceptor customInterceptor = new LogTypeInterceptor();
        Event outEvent = customInterceptor.intercept(event);
        Map<String, String> headers = outEvent.getHeaders();
        System.out.println(JSON.toJSONString(headers));


    }

    @Test
    public void testEventLog() {
        String logStr = "2021-09-16 16:54:41.480 [main] INFO  com.lagou.ecommerce.AppEvent - {\"lagou_event\":[{\"name\":\"goods_detail_loading\",\"json\":{\"entry\":\"3\",\"goodsid\":\"0\",\"loading_time\":\"12\",\"action\":\"1\",\"staytime\":\"38\",\"showtype\":\"2\"},\"time\":1595260800000},{\"name\":\"loading\",\"json\":{\"loading_time\":\"21\",\"action\":\"1\",\"loading_type\":\"1\",\"type\":\"1\"},\"time\":1595260800000},{\"name\":\"ad\",\"json\":{\"duration\":\"26\",\"ad_action\":\"0\",\"shop_id\":\"0\",\"event_type\":\"ad\",\"ad_type\":\"4\",\"show_style\":\"0\",\"product_id\":\"9\",\"place\":\"placecampaign2_index\",\"sort\":\"8\"},\"time\":1595260800000},{\"name\":\"comment\",\"json\":{\"praise_count\":447,\"comment_id\":2,\"reply_count\":25,\"userid\":4,\"content\":\"Non voluptatem reprehenderit voluptatem velit minus non ut sunt et sed amet nemo vel sunt rem libero deserunt aliquam vitae dicta ut aut dolores molestiae fuga excepturi est occaecati.\"},\"time\":1595260800000},{\"name\":\"favorites\",\"json\":{\"course_id\":2,\"id\":0,\"userid\":0},\"time\":1595260800000}],\"attr\":{\"area\":\"扬州\",\"uid\":\"2F10092A0\",\"app_v\":\"1.1.6\",\"event_type\":\"common\",\"device_id\":\"1FB872-9A1000\",\"os_type\":\"4.71\",\"channel\":\"MJ\",\"language\":\"chinese\",\"brand\":\"Huawei-8\"}}";
//        new event
        SimpleEvent event = new SimpleEvent();
        Map<String, String> map = new HashMap<>();
        map.put("logtype", "event");
        event.setHeaders(map);
        event.setBody(logStr.getBytes(Charsets.UTF_8));
        // 调用interceptor
        LogTypeInterceptor customInterceptor = new LogTypeInterceptor();
        Event outEvent = customInterceptor.intercept(event);
        Map<String, String> headers = outEvent.getHeaders();
        System.out.println(JSON.toJSONString(headers));


    }

}